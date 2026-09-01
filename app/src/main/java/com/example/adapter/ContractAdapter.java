package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databinding.ItemContractCardBinding;
import com.example.model.ContractFarmingDeal;
import com.example.model.FarmerCrop;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * RecyclerView Adapter for Corporate Contract Farming Deals.
 */
public class ContractAdapter extends ListAdapter<ContractFarmingDeal, ContractAdapter.ContractViewHolder> {

    public interface OnContractClickListener {
        void onContractDetailsClick(ContractFarmingDeal deal);
        void onApplyContractClick(ContractFarmingDeal deal);
    }

    private final OnContractClickListener listener;
    private final List<FarmerCrop> farmerCrops = new ArrayList<>();
    private final Set<String> appliedContractIds = new HashSet<>();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public ContractAdapter(OnContractClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        currencyFormat.setMaximumFractionDigits(0);
    }

    private static final DiffUtil.ItemCallback<ContractFarmingDeal> DIFF_CALLBACK = new DiffUtil.ItemCallback<ContractFarmingDeal>() {
        @Override
        public boolean areItemsTheSame(@NonNull ContractFarmingDeal oldItem, @NonNull ContractFarmingDeal newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ContractFarmingDeal oldItem, @NonNull ContractFarmingDeal newItem) {
            return oldItem.getStatus().equals(newItem.getStatus()) &&
                    oldItem.getOfferedPricePerTon() == newItem.getOfferedPricePerTon() &&
                    oldItem.getRequiredQuantityTons() == newItem.getRequiredQuantityTons();
        }
    };

    public void setFarmerCrops(List<FarmerCrop> crops) {
        this.farmerCrops.clear();
        if (crops != null) {
            this.farmerCrops.addAll(crops);
        }
        notifyDataSetChanged();
    }

    public void setAppliedContractIds(Set<String> appliedIds) {
        this.appliedContractIds.clear();
        if (appliedIds != null) {
            this.appliedContractIds.addAll(appliedIds);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContractCardBinding binding = ItemContractCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ContractViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractViewHolder holder, int position) {
        ContractFarmingDeal deal = getItem(position);
        holder.bind(deal);
    }

    class ContractViewHolder extends RecyclerView.ViewHolder {
        private final ItemContractCardBinding binding;

        public ContractViewHolder(@NonNull ItemContractCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ContractFarmingDeal deal) {
            binding.tvContractCropEmoji.setText(deal.getCropEmoji() != null ? deal.getCropEmoji() : "🌾");
            binding.tvContractCropName.setText(deal.getCropName());
            binding.tvContractVariety.setText("Variety: " + (deal.getVariety() != null ? deal.getVariety() : "Standard"));
            binding.tvContractCompanyName.setText(deal.getCompanyName());

            // Status Badge
            binding.tvContractStatusBadge.setText(deal.getStatus());
            if ("Open".equalsIgnoreCase(deal.getStatus())) {
                binding.tvContractStatusBadge.setBackgroundColor(0xFFE8F5E9);
                binding.tvContractStatusBadge.setTextColor(0xFF2E7D32);
            } else {
                binding.tvContractStatusBadge.setBackgroundColor(0xFFEEEEEE);
                binding.tvContractStatusBadge.setTextColor(0xFF757575);
            }

            // Matching crop banner
            boolean isMatched = deal.matchesFarmerCrops(farmerCrops);
            binding.layoutCropMatch.setVisibility(isMatched ? View.VISIBLE : View.GONE);

            // Quantity & Price
            binding.tvContractQuantity.setText(String.format(Locale.getDefault(), "%.0f %s", deal.getRequiredQuantityTons(), deal.getUnit()));
            binding.tvContractPrice.setText(currencyFormat.format(deal.getOfferedPricePerTon()) + " / " + deal.getPriceUnit());

            // Harvest & Location
            binding.tvContractHarvestPeriod.setText(deal.getHarvestPeriod());
            binding.tvContractLocation.setText(deal.getLocation());

            // Advance & Deadline
            binding.tvContractAdvanceTag.setText("💰 " + deal.getAdvancePaymentPercent() + "% Advance Token");
            binding.tvContractDeadline.setText("Apply before " + deal.getApplicationDeadline());

            // Check if already applied
            boolean alreadyApplied = appliedContractIds.contains(deal.getId());
            if (alreadyApplied) {
                binding.btnApplyContract.setText("Applied ✓");
                binding.btnApplyContract.setEnabled(false);
                binding.btnApplyContract.setBackgroundColor(0xFF9E9E9E);
            } else if (!"Open".equalsIgnoreCase(deal.getStatus())) {
                binding.btnApplyContract.setText("Closed");
                binding.btnApplyContract.setEnabled(false);
                binding.btnApplyContract.setBackgroundColor(0xFFBDBDBD);
            } else {
                binding.btnApplyContract.setText("Apply Now");
                binding.btnApplyContract.setEnabled(true);
                binding.btnApplyContract.setBackgroundColor(0xFF2E7D32);
            }

            // Click Listeners
            binding.btnViewContractDetails.setOnClickListener(v -> {
                if (listener != null) listener.onContractDetailsClick(deal);
            });

            binding.btnApplyContract.setOnClickListener(v -> {
                if (listener != null && !alreadyApplied) listener.onApplyContractClick(deal);
            });

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onContractDetailsClick(deal);
            });
        }
    }
}
