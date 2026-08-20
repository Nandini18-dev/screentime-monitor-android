package com.nandini.screentime.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class TimeUtils {

    private static final ThreadLocal<SimpleDateFormat> DATE_KEY_FORMAT =
            ThreadLocal.withInitial(() -> {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                fmt.setTimeZone(TimeZone.getDefault());
                return fmt;
            });

    private TimeUtils() {
    }

    public static String todayKey() {
        return dateKey(System.currentTimeMillis());
    }

    public static String dateKey(long timestampMs) {
        return DATE_KEY_FORMAT.get().format(new Date(timestampMs));
    }

    /** Start-of-day epoch millis for "today", in the device's default timezone. */
    public static long startOfTodayMillis() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static String dateKeyDaysAgo(int daysAgo) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo);
        return DATE_KEY_FORMAT.get().format(cal.getTime());
    }

    public static String formatDurationShort(long millis) {
        long totalMinutes = millis / 60000;
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        return minutes + "m";
    }

    public static String formatBytesShort(long bytes) {
        double kb = bytes / 1024.0;
        if (kb < 1024) {
            return String.format(Locale.US, "%.0f KB", kb);
        }
        double mb = kb / 1024.0;
        if (mb < 1024) {
            return String.format(Locale.US, "%.1f MB", mb);
        }
        double gb = mb / 1024.0;
        return String.format(Locale.US, "%.2f GB", gb);
    }
}
