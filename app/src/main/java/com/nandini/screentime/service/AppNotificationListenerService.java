package com.nandini.screentime.service;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.nandini.screentime.data.repository.EventRepository;

public class AppNotificationListenerService extends NotificationListenerService {

    private EventRepository eventRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        eventRepository = new EventRepository(getApplicationContext());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null) return;
        // Ignore our own foreground-service status notification so its updates
        // don't inflate the count.
        if (getPackageName().equals(sbn.getPackageName())) return;

        eventRepository.recordNotification(sbn.getPostTime(), sbn.getPackageName());
    }
}
