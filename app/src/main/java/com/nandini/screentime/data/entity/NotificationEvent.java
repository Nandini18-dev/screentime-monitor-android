package com.nandini.screentime.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification_event")
public class NotificationEvent {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long timestamp;

    @NonNull
    public String dateKey; // yyyy-MM-dd

    @NonNull
    public String packageName;

    public NotificationEvent(long timestamp, @NonNull String dateKey, @NonNull String packageName) {
        this.timestamp = timestamp;
        this.dateKey = dateKey;
        this.packageName = packageName;
    }
}
