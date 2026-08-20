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

import com.nandini.screentime.databinding.FragmentAppListBinding;
import com.nandini.screentime.viewmodel.AppListViewModel;

public class AppListFragment extends Fragment {

    private FragmentAppListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentAppListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppListViewModel viewModel = new ViewModelProvider(this).get(AppListViewModel.class);

        AppUsageAdapter adapter = new AppUsageAdapter(requireContext(), packageName -> {
            AppDetailFragment detail = AppDetailFragment.newInstance(packageName);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(com.nandini.screentime.R.id.fragmentContainer, detail)
                    .addToBackStack(null)
                    .commit();
        });

        binding.recyclerApps.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerApps.setAdapter(adapter);

        viewModel.getMergedRows().observe(getViewLifecycleOwner(), adapter::submitList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
