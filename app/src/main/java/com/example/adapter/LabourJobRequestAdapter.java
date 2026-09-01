package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databinding.ItemLabourRequirementBinding;
import com.example.model.LabourApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for Worker's incoming job requests and confirmed jobs in Labour Portal.
 */
public class LabourJobRequestAdapter extends RecyclerView.Adapter<LabourJobRequestAdapter.JobRequestViewHolder> {

    private List<LabourApplication> applications = new ArrayList<>();
    private OnJobRequestActionListener listener;

    public interface OnJobRequestActionListener {
        void onAccept(LabourApplication application);
        void onReject(LabourApplication application);
    }

    public void setOnJobRequestActionListener(OnJobRequestActionListener listener) {
        this.listener = listener;
    }

    public void setApplications(List<LabourApplication> newApplications) {
        this.applications = newApplications != null ? new ArrayList<>(newApplications) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLabourRequirementBinding binding = ItemLabourRequirementBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new JobRequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JobRequestViewHolder holder, int position) {
        holder.bind(applications.get(position));
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    class JobRequestViewHolder extends RecyclerView.ViewHolder {
        private final ItemLabourRequirementBinding binding;

        JobRequestViewHolder(@NonNull ItemLabourRequirementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(LabourApplication app) {
            binding.tvReqWorkType.setText(app.getWorkType());
            binding.tvReqCropFarmer.setText(String.format(Locale.ENGLISH, "🌾 %s • Farmer: %s (%s)",
                    app.getCrop(), app.getFarmerName(), app.getFarmerPhone()));
            binding.chipReqStatus.setText(app.getStatus());
            binding.tvReqDateTime.setText(String.format(Locale.ENGLISH, "📅 %s • %s (%d hrs)",
                    app.getStartDate(), app.getStartTime(), app.getWorkingHours()));
            binding.tvReqWage.setText(String.format(Locale.ENGLISH, "₹%.0f / %s",
                    app.getWage(), app.getWageType().toLowerCase()));
            binding.tvReqLocation.setText(String.format(Locale.ENGLISH, "📍 %s, %s",
                    app.getVillage(), app.getTaluka()));

            String perks = (app.isFoodProvided() ? "🍱 Food Provided" : "🚫 No Food") +
                    " • " + (app.isTransportProvided() ? "🚐 Transport Provided" : "🚫 No Transport");
            binding.tvReqPerks.setText(perks);

            if ("Pending".equalsIgnoreCase(app.getStatus())) {
                binding.layoutActionButtons.setVisibility(View.VISIBLE);
                binding.btnAcceptRequest.setOnClickListener(v -> {
                    if (listener != null) listener.onAccept(app);
                });
                binding.btnRejectRequest.setOnClickListener(v -> {
                    if (listener != null) listener.onReject(app);
                });
            } else {
                binding.layoutActionButtons.setVisibility(View.GONE);
            }
        }
    }
}
