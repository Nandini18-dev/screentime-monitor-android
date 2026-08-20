package com.nandini.screentime.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lock_unlock_event")
public class LockUnlockEvent {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long timestamp;

    @NonNull
    public String dateKey; // yyyy-MM-dd

    @NonNull
    public String type; // "LOCK" or "UNLOCK"

    public LockUnlockEvent(long timestamp, @NonNull String dateKey, @NonNull String type) {
        this.timestamp = timestamp;
        this.dateKey = dateKey;
        this.type = type;
    }
}
