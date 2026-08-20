package com.nandini.screentime.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.nandini.screentime.data.repository.EventRepository;
import com.nandini.screentime.data.repository.NetworkUsageRepository;
import com.nandini.screentime.data.repository.UsageRepository;
import com.nandini.screentime.databinding.FragmentSettingsBinding;
import com.nandini.screentime.worker.DailyRolloverWorker;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireContext().getSharedPreferences(
                DailyRolloverWorker.PREFS_NAME, Context.MODE_PRIVATE);
        int retentionDays = prefs.getInt(DailyRolloverWorker.KEY_RETENTION_DAYS,
                DailyRolloverWorker.DEFAULT_RETENTION_DAYS);
        binding.editRetentionDays.setText(String.valueOf(retentionDays));

        binding.btnSaveRetention.setOnClickListener(v -> {
            String text = binding.editRetentionDays.getText().toString();
            if (text.isEmpty()) return;
            int days = Integer.parseInt(text);
            prefs.edit().putInt(DailyRolloverWorker.KEY_RETENTION_DAYS, days).apply();
            Toast.makeText(requireContext(), "Retention set to " + days + " days", Toast.LENGTH_SHORT).show();
        });

        binding.btnReviewPermissions.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), OnboardingActivity.class)));

        binding.btnResetData.setOnClickListener(v -> confirmReset());
    }

    private void confirmReset() {
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(com.nandini.screentime.R.string.settings_reset))
                .setMessage("This deletes all recorded screen time, Wi-Fi usage, lock/unlock, and notification history. This cannot be undone.")
                .setPositiveButton("Reset", (dialog, which) -> resetAllData())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void resetAllData() {
        new UsageRepository(requireContext().getApplicationContext()).resetAll();
        new NetworkUsageRepository(requireContext().getApplicationContext()).resetAll();
        new EventRepository(requireContext().getApplicationContext()).resetAll();
        Toast.makeText(requireContext(), "All data reset", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
