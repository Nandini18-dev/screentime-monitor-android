package com.nandini.screentime.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nandini.screentime.data.PackageTotal;
import com.nandini.screentime.data.repository.EventRepository;
import com.nandini.screentime.data.repository.NetworkUsageRepository;
import com.nandini.screentime.data.repository.UsageRepository;
import com.nandini.screentime.util.TimeUtils;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private static final int TOP_APPS_LIMIT = 5;

    private final LiveData<Long> screenTimeMs;
    private final LiveData<Long> wifiBytes;
    private final LiveData<Integer> locks;
    private final LiveData<Integer> unlocks;
    private final LiveData<Integer> notifications;
    private final LiveData<List<PackageTotal>> topApps;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        UsageRepository usageRepository = new UsageRepository(application);
        NetworkUsageRepository networkUsageRepository = new NetworkUsageRepository(application);
        EventRepository eventRepository = new EventRepository(application);

        String today = TimeUtils.todayKey();
        screenTimeMs = usageRepository.getTotalForDate(today);
        wifiBytes = networkUsageRepository.getTotalBytesForDate(today);
        locks = eventRepository.countForDateAndType(today, EventRepository.TYPE_LOCK);
        unlocks = eventRepository.countForDateAndType(today, EventRepository.TYPE_UNLOCK);
        notifications = eventRepository.countNotificationsForDate(today);
        topApps = usageRepository.getTopAppsForDate(today, TOP_APPS_LIMIT);
    }

    public LiveData<Long> getScreenTimeMs() {
        return screenTimeMs;
    }

    public LiveData<Long> getWifiBytes() {
        return wifiBytes;
    }

    public LiveData<Integer> getLocks() {
        return locks;
    }

    public LiveData<Integer> getUnlocks() {
        return unlocks;
    }

    public LiveData<Integer> getNotifications() {
        return notifications;
    }

    public LiveData<List<PackageTotal>> getTopApps() {
        return topApps;
    }
}
