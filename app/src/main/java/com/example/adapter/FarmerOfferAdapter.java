package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.FarmerBrokerOffer;
import com.example.R;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for rendering bulk crop supply offers and counter-offers between farmers and brokers.
 */
public class FarmerOfferAdapter extends RecyclerView.Adapter<FarmerOfferAdapter.OfferViewHolder> {

    public interface OnOfferActionListener {
        void onAcceptClicked(FarmerBrokerOffer offer);
        void onCounterClicked(FarmerBrokerOffer offer);
        void onRejectOrWithdrawClicked(FarmerBrokerOffer offer);
    }

    private final List<FarmerBrokerOffer> offerList = new ArrayList<>();
    private final OnOfferActionListener listener;
    private final boolean isBrokerMode;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public FarmerOfferAdapter(boolean isBrokerMode, OnOfferActionListener listener) {
        this.isBrokerMode = isBrokerMode;
        this.listener = listener;
        this.currencyFormat.setMaximumFractionDigits(0);
    }

    public void updateData(List<FarmerBrokerOffer> newOffers) {
        this.offerList.clear();
        if (newOffers != null) {
            this.offerList.addAll(newOffers);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farmer_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        FarmerBrokerOffer offer = offerList.get(position);
        holder.bind(offer, isBrokerMode, listener, currencyFormat);
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOfferCropEmoji;
        private final TextView tvOfferTitle;
        private final TextView tvOfferSubtitle;
        private final TextView tvOfferStatus;
        private final TextView tvOfferQuantity;
        private final TextView tvOfferPriceBreakdown;
        private final TextView tvOfferTotalValue;
        private final LinearLayout llNegotiationNote;
        private final TextView tvNegotiationNote;
        private final TextView tvOfferDetails;
        private final LinearLayout llOfferActions;
        private final MaterialButton btnCancelOffer;
        private final MaterialButton btnCounterOffer;
        private final MaterialButton btnAcceptOffer;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOfferCropEmoji = itemView.findViewById(R.id.tvOfferCropEmoji);
            tvOfferTitle = itemView.findViewById(R.id.tvOfferTitle);
            tvOfferSubtitle = itemView.findViewById(R.id.tvOfferSubtitle);
            tvOfferStatus = itemView.findViewById(R.id.tvOfferStatus);
            tvOfferQuantity = itemView.findViewById(R.id.tvOfferQuantity);
            tvOfferPriceBreakdown = itemView.findViewById(R.id.tvOfferPriceBreakdown);
            tvOfferTotalValue = itemView.findViewById(R.id.tvOfferTotalValue);
            llNegotiationNote = itemView.findViewById(R.id.llNegotiationNote);
            tvNegotiationNote = itemView.findViewById(R.id.tvNegotiationNote);
            tvOfferDetails = itemView.findViewById(R.id.tvOfferDetails);
            llOfferActions = itemView.findViewById(R.id.llOfferActions);
            btnCancelOffer = itemView.findViewById(R.id.btnCancelOffer);
            btnCounterOffer = itemView.findViewById(R.id.btnCounterOffer);
            btnAcceptOffer = itemView.findViewById(R.id.btnAcceptOffer);
        }

        public void bind(FarmerBrokerOffer offer, boolean isBrokerMode,
                         OnOfferActionListener listener, NumberFormat currencyFormat) {
            tvOfferCropEmoji.setText(offer.getCropEmoji() != null ? offer.getCropEmoji() : "🌾");
            tvOfferTitle.setText(offer.getCropName() + " Supply Offer (" + offer.getAvailableQuantity() + " " + offer.getUnit() + ")");

            if (isBrokerMode) {
                tvOfferSubtitle.setText("Farmer: " + (offer.getFarmerName() != null ? offer.getFarmerName() : "Registered Farmer") + " • " + (offer.getFarmerLocation() != null ? offer.getFarmerLocation() : "Maharashtra"));
            } else {
                tvOfferSubtitle.setText("Location: " + (offer.getFarmerLocation() != null ? offer.getFarmerLocation() : "Farm Gate") + " • Date: " + offer.getCreatedAt());
            }

            String status = offer.getStatus() != null ? offer.getStatus() : "Pending";
            tvOfferStatus.setText(status);

            // Quantity formatting
            double qty = offer.getAvailableQuantity();
            String unitStr = offer.getUnit() != null ? offer.getUnit() : "Tons";
            if ("Tons".equalsIgnoreCase(unitStr) || "Ton".equalsIgnoreCase(unitStr)) {
                tvOfferQuantity.setText(qty + " Tons (" + (int)(qty * 10) + " Qtl)");
            } else {
                tvOfferQuantity.setText(qty + " " + unitStr);
            }

            // Price Details
            double effPrice = offer.getFinalAgreedPrice() > 0 ? offer.getFinalAgreedPrice() :
                    (offer.getCounterPrice() > 0 ? offer.getCounterPrice() : offer.getExpectedPrice());

            if (offer.getCounterPrice() > 0 && "Negotiating".equalsIgnoreCase(status)) {
                tvOfferPriceBreakdown.setText("Exp: ₹" + (int) offer.getExpectedPrice() + " | Counter: ₹" + (int) offer.getCounterPrice() + "/" + offer.getPriceUnit());
            } else if (offer.getFinalAgreedPrice() > 0) {
                tvOfferPriceBreakdown.setText("Agreed: ₹" + (int) offer.getFinalAgreedPrice() + "/" + offer.getPriceUnit());
            } else {
                tvOfferPriceBreakdown.setText("Expected: ₹" + (int) offer.getExpectedPrice() + "/" + offer.getPriceUnit());
            }

            double totalVal = offer.calculateTotalValue(effPrice);
            tvOfferTotalValue.setText("Est. Total: " + currencyFormat.format(totalVal));

            // Negotiation note
            if (offer.getNegotiationNote() != null && !offer.getNegotiationNote().isEmpty()) {
                llNegotiationNote.setVisibility(View.VISIBLE);
                tvNegotiationNote.setText(offer.getNegotiationNote());
            } else {
                llNegotiationNote.setVisibility(View.GONE);
            }

            // Details
            tvOfferDetails.setText("📅 Available: " + (offer.getAvailableDate() != null ? offer.getAvailableDate() : "Immediate") +
                    " • Quality: " + (offer.getQualityDetails() != null ? offer.getQualityDetails() : "Standard quality"));

            // Actions Setup
            if ("Accepted".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status) || "Rejected".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status)) {
                llOfferActions.setVisibility(View.GONE);
            } else {
                llOfferActions.setVisibility(View.VISIBLE);

                if (isBrokerMode) {
                    btnCancelOffer.setText("Decline");
                    btnCounterOffer.setText("Counter");
                    btnAcceptOffer.setText("Accept Offer");
                } else {
                    btnCancelOffer.setText("Withdraw");
                    btnCounterOffer.setText("Counter");
                    btnAcceptOffer.setText("Accept Price");

                    // If Farmer placed the last offer, Accept might be disabled or hidden until broker counters
                    if ("FARMER".equalsIgnoreCase(offer.getLastActorRole()) && !"Negotiating".equalsIgnoreCase(status)) {
                        btnAcceptOffer.setVisibility(View.GONE);
                    } else {
                        btnAcceptOffer.setVisibility(View.VISIBLE);
                    }
                }

                btnAcceptOffer.setOnClickListener(v -> {
                    if (listener != null) listener.onAcceptClicked(offer);
                });

                btnCounterOffer.setOnClickListener(v -> {
                    if (listener != null) listener.onCounterClicked(offer);
                });

                btnCancelOffer.setOnClickListener(v -> {
                    if (listener != null) listener.onRejectOrWithdrawClicked(offer);
                });
            }
        }
    }
}
