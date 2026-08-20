package com.nandini.screentime.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.nandini.screentime.data.DateTotal;
import com.nandini.screentime.databinding.FragmentHistoryBinding;
import com.nandini.screentime.util.TimeUtils;
import com.nandini.screentime.viewmodel.HistoryViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private static final int HISTORY_DAYS = 7;

    private FragmentHistoryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HistoryViewModel viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        setUpChart(binding.chartScreenTime);
        setUpChart(binding.chartWifi);

        viewModel.getDailyScreenTime().observe(getViewLifecycleOwner(), data ->
                renderChart(binding.chartScreenTime, data, 60_000.0, "min"));

        viewModel.getDailyWifi().observe(getViewLifecycleOwner(), data ->
                renderChart(binding.chartWifi, data, 1024.0 * 1024.0, "MB"));
    }

    private void setUpChart(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
    }

    private void renderChart(BarChart chart, List<DateTotal> data, double divisor, String unitSuffix) {
        Map<String, Long> byDate = new HashMap<>();
        if (data != null) {
            for (DateTotal dt : data) {
                byDate.put(dt.date, dt.total);
            }
        }

        List<BarEntry> entries = new ArrayList<>(HISTORY_DAYS);
        List<String> labels = new ArrayList<>(HISTORY_DAYS);
        for (int i = HISTORY_DAYS - 1; i >= 0; i--) {
            String date = TimeUtils.dateKeyDaysAgo(i);
            long total = byDate.containsKey(date) ? byDate.get(date) : 0L;
            entries.add(new BarEntry(HISTORY_DAYS - 1 - i, (float) (total / divisor)));
            labels.add(date.substring(5)); // MM-dd
        }

        BarDataSet dataSet = new BarDataSet(entries, unitSuffix);
        dataSet.setColor(androidx.core.content.ContextCompat.getColor(
                requireContext(), com.nandini.screentime.R.color.primary));
        BarData barData = new BarData(dataSet);
        barData.setValueTextSize(10f);

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.setData(barData);
        chart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
