package com.nandini.screentime.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nandini.screentime.data.DateTotal;
import com.nandini.screentime.data.repository.NetworkUsageRepository;
import com.nandini.screentime.data.repository.UsageRepository;
import com.nandini.screentime.util.TimeUtils;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private static final int HISTORY_DAYS = 7;

    private final LiveData<List<DateTotal>> dailyScreenTime;
    private final LiveData<List<DateTotal>> dailyWifi;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        String startDate = TimeUtils.dateKeyDaysAgo(HISTORY_DAYS - 1);
        String endDate = TimeUtils.todayKey();

        dailyScreenTime = new UsageRepository(application).getDailyTotals(startDate, endDate);
        dailyWifi = new NetworkUsageRepository(application).getDailyTotals(startDate, endDate);
    }

    public LiveData<List<DateTotal>> getDailyScreenTime() {
        return dailyScreenTime;
    }

    public LiveData<List<DateTotal>> getDailyWifi() {
        return dailyWifi;
    }
}
