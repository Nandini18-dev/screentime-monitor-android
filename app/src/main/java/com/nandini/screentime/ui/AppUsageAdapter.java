package com.nandini.screentime.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nandini.screentime.data.AppUsageRow;
import com.nandini.screentime.databinding.ItemAppFullBinding;
import com.nandini.screentime.util.AppInfoUtils;
import com.nandini.screentime.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageAdapter.ViewHolder> {

    public interface OnAppClickListener {
        void onAppClick(String packageName);
    }

    private final Context context;
    private final OnAppClickListener listener;
    private List<AppUsageRow> items = new ArrayList<>();

    public AppUsageAdapter(Context context, OnAppClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void submitList(List<AppUsageRow> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppFullBinding binding = ItemAppFullBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppUsageRow item = items.get(position);
        holder.binding.txtAppName.setText(AppInfoUtils.getAppLabel(context, item.packageName));
        holder.binding.imgAppIcon.setImageDrawable(AppInfoUtils.getAppIcon(context, item.packageName));
        holder.binding.txtScreenTime.setText(TimeUtils.formatDurationShort(item.foregroundTimeMs));
        holder.binding.txtWifiUsage.setText(TimeUtils.formatBytesShort(item.wifiBytes));
        holder.itemView.setOnClickListener(v -> listener.onAppClick(item.packageName));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemAppFullBinding binding;

        ViewHolder(ItemAppFullBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
