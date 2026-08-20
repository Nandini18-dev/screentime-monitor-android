package com.nandini.screentime.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "daily_app_usage", primaryKeys = {"date", "packageName"})
public class DailyAppUsage {

    @NonNull
    public String date; // yyyy-MM-dd

    @NonNull
    public String packageName;

    public long foregroundTimeMs;

    public long lastUpdated;

    public DailyAppUsage(@NonNull String date, @NonNull String packageName, long foregroundTimeMs, long lastUpdated) {
        this.date = date;
        this.packageName = packageName;
        this.foregroundTimeMs = foregroundTimeMs;
        this.lastUpdated = lastUpdated;
    }
}
