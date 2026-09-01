package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databinding.ItemContractApplicationBinding;
import com.example.model.ContractApplication;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * RecyclerView Adapter for Farmer Contract Applications & Active Deals.
 */
public class ContractApplicationAdapter extends ListAdapter<ContractApplication, ContractApplicationAdapter.ApplicationViewHolder> {

    public interface OnApplicationClickListener {
        void onApplicationClick(ContractApplication application);
        void onAcceptApplication(ContractApplication application);
        void onRejectApplication(ContractApplication application);
        void onUpdateMilestone(ContractApplication application);
    }

    private final OnApplicationClickListener listener;
    private boolean isCompanyView = false;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public ContractApplicationAdapter(OnApplicationClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        currencyFormat.setMaximumFractionDigits(0);
    }

    public void setCompanyView(boolean companyView) {
        this.isCompanyView = companyView;
        notifyDataSetChanged();
    }

    private static final DiffUtil.ItemCallback<ContractApplication> DIFF_CALLBACK = new DiffUtil.ItemCallback<ContractApplication>() {
        @Override
        public boolean areItemsTheSame(@NonNull ContractApplication oldItem, @NonNull ContractApplication newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ContractApplication oldItem, @NonNull ContractApplication newItem) {
            return oldItem.getStatus().equals(newItem.getStatus()) &&
                    oldItem.getMilestoneProgressPercent() == newItem.getMilestoneProgressPercent() &&
                    oldItem.getExpectedQuantityTons() == newItem.getExpectedQuantityTons();
        }
    };

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContractApplicationBinding binding = ItemContractApplicationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ApplicationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        ContractApplication app = getItem(position);
        holder.bind(app);
    }

    class ApplicationViewHolder extends RecyclerView.ViewHolder {
        private final ItemContractApplicationBinding binding;

        public ApplicationViewHolder(@NonNull ItemContractApplicationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ContractApplication app) {
            binding.tvAppCropEmoji.setText(app.getCropEmoji() != null ? app.getCropEmoji() : "🤝");
            binding.tvAppCropTitle.setText(app.getCropName() + " Contract");

            if (isCompanyView) {
                binding.tvAppCounterparty.setText("Farmer: " + app.getFarmerName() + " (" + app.getVillage() + ", " + app.getTaluka() + ")");
            } else {
                binding.tvAppCounterparty.setText("Company: " + app.getCompanyName());
            }

            // Status Badge with Color Coding
            String status = app.getStatus();
            binding.tvAppStatusBadge.setText(status);
            if ("Active".equalsIgnoreCase(status) || "Confirmed".equalsIgnoreCase(status)) {
                binding.tvAppStatusBadge.setBackgroundColor(0xFFE8F5E9);
                binding.tvAppStatusBadge.setTextColor(0xFF2E7D32);
            } else if ("Under Review".equalsIgnoreCase(status) || "Submitted".equalsIgnoreCase(status)) {
                binding.tvAppStatusBadge.setBackgroundColor(0xFFFFF3E0);
                binding.tvAppStatusBadge.setTextColor(0xFFE65100);
            } else if ("Accepted".equalsIgnoreCase(status)) {
                binding.tvAppStatusBadge.setBackgroundColor(0xFFE3F2FD);
                binding.tvAppStatusBadge.setTextColor(0xFF1976D2);
            } else if ("Completed".equalsIgnoreCase(status)) {
                binding.tvAppStatusBadge.setBackgroundColor(0xFFE8F5E9);
                binding.tvAppStatusBadge.setTextColor(0xFF388E3C);
            } else if ("Rejected".equalsIgnoreCase(status)) {
                binding.tvAppStatusBadge.setBackgroundColor(0xFFFFEBEE);
                binding.tvAppStatusBadge.setTextColor(0xFFD32F2F);
            } else {
                binding.tvAppStatusBadge.setBackgroundColor(0xFFEEEEEE);
                binding.tvAppStatusBadge.setTextColor(0xFF616161);
            }

            // Specs
            binding.tvAppLandArea.setText(String.format(Locale.getDefault(), "%.1f Acres", app.getLandAreaAcres()));
            binding.tvAppQuantity.setText(String.format(Locale.getDefault(), "%.1f Tons", app.getExpectedQuantityTons()));
            binding.tvAppHarvestDate.setText(app.getExpectedHarvestDate() != null ? app.getExpectedHarvestDate() : "TBD");
            binding.tvAppTotalValue.setText(currencyFormat.format(app.calculateTotalContractValue()));

            // Progress & Milestone
            binding.tvAppMilestoneText.setText(app.getCurrentMilestone() != null ? app.getCurrentMilestone() : "Application Submitted");
            binding.tvAppProgressPercent.setText(app.getMilestoneProgressPercent() + "%");
            binding.pbAppMilestoneProgress.setProgress(app.getMilestoneProgressPercent());

            // Notes
            if (app.getQualityGradeNotes() != null && !app.getQualityGradeNotes().isEmpty()) {
                binding.tvAppNotes.setVisibility(View.VISIBLE);
                binding.tvAppNotes.setText("Specs: " + app.getQualityGradeNotes());
            } else if (app.getAdditionalMessage() != null && !app.getAdditionalMessage().isEmpty()) {
                binding.tvAppNotes.setVisibility(View.VISIBLE);
                binding.tvAppNotes.setText("Notes: " + app.getAdditionalMessage());
            } else {
                binding.tvAppNotes.setVisibility(View.GONE);
            }

            // Company vs Farmer Buttons
            if (isCompanyView && ("Submitted".equalsIgnoreCase(status) || "Under Review".equalsIgnoreCase(status))) {
                binding.layoutCompanyActions.setVisibility(View.VISIBLE);
                binding.btnViewTimeline.setVisibility(View.GONE);

                binding.btnAcceptApp.setOnClickListener(v -> {
                    if (listener != null) listener.onAcceptApplication(app);
                });

                binding.btnRejectApp.setOnClickListener(v -> {
                    if (listener != null) listener.onRejectApplication(app);
                });
            } else if (isCompanyView && ("Active".equalsIgnoreCase(status) || "Accepted".equalsIgnoreCase(status))) {
                binding.layoutCompanyActions.setVisibility(View.GONE);
                binding.btnViewTimeline.setVisibility(View.VISIBLE);
                binding.btnViewTimeline.setText("Update Crop Progress & Milestone");
                binding.btnViewTimeline.setOnClickListener(v -> {
                    if (listener != null) listener.onUpdateMilestone(app);
                });
            } else {
                binding.layoutCompanyActions.setVisibility(View.GONE);
                binding.btnViewTimeline.setVisibility(View.VISIBLE);
                binding.btnViewTimeline.setText("View Contract Timeline & Terms");
                binding.btnViewTimeline.setOnClickListener(v -> {
                    if (listener != null) listener.onApplicationClick(app);
                });
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onApplicationClick(app);
            });
        }
    }
}
