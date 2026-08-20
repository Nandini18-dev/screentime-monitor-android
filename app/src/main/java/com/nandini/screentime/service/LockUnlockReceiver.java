package com.nandini.screentime.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.nandini.screentime.data.repository.EventRepository;

/**
 * Must be registered dynamically (SCREEN_OFF is an implicit broadcast and cannot be
 * declared in the manifest since API 26). Owned by MonitoringForegroundService.
 *
 * Only handles the lock side (SCREEN_OFF, which is instant and reliably delivered).
 * The unlock side is intentionally NOT handled via ACTION_USER_PRESENT or a delay
 * after SCREEN_ON: testing showed USER_PRESENT can be silently dropped by the system's
 * broadcast dispatcher for context-registered receivers, and a fixed post-SCREEN_ON
 * delay races against variable-length PIN/pattern entry. See LockStateTracker: the
 * unlock is instead detected by MonitoringForegroundService's periodic poll checking
 * KeyguardManager once this receiver marks the device as locked.
 */
public class LockUnlockReceiver extends BroadcastReceiver {

    private final EventRepository eventRepository;

    public LockUnlockReceiver(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public static IntentFilter intentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        return filter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) return;
        eventRepository.recordLock(System.currentTimeMillis());
        LockStateTracker.markLocked();
    }
}
