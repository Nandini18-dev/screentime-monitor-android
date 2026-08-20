package com.nandini.screentime.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.nandini.screentime.data.AppDatabase;
import com.nandini.screentime.data.PackageTotal;
import com.nandini.screentime.data.dao.LockEventDao;
import com.nandini.screentime.data.dao.NotificationEventDao;
import com.nandini.screentime.data.entity.LockUnlockEvent;
import com.nandini.screentime.data.entity.NotificationEvent;
import com.nandini.screentime.util.AppExecutors;
import com.nandini.screentime.util.TimeUtils;

import java.util.List;

public class EventRepository {

    public static final String TYPE_LOCK = "LOCK";
    public static final String TYPE_UNLOCK = "UNLOCK";

    private final LockEventDao lockDao;
    private final NotificationEventDao notificationDao;

    public EventRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        this.lockDao = db.lockEventDao();
        this.notificationDao = db.notificationEventDao();
    }

    public void recordLock(long timestamp) {
        recordLockEvent(timestamp, TYPE_LOCK);
    }

    public void recordUnlock(long timestamp) {
        recordLockEvent(timestamp, TYPE_UNLOCK);
    }

    private void recordLockEvent(long timestamp, String type) {
        AppExecutors.getInstance().diskIo().execute(() ->
                lockDao.insert(new LockUnlockEvent(timestamp, TimeUtils.dateKey(timestamp), type)));
    }

    public void recordNotification(long timestamp, String packageName) {
        AppExecutors.getInstance().diskIo().execute(() ->
                notificationDao.insert(new NotificationEvent(timestamp, TimeUtils.dateKey(timestamp), packageName)));
    }

    public LiveData<Integer> countForDateAndType(String date, String type) {
        return lockDao.countForDateAndType(date, type);
    }

    public LiveData<Integer> countNotificationsForDate(String date) {
        return notificationDao.countForDate(date);
    }

    public LiveData<List<PackageTotal>> getTopNotificationPackagesForDate(String date, int limit) {
        return notificationDao.getTopPackagesForDate(date, limit);
    }

    public void deleteOlderThan(String cutoffDate) {
        AppExecutors.getInstance().diskIo().execute(() -> {
            lockDao.deleteOlderThan(cutoffDate);
            notificationDao.deleteOlderThan(cutoffDate);
        });
    }

    public void resetAll() {
        AppExecutors.getInstance().diskIo().execute(() -> {
            lockDao.deleteAll();
            notificationDao.deleteAll();
        });
    }
}
