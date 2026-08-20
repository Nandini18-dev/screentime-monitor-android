package com.nandini.screentime.service;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Shared flag between LockUnlockReceiver (sets it on SCREEN_OFF) and
 * MonitoringForegroundService's poll loop (clears it once KeyguardManager reports
 * the device is unlocked again, recording the unlock at that point).
 *
 * Unlock detection deliberately does not rely on ACTION_USER_PRESENT or a fixed
 * delay after SCREEN_ON: USER_PRESENT delivery to context-registered receivers proved
 * unreliable in testing, and a fixed delay races against variable-length PIN/pattern
 * entry. Reconciling against KeyguardManager on the existing poll cadence is slower
 * (up to one poll interval) but correct regardless of how long unlocking takes.
 */
final class LockStateTracker {

    private static final AtomicBoolean awaitingUnlock = new AtomicBoolean(false);

    private LockStateTracker() {
    }

    static void markLocked() {
        awaitingUnlock.set(true);
    }

    /** Returns true (once) if the device was locked and is now unlocked. */
    static boolean consumeUnlockIfLocked(boolean keyguardCurrentlyLocked) {
        if (!keyguardCurrentlyLocked) {
            return awaitingUnlock.compareAndSet(true, false);
        }
        return false;
    }
}
