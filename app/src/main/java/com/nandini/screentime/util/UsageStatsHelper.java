package com.nandini.screentime.util;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Computes per-app foreground time for "today so far" by replaying raw UsageEvents
 * and pairing MOVE_TO_FOREGROUND/MOVE_TO_BACKGROUND events per package. This is more
 * accurate than UsageStatsManager#queryUsageStats aggregates, which can double count
 * across overlapping buckets.
 */
public final class UsageStatsHelper {

    private UsageStatsHelper() {
    }

    public static Map<String, Long> getForegroundTimePerAppToday(Context context) {
        long start = TimeUtils.startOfTodayMillis();
        long end = System.currentTimeMillis();
        return getForegroundTimePerApp(context, start, end);
    }

    public static Map<String, Long> getForegroundTimePerApp(Context context, long startMs, long endMs) {
        Map<String, Long> totals = new HashMap<>();
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usm == null) return totals;

        UsageEvents events = usm.queryEvents(startMs, endMs);
        Map<String, Long> openSince = new HashMap<>();
        UsageEvents.Event event = new UsageEvents.Event();

        while (events.hasNextEvent()) {
            events.getNextEvent(event);
            String pkg = event.getPackageName();
            if (pkg == null) continue;

            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                openSince.put(pkg, event.getTimeStamp());
            } else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                Long since = openSince.remove(pkg);
                if (since != null && event.getTimeStamp() > since) {
                    totals.merge(pkg, event.getTimeStamp() - since, Long::sum);
                }
            }
        }

        // Anything still "open" at query time has been in the foreground continuously since.
        for (Map.Entry<String, Long> entry : openSince.entrySet()) {
            long since = entry.getValue();
            if (endMs > since) {
                totals.merge(entry.getKey(), endMs - since, Long::sum);
            }
        }

        return totals;
    }
}
