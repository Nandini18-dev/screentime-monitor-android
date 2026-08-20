package com.nandini.screentime.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "daily_network_usage", primaryKeys = {"date", "packageName"})
public class DailyNetworkUsage {

    @NonNull
    public String date; // yyyy-MM-dd

    @NonNull
    public String packageName;

    public long rxBytes;
    public long txBytes;
    public long lastUpdated;

    public DailyNetworkUsage(@NonNull String date, @NonNull String packageName, long rxBytes, long txBytes, long lastUpdated) {
        this.date = date;
        this.packageName = packageName;
        this.rxBytes = rxBytes;
        this.txBytes = txBytes;
        this.lastUpdated = lastUpdated;
    }
}
