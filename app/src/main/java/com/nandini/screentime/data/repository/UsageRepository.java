package com.nandini.screentime.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.nandini.screentime.data.AppDatabase;
import com.nandini.screentime.data.DateTotal;
import com.nandini.screentime.data.PackageTotal;
import com.nandini.screentime.data.dao.UsageDao;
import com.nandini.screentime.data.entity.DailyAppUsage;
import com.nandini.screentime.util.AppExecutors;
import com.nandini.screentime.util.TimeUtils;
import com.nandini.screentime.util.UsageStatsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsageRepository {

    private final Context appContext;
    private final UsageDao dao;

    public UsageRepository(Context context) {
        this.appContext = context.getApplicationContext();
        this.dao = AppDatabase.getInstance(appContext).usageDao();
    }

    /** Re-reads UsageStatsManager for "today so far" and upserts absolute per-app totals. */
    public void refreshToday() {
        AppExecutors.getInstance().background().execute(() -> {
            Map<String, Long> perApp = UsageStatsHelper.getForegroundTimePerAppToday(appContext);
            String today = TimeUtils.todayKey();
            long now = System.currentTimeMillis();

            List<DailyAppUsage> rows = new ArrayList<>(perApp.size());
            for (Map.Entry<String, Long> entry : perApp.entrySet()) {
                rows.add(new DailyAppUsage(today, entry.getKey(), entry.getValue(), now));
            }

            AppExecutors.getInstance().diskIo().execute(() -> dao.upsertAll(rows));
        });
    }

    public LiveData<List<DailyAppUsage>> getForDate(String date) {
        return dao.getForDate(date);
    }

    public LiveData<Long> getTotalForDate(String date) {
        return dao.getTotalForDate(date);
    }

    public LiveData<Long> getTotalForApp(String date, String packageName) {
        return dao.getTotalForApp(date, packageName);
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
