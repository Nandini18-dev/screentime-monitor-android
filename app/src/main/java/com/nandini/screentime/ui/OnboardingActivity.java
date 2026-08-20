package com.nandini.screentime.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.nandini.screentime.databinding.ActivityOnboardingBinding;
import com.nandini.screentime.service.MonitoringForegroundService;
import com.nandini.screentime.util.PermissionUtils;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private ActivityResultLauncher<String> postNotificationsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        postNotificationsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> refreshStatus());

        binding.btnUsageGrant.setOnClickListener(v -> PermissionUtils.openUsageAccessSettings(this));
        binding.btnNotificationGrant.setOnClickListener(v -> PermissionUtils.openNotificationListenerSettings(this));
        binding.btnPostNotifGrant.setOnClickListener(v -> requestPostNotifications());
        binding.btnBatteryGrant.setOnClickListener(v -> PermissionUtils.requestIgnoreBatteryOptimizations(this));
        binding.btnContinue.setOnClickListener(v -> finishOnboarding());

        // If everything is already granted (e.g. re-launch after being fully set up), skip straight in.
        if (PermissionUtils.hasCoreMonitoringPermissions(this)) {
            finishOnboarding();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void requestPostNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            postNotificationsLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void refreshStatus() {
        boolean usageGranted = PermissionUtils.hasUsageAccess(this);
        boolean notifListenerGranted = PermissionUtils.hasNotificationListenerAccess(this);
        boolean postNotifGranted = PermissionUtils.hasPostNotificationsPermission(this);
        boolean batteryGranted = PermissionUtils.isIgnoringBatteryOptimizations(this);

        setGranted(binding.btnUsageGrant, usageGranted);
        setGranted(binding.btnNotificationGrant, notifListenerGranted);
        setGranted(binding.btnPostNotifGrant, postNotifGranted);
        setGranted(binding.btnBatteryGrant, batteryGranted);

        binding.btnContinue.setEnabled(usageGranted && notifListenerGranted && postNotifGranted);
    }

    private void setGranted(android.widget.Button button, boolean granted) {
        button.setEnabled(!granted);
        button.setText(granted ? com.nandini.screentime.R.string.granted : com.nandini.screentime.R.string.grant);
    }

    private void finishOnboarding() {
        Intent serviceIntent = new Intent(this, MonitoringForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
