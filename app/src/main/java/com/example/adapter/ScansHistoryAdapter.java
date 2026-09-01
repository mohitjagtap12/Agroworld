package com.example.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databinding.ItemDiseaseScanHistoryBinding;
import com.example.model.SavedDiseaseScan;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying previous AI crop disease scans.
 */
public class ScansHistoryAdapter extends RecyclerView.Adapter<ScansHistoryAdapter.ScanViewHolder> {

    public interface OnScanClickListener {
        void onScanClick(SavedDiseaseScan scan);
    }

    private final List<SavedDiseaseScan> scanList = new ArrayList<>();
    private final OnScanClickListener listener;

    public ScansHistoryAdapter(OnScanClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<SavedDiseaseScan> newScans) {
        scanList.clear();
        if (newScans != null) {
            scanList.addAll(newScans);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDiseaseScanHistoryBinding binding = ItemDiseaseScanHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ScanViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanViewHolder holder, int position) {
        holder.bind(scanList.get(position));
    }

    @Override
    public int getItemCount() {
        return scanList.size();
    }

    class ScanViewHolder extends RecyclerView.ViewHolder {
        private final ItemDiseaseScanHistoryBinding binding;

        public ScanViewHolder(ItemDiseaseScanHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final SavedDiseaseScan scan) {
            binding.tvHistoryCropName.setText("🌱 " + (scan.getCropName() != null ? scan.getCropName() : "Crop"));
            binding.tvHistoryDate.setText(scan.getFormattedDate() != null ? scan.getFormattedDate() : "");
            binding.tvHistoryDiseaseName.setText(scan.getDiseaseName() != null ? scan.getDiseaseName() : "Unknown");
            binding.tvHistoryConfidence.setText("Confidence: " + (scan.getConfidence() != null ? scan.getConfidence() : "High"));
            binding.tvHistorySeverity.setText("Severity: " + (scan.getSeverity() != null ? scan.getSeverity() : "Moderate"));

            if (scan.getRecommendedAction() != null && !scan.getRecommendedAction().isEmpty()) {
                binding.tvHistoryRemedy.setText("Remedy: " + scan.getRecommendedAction().get(0));
            } else {
                binding.tvHistoryRemedy.setText("Monitor field humidity and remove infected leaves.");
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onScanClick(scan);
            });
        }
    }
}
