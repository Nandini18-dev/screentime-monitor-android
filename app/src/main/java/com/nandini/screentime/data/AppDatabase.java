package com.nandini.screentime.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nandini.screentime.data.dao.LockEventDao;
import com.nandini.screentime.data.dao.NetworkUsageDao;
import com.nandini.screentime.data.dao.NotificationEventDao;
import com.nandini.screentime.data.dao.UsageDao;
import com.nandini.screentime.data.entity.DailyAppUsage;
import com.nandini.screentime.data.entity.DailyNetworkUsage;
import com.nandini.screentime.data.entity.LockUnlockEvent;
import com.nandini.screentime.data.entity.NotificationEvent;

@Database(
        entities = {
                DailyAppUsage.class,
                DailyNetworkUsage.class,
                LockUnlockEvent.class,
                NotificationEvent.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract UsageDao usageDao();
    public abstract NetworkUsageDao networkUsageDao();
    public abstract LockEventDao lockEventDao();
    public abstract NotificationEventDao notificationEventDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "screentime.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
