package com.nandini.screentime.service;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.nandini.screentime.R;
import com.nandini.screentime.data.repository.EventRepository;
import com.nandini.screentime.data.repository.NetworkUsageRepository;
import com.nandini.screentime.data.repository.UsageRepository;
import com.nandini.screentime.ui.MainActivity;

/**
 * Long-running component that owns the two push-based listeners' lifecycle context
 * (the lock/unlock receiver is registered here) and periodically polls
 * UsageStatsManager/NetworkStatsManager, since those two have no push API.
 * Writes go straight to Room; the UI observes Room's LiveData, so no separate
 * in-process pub/sub is needed for "real-time" updates.
 */
public class MonitoringForegroundService extends Service {

    private static final String CHANNEL_ID = "monitoring_status";
    private static final int NOTIFICATION_ID = 1001;
    private static final long STATS_POLL_INTERVAL_MS = 10_000L;
    private static final long KEYGUARD_POLL_INTERVAL_MS = 1_500L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private LockUnlockReceiver lockUnlockReceiver;
    private UsageRepository usageRepository;
    private NetworkUsageRepository networkUsageRepository;
    private EventRepository eventRepository;
    private KeyguardManager keyguardManager;

    private final Runnable statsPollTask = new Runnable() {
        @Override
        public void run() {
            usageRepository.refreshToday();
            networkUsageRepository.refreshToday();
            handler.postDelayed(this, STATS_POLL_INTERVAL_MS);
        }
    };

    private final Runnable keyguardPollTask = new Runnable() {
        @Override
        public void run() {
            boolean locked = keyguardManager != null && keyguardManager.isKeyguardLocked();
            if (LockStateTracker.consumeUnlockIfLocked(locked)) {
                eventRepository.recordUnlock(System.currentTimeMillis());
            }
            handler.postDelayed(this, KEYGUARD_POLL_INTERVAL_MS);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        eventRepository = new EventRepository(this);
        usageRepository = new UsageRepository(this);
        networkUsageRepository = new NetworkUsageRepository(this);
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        lockUnlockReceiver = new LockUnlockReceiver(eventRepository);
        ContextCompat.registerReceiver(this, lockUnlockReceiver, LockUnlockReceiver.intentFilter(),
                ContextCompat.RECEIVER_NOT_EXPORTED);

        startForeground(NOTIFICATION_ID, buildNotification());
        handler.post(statsPollTask);
        handler.post(keyguardPollTask);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(statsPollTask);
        handler.removeCallbacks(keyguardPollTask);
        if (lockUnlockReceiver != null) {
            unregisterReceiver(lockUnlockReceiver);
        }
        super.onDestroy();
    }

    private Notification buildNotification() {
        createChannelIfNeeded();

        Intent openApp = new Intent(this, MainActivity.class);
        android.app.PendingIntent contentIntent = android.app.PendingIntent.getActivity(
                this, 0, openApp,
                android.app.PendingIntent.FLAG_IMMUTABLE | android.app.PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.monitoring_notification_title))
                .setContentText(getString(R.string.monitoring_notification_text))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true)
                .setContentIntent(contentIntent)
                .build();
    }

    private void createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager == null) return;
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.monitoring_notification_channel_name),
                NotificationManager.IMPORTANCE_MIN);
        manager.createNotificationChannel(channel);
    }
}
