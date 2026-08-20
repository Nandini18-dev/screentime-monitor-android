package com.nandini.screentime.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.nandini.screentime.R;
import com.nandini.screentime.databinding.FragmentAppDetailBinding;
import com.nandini.screentime.util.AppInfoUtils;
import com.nandini.screentime.util.TimeUtils;
import com.nandini.screentime.viewmodel.AppDetailViewModel;

public class AppDetailFragment extends Fragment {

    private static final String ARG_PACKAGE_NAME = "package_name";

    private FragmentAppDetailBinding binding;

    public static AppDetailFragment newInstance(String packageName) {
        AppDetailFragment fragment = new AppDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PACKAGE_NAME, packageName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentAppDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String packageName = requireArguments().getString(ARG_PACKAGE_NAME);

        binding.imgAppIcon.setImageDrawable(AppInfoUtils.getAppIcon(requireContext(), packageName));
        binding.txtAppName.setText(AppInfoUtils.getAppLabel(requireContext(), packageName));
        binding.cardScreenTime.txtStatLabel.setText(R.string.today_screen_time);
        binding.cardWifi.txtStatLabel.setText(R.string.today_wifi_usage);

        AppDetailViewModel viewModel = new ViewModelProvider(this,
                new AppDetailViewModel.Factory(requireActivity().getApplication(), packageName))
                .get(AppDetailViewModel.class);

        viewModel.getScreenTimeMs().observe(getViewLifecycleOwner(), ms ->
                binding.cardScreenTime.txtStatValue.setText(TimeUtils.formatDurationShort(ms == null ? 0 : ms)));

        viewModel.getWifiBytes().observe(getViewLifecycleOwner(), bytes ->
                binding.cardWifi.txtStatValue.setText(TimeUtils.formatBytesShort(bytes == null ? 0 : bytes)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
