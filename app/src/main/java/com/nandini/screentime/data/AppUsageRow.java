package com.nandini.screentime.data;

/** Merged per-app row for "today": screen time + Wi-Fi bytes, keyed by package name. */
public class AppUsageRow {
    public final String packageName;
    public final long foregroundTimeMs;
    public final long wifiBytes;

    public AppUsageRow(String packageName, long foregroundTimeMs, long wifiBytes) {
        this.packageName = packageName;
        this.foregroundTimeMs = foregroundTimeMs;
        this.wifiBytes = wifiBytes;
    }
}
