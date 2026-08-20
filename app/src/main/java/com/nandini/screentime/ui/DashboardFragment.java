package com.nandini.screentime.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nandini.screentime.R;
import com.nandini.screentime.databinding.FragmentDashboardBinding;
import com.nandini.screentime.util.TimeUtils;
import com.nandini.screentime.viewmodel.DashboardViewModel;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding.cardScreenTime.txtStatLabel.setText(R.string.today_screen_time);
        binding.cardWifi.txtStatLabel.setText(R.string.today_wifi_usage);
        binding.cardLocks.txtStatLabel.setText(R.string.today_locks);
        binding.cardUnlocks.txtStatLabel.setText(R.string.today_unlocks);
        binding.cardNotifications.txtStatLabel.setText(R.string.today_notifications);

        PackageTotalAdapter topAppsAdapter = new PackageTotalAdapter(
                requireContext(), PackageTotalAdapter.ValueType.DURATION);
        binding.recyclerTopApps.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerTopApps.setAdapter(topAppsAdapter);

        viewModel.getScreenTimeMs().observe(getViewLifecycleOwner(), ms ->
                binding.cardScreenTime.txtStatValue.setText(TimeUtils.formatDurationShort(ms == null ? 0 : ms)));

        viewModel.getWifiBytes().observe(getViewLifecycleOwner(), bytes ->
                binding.cardWifi.txtStatValue.setText(TimeUtils.formatBytesShort(bytes == null ? 0 : bytes)));

        viewModel.getLocks().observe(getViewLifecycleOwner(), count ->
                binding.cardLocks.txtStatValue.setText(String.valueOf(count == null ? 0 : count)));

        viewModel.getUnlocks().observe(getViewLifecycleOwner(), count ->
                binding.cardUnlocks.txtStatValue.setText(String.valueOf(count == null ? 0 : count)));

        viewModel.getNotifications().observe(getViewLifecycleOwner(), count ->
                binding.cardNotifications.txtStatValue.setText(String.valueOf(count == null ? 0 : count)));

        viewModel.getTopApps().observe(getViewLifecycleOwner(), topAppsAdapter::submitList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
