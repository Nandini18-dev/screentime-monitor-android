package com.nandini.screentime.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.nandini.screentime.util.PermissionUtils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;
        if (!PermissionUtils.hasCoreMonitoringPermissions(context)) return;

        Intent serviceIntent = new Intent(context, MonitoringForegroundService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}
