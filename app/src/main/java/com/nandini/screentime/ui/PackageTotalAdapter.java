package com.nandini.screentime.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nandini.screentime.data.PackageTotal;
import com.nandini.screentime.databinding.ItemAppUsageBinding;
import com.nandini.screentime.util.AppInfoUtils;
import com.nandini.screentime.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/** Renders a list of {package, total} rows, formatting the total as a duration or as bytes. */
public class PackageTotalAdapter extends RecyclerView.Adapter<PackageTotalAdapter.ViewHolder> {

    public enum ValueType { DURATION, BYTES }

    private final Context context;
    private final ValueType valueType;
    private List<PackageTotal> items = new ArrayList<>();

    public PackageTotalAdapter(Context context, ValueType valueType) {
        this.context = context;
        this.valueType = valueType;
    }

    public void submitList(List<PackageTotal> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppUsageBinding binding = ItemAppUsageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PackageTotal item = items.get(position);
        holder.binding.txtAppName.setText(AppInfoUtils.getAppLabel(context, item.packageName));
        holder.binding.imgAppIcon.setImageDrawable(AppInfoUtils.getAppIcon(context, item.packageName));
        holder.binding.txtAppValue.setText(valueType == ValueType.DURATION
                ? TimeUtils.formatDurationShort(item.total)
                : TimeUtils.formatBytesShort(item.total));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemAppUsageBinding binding;

        ViewHolder(ItemAppUsageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
