package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databinding.ItemCropCardBinding;
import com.example.model.FarmerCrop;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying Farmer crops with health status and AI action triggers.
 */
public class CropsAdapter extends RecyclerView.Adapter<CropsAdapter.CropViewHolder> {

    public interface OnCropActionListener {
        void onCropClick(FarmerCrop crop);
        void onCheckDiseaseClick(FarmerCrop crop);
        void onDiseaseHistoryClick(FarmerCrop crop);
    }

    private final List<FarmerCrop> cropList = new ArrayList<>();
    private final OnCropActionListener listener;

    public CropsAdapter(OnCropActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<FarmerCrop> newCrops) {
        cropList.clear();
        if (newCrops != null) {
            cropList.addAll(newCrops);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCropCardBinding binding = ItemCropCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CropViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CropViewHolder holder, int position) {
        holder.bind(cropList.get(position));
    }

    @Override
    public int getItemCount() {
        return cropList.size();
    }

    class CropViewHolder extends RecyclerView.ViewHolder {
        private final ItemCropCardBinding binding;

        public CropViewHolder(ItemCropCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final FarmerCrop crop) {
            binding.tvCropEmoji.setText(crop.getImagePreset() != null ? crop.getImagePreset() : "🌱");
            binding.tvCropName.setText(crop.getName());
            binding.tvCropVariety.setText("Variety: " + crop.getVariety() + " • " + crop.getCategory());
            binding.tvCropStage.setText(crop.getStatus());

            binding.tvLandArea.setText("📍 Land: " + crop.getLandArea() + " " + crop.getUnit());
            binding.tvIrrigation.setText("💧 " + crop.getIrrigationType());
            binding.tvSowingDate.setText("📅 Sown: " + crop.getSowingDate());
            binding.tvHarvestDate.setText("🌾 Harvest: " + crop.getHarvestDate());

            if (crop.getLatestHealthStatus() != null && !crop.getLatestHealthStatus().isEmpty()
                    && !"Healthy".equalsIgnoreCase(crop.getLatestHealthStatus())) {
                binding.tvDiseaseStatus.setText("🤖 Health Alert: " + crop.getLatestHealthStatus());
                binding.tvDiseaseStatus.setTextColor(binding.getRoot().getContext().getColor(com.example.R.color.farmer_error));
            } else {
                binding.tvDiseaseStatus.setText("🤖 Health Status: Healthy & Monitored");
                binding.tvDiseaseStatus.setTextColor(binding.getRoot().getContext().getColor(com.example.R.color.farmer_primary));
            }

            binding.cardCrop.setOnClickListener(v -> {
                if (listener != null) listener.onCropClick(crop);
            });

            binding.btnCheckDisease.setOnClickListener(v -> {
                if (listener != null) listener.onCheckDiseaseClick(crop);
            });

            binding.btnDiseaseHistory.setOnClickListener(v -> {
                if (listener != null) listener.onDiseaseHistoryClick(crop);
            });
        }
    }
}
