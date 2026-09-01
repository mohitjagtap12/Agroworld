package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.BrokerRequirement;
import com.example.model.FarmerCrop;
import com.example.R;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for rendering wholesale broker buying requirements and bulk commodity demands.
 */
public class BrokerRequirementAdapter extends RecyclerView.Adapter<BrokerRequirementAdapter.RequirementViewHolder> {

    public interface OnRequirementActionListener {
        void onMakeOfferClicked(BrokerRequirement requirement);
    }

    private final List<BrokerRequirement> requirementList = new ArrayList<>();
    private final List<FarmerCrop> farmerCrops = new ArrayList<>();
    private final OnRequirementActionListener listener;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public BrokerRequirementAdapter(OnRequirementActionListener listener) {
        this.listener = listener;
        this.currencyFormat.setMaximumFractionDigits(0);
    }

    public void updateData(List<BrokerRequirement> newRequirements, List<FarmerCrop> farmerCrops) {
        this.requirementList.clear();
        if (newRequirements != null) {
            this.requirementList.addAll(newRequirements);
        }
        this.farmerCrops.clear();
        if (farmerCrops != null) {
            this.farmerCrops.addAll(farmerCrops);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequirementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_broker_requirement, parent, false);
        return new RequirementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequirementViewHolder holder, int position) {
        BrokerRequirement req = requirementList.get(position);
        holder.bind(req, farmerCrops, listener, currencyFormat);
    }

    @Override
    public int getItemCount() {
        return requirementList.size();
    }

    static class RequirementViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCropEmoji;
        private final TextView tvCropName;
        private final TextView tvBrokerFirm;
        private final TextView tvStatusBadge;
        private final TextView tvMatchBadge;
        private final TextView tvOfferedPrice;
        private final TextView tvSampleMandiRate;
        private final TextView tvRequiredQuantity;
        private final TextView tvEstTotalVal;
        private final TextView tvQualitySpecs;
        private final TextView tvPickupLocation;
        private final TextView tvRequiredDate;
        private final MaterialButton btnMakeOffer;

        public RequirementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCropEmoji = itemView.findViewById(R.id.tvCropEmoji);
            tvCropName = itemView.findViewById(R.id.tvCropName);
            tvBrokerFirm = itemView.findViewById(R.id.tvBrokerFirm);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvMatchBadge = itemView.findViewById(R.id.tvMatchBadge);
            tvOfferedPrice = itemView.findViewById(R.id.tvOfferedPrice);
            tvSampleMandiRate = itemView.findViewById(R.id.tvSampleMandiRate);
            tvRequiredQuantity = itemView.findViewById(R.id.tvRequiredQuantity);
            tvEstTotalVal = itemView.findViewById(R.id.tvEstTotalVal);
            tvQualitySpecs = itemView.findViewById(R.id.tvQualitySpecs);
            tvPickupLocation = itemView.findViewById(R.id.tvPickupLocation);
            tvRequiredDate = itemView.findViewById(R.id.tvRequiredDate);
            btnMakeOffer = itemView.findViewById(R.id.btnMakeOffer);
        }

        public void bind(BrokerRequirement req, List<FarmerCrop> farmerCrops,
                         OnRequirementActionListener listener, NumberFormat currencyFormat) {
            tvCropEmoji.setText(req.getCropEmoji() != null ? req.getCropEmoji() : "🌾");
            tvCropName.setText(req.getCrop());
            tvBrokerFirm.setText((req.getBrokerFirmName() != null ? req.getBrokerFirmName() : req.getBrokerName()) + " • Verified Buyer");
            tvStatusBadge.setText(req.getStatus() != null ? req.getStatus() : "Open Demand");

            // Check if matching farmer's registered crops
            boolean isMatch = req.matchesFarmerCrops(farmerCrops);
            if (isMatch) {
                tvMatchBadge.setVisibility(View.VISIBLE);
                tvMatchBadge.setText("⭐ Matches your registered crop (" + req.getCrop() + ")");
            } else {
                tvMatchBadge.setVisibility(View.GONE);
            }

            // Pricing & Mandi Reference
            tvOfferedPrice.setText("₹" + (int) req.getOfferedPrice() + " / " + req.getPriceUnit());
            if (req.getSampleMandiPrice() > 0) {
                tvSampleMandiRate.setText("Sample Mandi: ₹" + (int) req.getSampleMandiPrice());
                tvSampleMandiRate.setVisibility(View.VISIBLE);
            } else {
                tvSampleMandiRate.setVisibility(View.GONE);
            }

            // Quantity & Total Value
            double qty = req.getRequiredQuantity();
            String unitStr = req.getUnit() != null ? req.getUnit() : "Tons";
            if ("Tons".equalsIgnoreCase(unitStr) || "Ton".equalsIgnoreCase(unitStr)) {
                tvRequiredQuantity.setText(qty + " Tons (" + (int)(qty * 10) + " Qtl)");
            } else {
                tvRequiredQuantity.setText(qty + " " + unitStr);
            }

            double totalEst = req.calculateTotalEstimatedValue();
            tvEstTotalVal.setText("Est. Total: " + currencyFormat.format(totalEst));

            // Details
            tvQualitySpecs.setText("✨ Quality: " + (req.getQualityRequirement() != null ? req.getQualityRequirement() : "FAQ Grade"));
            tvPickupLocation.setText("📍 Pickup: " + (req.getPickupLocation() != null ? req.getPickupLocation() : "Local APMC Hub"));
            tvRequiredDate.setText("📅 Required: " + (req.getRequiredDate() != null ? req.getRequiredDate() : "Immediate") +
                    " • Payment: " + (req.getPaymentTerms() != null ? req.getPaymentTerms() : "Immediate RTGS on delivery slip"));

            btnMakeOffer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMakeOfferClicked(req);
                }
            });
        }
    }
}
