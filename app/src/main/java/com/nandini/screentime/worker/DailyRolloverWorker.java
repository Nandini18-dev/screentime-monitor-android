package com.nandini.screentime.worker;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.nandini.screentime.data.repository.EventRepository;
import com.nandini.screentime.data.repository.NetworkUsageRepository;
import com.nandini.screentime.data.repository.UsageRepository;
import com.nandini.screentime.util.TimeUtils;

/** Daily periodic job: enforces the configured retention window by deleting old rows. */
public class DailyRolloverWorker extends Worker {

    public static final String PREFS_NAME = "screentime_settings";
    public static final String KEY_RETENTION_DAYS = "retention_days";
    public static final int DEFAULT_RETENTION_DAYS = 30;

    public DailyRolloverWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int retentionDays = prefs.getInt(KEY_RETENTION_DAYS, DEFAULT_RETENTION_DAYS);
        String cutoffDate = TimeUtils.dateKeyDaysAgo(retentionDays);

        new UsageRepository(context).deleteOlderThan(cutoffDate);
        new NetworkUsageRepository(context).deleteOlderThan(cutoffDate);
        new EventRepository(context).deleteOlderThan(cutoffDate);

        return Result.success();
    }
}
