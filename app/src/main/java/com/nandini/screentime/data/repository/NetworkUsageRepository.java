package com.nandini.screentime.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.nandini.screentime.data.AppDatabase;
import com.nandini.screentime.data.DateTotal;
import com.nandini.screentime.data.PackageTotal;
import com.nandini.screentime.data.dao.NetworkUsageDao;
import com.nandini.screentime.data.entity.DailyNetworkUsage;
import com.nandini.screentime.util.AppExecutors;
import com.nandini.screentime.util.NetworkStatsHelper;
import com.nandini.screentime.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetworkUsageRepository {

    private final Context appContext;
    private final NetworkUsageDao dao;

    public NetworkUsageRepository(Context context) {
        this.appContext = context.getApplicationContext();
        this.dao = AppDatabase.getInstance(appContext).networkUsageDao();
    }

    /** Re-reads NetworkStatsManager for "today so far" and upserts absolute per-app Wi-Fi totals. */
    public void refreshToday() {
        AppExecutors.getInstance().background().execute(() -> {
            Map<String, long[]> perApp = NetworkStatsHelper.getWifiUsagePerAppToday(appContext);
            String today = TimeUtils.todayKey();
            long now = System.currentTimeMillis();

            List<DailyNetworkUsage> rows = new ArrayList<>(perApp.size());
            for (Map.Entry<String, long[]> entry : perApp.entrySet()) {
                long[] rxTx = entry.getValue();
                rows.add(new DailyNetworkUsage(today, entry.getKey(), rxTx[0], rxTx[1], now));
            }

            AppExecutors.getInstance().diskIo().execute(() -> dao.upsertAll(rows));
        });
    }

    public LiveData<List<DailyNetworkUsage>> getForDate(String date) {
        return dao.getForDate(date);
    }

    public LiveData<Long> getTotalBytesForDate(String date) {
        return dao.getTotalBytesForDate(date);
    }

    public LiveData<Long> getTotalBytesForApp(String date, String packageName) {
        return dao.getTotalBytesForApp(date, packageName);
    }

    public LiveData<List<PackageTotal>> getTopAppsForDate(String date, int limit) {
        return dao.getTopAppsForDate(date, limit);
    }

    public LiveData<List<DateTotal>> getDailyTotals(String startDate, String endDate) {
        return dao.getDailyTotals(startDate, endDate);
    }

    public void deleteOlderThan(String cutoffDate) {
        AppExecutors.getInstance().diskIo().execute(() -> dao.deleteOlderThan(cutoffDate));
    }

    public void resetAll() {
        AppExecutors.getInstance().diskIo().execute(dao::deleteAll);
    }
}
