package com.nandini.screentime.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.nandini.screentime.data.AppUsageRow;
import com.nandini.screentime.data.entity.DailyAppUsage;
import com.nandini.screentime.data.entity.DailyNetworkUsage;
import com.nandini.screentime.data.repository.NetworkUsageRepository;
import com.nandini.screentime.data.repository.UsageRepository;
import com.nandini.screentime.util.TimeUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Merges today's per-app screen time and Wi-Fi usage into one sorted list for the Apps tab. */
public class AppListViewModel extends AndroidViewModel {

    private final MediatorLiveData<List<AppUsageRow>> mergedRows = new MediatorLiveData<>();

    private List<DailyAppUsage> latestUsage = new ArrayList<>();
    private List<DailyNetworkUsage> latestNetwork = new ArrayList<>();

    public AppListViewModel(@NonNull Application application) {
        super(application);
        String today = TimeUtils.todayKey();

        UsageRepository usageRepository = new UsageRepository(application);
        NetworkUsageRepository networkUsageRepository = new NetworkUsageRepository(application);

        LiveData<List<DailyAppUsage>> usageLiveData = usageRepository.getForDate(today);
        LiveData<List<DailyNetworkUsage>> networkLiveData = networkUsageRepository.getForDate(today);

        mergedRows.addSource(usageLiveData, rows -> {
            latestUsage = rows != null ? rows : new ArrayList<>();
            recompute();
        });
        mergedRows.addSource(networkLiveData, rows -> {
            latestNetwork = rows != null ? rows : new ArrayList<>();
            recompute();
        });
    }

    private void recompute() {
        Map<String, Long> screenTimeByPkg = new HashMap<>();
        for (DailyAppUsage row : latestUsage) {
            screenTimeByPkg.put(row.packageName, row.foregroundTimeMs);
        }
        Map<String, Long> wifiByPkg = new HashMap<>();
        for (DailyNetworkUsage row : latestNetwork) {
            wifiByPkg.put(row.packageName, row.rxBytes + row.txBytes);
        }

        Map<String, long[]> combined = new HashMap<>();
        for (Map.Entry<String, Long> entry : screenTimeByPkg.entrySet()) {
            combined.computeIfAbsent(entry.getKey(), k -> new long[2])[0] = entry.getValue();
        }
        for (Map.Entry<String, Long> entry : wifiByPkg.entrySet()) {
            combined.computeIfAbsent(entry.getKey(), k -> new long[2])[1] = entry.getValue();
        }

        List<AppUsageRow> result = new ArrayList<>(combined.size());
        for (Map.Entry<String, long[]> entry : combined.entrySet()) {
            result.add(new AppUsageRow(entry.getKey(), entry.getValue()[0], entry.getValue()[1]));
        }
        result.sort(Comparator.<AppUsageRow>comparingLong(r -> r.foregroundTimeMs).reversed());

        mergedRows.setValue(result);
    }

    public LiveData<List<AppUsageRow>> getMergedRows() {
        return mergedRows;
    }
}
