package com.example.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.databinding.ItemFarmerLabourJobBinding;
import com.example.model.LabourRequirement;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying Farmer's posted labour requirements.
 */
public class LabourRequirementAdapter extends RecyclerView.Adapter<LabourRequirementAdapter.RequirementViewHolder> {

    private List<LabourRequirement> requirements = new ArrayList<>();
    private OnRequirementActionListener listener;

    public interface OnRequirementActionListener {
        void onConfirmWorker(LabourRequirement requirement);
        void onViewRequirement(LabourRequirement requirement);
    }

    public void setOnRequirementActionListener(OnRequirementActionListener listener) {
        this.listener = listener;
    }

    public void setRequirements(List<LabourRequirement> newRequirements) {
        this.requirements = newRequirements != null ? new ArrayList<>(newRequirements) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequirementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFarmerLabourJobBinding binding = ItemFarmerLabourJobBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RequirementViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RequirementViewHolder holder, int position) {
        holder.bind(requirements.get(position));
    }

    @Override
    public int getItemCount() {
        return requirements.size();
    }

    class RequirementViewHolder extends RecyclerView.ViewHolder {
        private final ItemFarmerLabourJobBinding binding;

        RequirementViewHolder(@NonNull ItemFarmerLabourJobBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(LabourRequirement req) {
            binding.tvJobWorkType.setText(req.getWorkType());
            binding.tvJobCrop.setText(String.format(Locale.ENGLISH, "🌾 %s • %d Workers Required", req.getCrop(), req.getWorkersRequired()));
            binding.chipJobStatus.setText(req.getStatus());
            binding.tvJobDate.setText(String.format(Locale.ENGLISH, "📅 %s • %s (%d hrs)", req.getStartDate(), req.getStartTime(), req.getWorkingHoursPerDay()));
            binding.tvJobWage.setText(String.format(Locale.ENGLISH, "₹%.0f / %s", req.getWageAmount(), req.getWageType().toLowerCase()));
            binding.tvJobLocation.setText(String.format(Locale.ENGLISH, "📍 %s, %s", req.getVillage(), req.getTaluka()));

            int accepted = req.getWorkerIdsAccepted() != null ? req.getWorkerIdsAccepted().size() : 0;
            int confirmed = req.getWorkerIdsConfirmed() != null ? req.getWorkerIdsConfirmed().size() : 0;
            int requested = req.getWorkerIdsRequested() != null ? req.getWorkerIdsRequested().size() : 0;

            binding.tvJobWorkersStatus.setText(String.format(Locale.ENGLISH,
                    "👥 %d Sent • %d Accepted • %d Confirmed", requested, accepted, confirmed));

            if ("Accepted".equalsIgnoreCase(req.getStatus()) || accepted > confirmed) {
                binding.btnJobAction.setText("[CONFIRM WORKER]");
                binding.btnJobAction.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_primary));
                binding.btnJobAction.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
            } else if ("Confirmed".equalsIgnoreCase(req.getStatus()) || "Scheduled".equalsIgnoreCase(req.getStatus())) {
                binding.btnJobAction.setText("✓ SCHEDULED");
                binding.btnJobAction.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_light_bg));
                binding.btnJobAction.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_primary_dark));
            } else {
                binding.btnJobAction.setText("[VIEW DETAILS]");
                binding.btnJobAction.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_light_bg));
                binding.btnJobAction.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_primary));
            }

            binding.btnJobAction.setOnClickListener(v -> {
                if (listener != null) {
                    if ("Accepted".equalsIgnoreCase(req.getStatus()) || accepted > confirmed) {
                        listener.onConfirmWorker(req);
                    } else {
                        listener.onViewRequirement(req);
                    }
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewRequirement(req);
                }
            });
        }
    }
}
