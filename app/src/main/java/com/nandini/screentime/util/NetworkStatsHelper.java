package com.nandini.screentime.util;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Reads Wi-Fi data usage (device total and per-app) via NetworkStatsManager.
 * Requires the same PACKAGE_USAGE_STATS special access as UsageStatsHelper.
 */
public final class NetworkStatsHelper {

    private static final String TAG = "NetworkStatsHelper";

    private NetworkStatsHelper() {
    }

    /** package name (or "uid:<n>" if unresolvable) -> {rxBytes, txBytes} */
    public static Map<String, long[]> getWifiUsagePerAppToday(Context context) {
        Map<String, long[]> result = new HashMap<>();
        NetworkStatsManager nsm = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
        if (nsm == null) return result;

        long start = TimeUtils.startOfTodayMillis();
        long end = System.currentTimeMillis();

        try (NetworkStats stats = nsm.querySummary(ConnectivityManager.TYPE_WIFI, null, start, end)) {
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            Map<Integer, long[]> byUid = new HashMap<>();
            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket);
                int uid = bucket.getUid();
                long[] agg = byUid.computeIfAbsent(uid, k -> new long[2]);
                agg[0] += bucket.getRxBytes();
                agg[1] += bucket.getTxBytes();
            }

            PackageManager pm = context.getPackageManager();
            for (Map.Entry<Integer, long[]> entry : byUid.entrySet()) {
                String pkg = resolvePackageForUid(pm, entry.getKey());
                long[] existing = result.computeIfAbsent(pkg, k -> new long[2]);
                existing[0] += entry.getValue()[0];
                existing[1] += entry.getValue()[1];
            }
        } catch (RemoteException | SecurityException e) {
            Log.w(TAG, "Failed to query per-app Wi-Fi usage", e);
        }

        return result;
    }

    /** {rxBytes, txBytes} for the whole device, today so far. */
    public static long[] getWifiTotalBytesToday(Context context) {
        NetworkStatsManager nsm = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
        if (nsm == null) return new long[]{0, 0};

        long start = TimeUtils.startOfTodayMillis();
        long end = System.currentTimeMillis();

        try {
            NetworkStats.Bucket bucket = nsm.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, null, start, end);
            return new long[]{bucket.getRxBytes(), bucket.getTxBytes()};
        } catch (RemoteException | SecurityException e) {
            Log.w(TAG, "Failed to query total Wi-Fi usage", e);
            return new long[]{0, 0};
        }
    }

    private static String resolvePackageForUid(PackageManager pm, int uid) {
        String[] packages = pm.getPackagesForUid(uid);
        if (packages != null && packages.length > 0) {
            return packages[0];
        }
        return "uid:" + uid;
    }
}
