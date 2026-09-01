package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.BrokerDeal;
import com.example.R;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for rendering confirmed wholesale broker deals and lifecycle tracking.
 */
public class BrokerDealAdapter extends RecyclerView.Adapter<BrokerDealAdapter.DealViewHolder> {

    public interface OnDealActionListener {
        void onDealActionClicked(BrokerDeal deal, String nextStatus);
    }

    private final List<BrokerDeal> dealList = new ArrayList<>();
    private final OnDealActionListener listener;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public BrokerDealAdapter(OnDealActionListener listener) {
        this.listener = listener;
        this.currencyFormat.setMaximumFractionDigits(0);
    }

    public void updateData(List<BrokerDeal> newDeals) {
        this.dealList.clear();
        if (newDeals != null) {
            this.dealList.addAll(newDeals);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_broker_deal, parent, false);
        return new DealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        BrokerDeal deal = dealList.get(position);
        holder.bind(deal, listener, currencyFormat);
    }

    @Override
    public int getItemCount() {
        return dealList.size();
    }

    static class DealViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDealCropEmoji;
        private final TextView tvDealCropName;
        private final TextView tvDealCounterparty;
        private final TextView tvDealStatusBadge;
        private final TextView tvDealQuantity;
        private final TextView tvDealAgreedRate;
        private final TextView tvDealTotalValue;
        private final TextView tvDealLocation;
        private final TextView tvDealPickupDate;
        private final TextView tvDealContact;
        private final MaterialButton btnDealAction;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDealCropEmoji = itemView.findViewById(R.id.tvDealCropEmoji);
            tvDealCropName = itemView.findViewById(R.id.tvDealCropName);
            tvDealCounterparty = itemView.findViewById(R.id.tvDealCounterparty);
            tvDealStatusBadge = itemView.findViewById(R.id.tvDealStatusBadge);
            tvDealQuantity = itemView.findViewById(R.id.tvDealQuantity);
            tvDealAgreedRate = itemView.findViewById(R.id.tvDealAgreedRate);
            tvDealTotalValue = itemView.findViewById(R.id.tvDealTotalValue);
            tvDealLocation = itemView.findViewById(R.id.tvDealLocation);
            tvDealPickupDate = itemView.findViewById(R.id.tvDealPickupDate);
            tvDealContact = itemView.findViewById(R.id.tvDealContact);
            btnDealAction = itemView.findViewById(R.id.btnDealAction);
        }

        public void bind(BrokerDeal deal, OnDealActionListener listener, NumberFormat currencyFormat) {
            tvDealCropEmoji.setText(deal.getCropEmoji() != null ? deal.getCropEmoji() : "🤝");
            tvDealCropName.setText((deal.getCrop() != null ? deal.getCrop() : "Produce") + " Bulk Deal");
            tvDealCounterparty.setText("Broker: " + (deal.getBrokerName() != null ? deal.getBrokerName() : "Verified Broker") +
                    " (" + (deal.getCompanyName() != null ? deal.getCompanyName() : "Agri Trading") + ")");

            String status = deal.getStatus() != null ? deal.getStatus() : "Deal Confirmed";
            tvDealStatusBadge.setText(status);

            // Quantity formatting
            double qty = deal.getQuantity();
            String unitStr = deal.getUnit() != null ? deal.getUnit() : "Tons";
            if ("Tons".equalsIgnoreCase(unitStr) || "Ton".equalsIgnoreCase(unitStr)) {
                tvDealQuantity.setText(qty + " Tons (" + (int)(qty * 10) + " Qtl)");
            } else {
                tvDealQuantity.setText(qty + " " + unitStr);
            }

            tvDealAgreedRate.setText("Rate: ₹" + (int) deal.getAgreedPrice() + " / " + (deal.getPriceUnit() != null ? deal.getPriceUnit() : "quintal"));

            double total = deal.getTotalValue();
            tvDealTotalValue.setText(currencyFormat.format(total));

            tvDealLocation.setText("📍 Pickup Location: " + (deal.getPickupLocation() != null ? deal.getPickupLocation() : "Farm Gate"));
            tvDealPickupDate.setText("📅 Handover: " + (deal.getPickupDate() != null ? deal.getPickupDate() : "Scheduled") +
                    " • " + (deal.getPaymentTerms() != null ? deal.getPaymentTerms() : "RTGS Settlement"));
            tvDealContact.setText("📞 Phone: " + (deal.getPhone() != null ? deal.getPhone() : "+91 98765 43210"));

            // Lifecycle Action button logic
            if ("Completed".equalsIgnoreCase(status)) {
                btnDealAction.setVisibility(View.GONE);
            } else {
                btnDealAction.setVisibility(View.VISIBLE);
                final String nextStatus;
                if ("Price Agreed".equalsIgnoreCase(status)) {
                    btnDealAction.setText("Confirm Deal");
                    nextStatus = "Deal Confirmed";
                } else if ("Deal Confirmed".equalsIgnoreCase(status)) {
                    btnDealAction.setText("Schedule Pickup");
                    nextStatus = "Pickup Scheduled";
                } else if ("Pickup Scheduled".equalsIgnoreCase(status)) {
                    btnDealAction.setText("Mark Crop Handed Over");
                    nextStatus = "Crop Handed Over";
                } else if ("Crop Handed Over".equalsIgnoreCase(status)) {
                    btnDealAction.setText("Mark Deal Completed");
                    nextStatus = "Completed";
                } else {
                    btnDealAction.setText("Update Status");
                    nextStatus = "Completed";
                }

                btnDealAction.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDealActionClicked(deal, nextStatus);
                    }
                });
            }
        }
    }
}
