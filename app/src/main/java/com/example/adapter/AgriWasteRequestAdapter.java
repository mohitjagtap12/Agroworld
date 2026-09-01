package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databinding.ItemAgriWasteRequestBinding;
import com.example.model.AgriWastePurchaseRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for incoming Buyer Requests shown to the Farmer.
 */
public class AgriWasteRequestAdapter extends RecyclerView.Adapter<AgriWasteRequestAdapter.RequestViewHolder> {

    private List<AgriWastePurchaseRequest> requests = new ArrayList<>();
    private OnBuyerRequestActionListener listener;

    public interface OnBuyerRequestActionListener {
        void onAccept(AgriWastePurchaseRequest request);
        void onReject(AgriWastePurchaseRequest request);
    }

    public void setOnBuyerRequestActionListener(OnBuyerRequestActionListener listener) {
        this.listener = listener;
    }

    public void setRequests(List<AgriWastePurchaseRequest> newRequests) {
        this.requests = newRequests != null ? new ArrayList<>(newRequests) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAgriWasteRequestBinding binding = ItemAgriWasteRequestBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        holder.bind(requests.get(position));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        private final ItemAgriWasteRequestBinding binding;

        RequestViewHolder(@NonNull ItemAgriWasteRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AgriWastePurchaseRequest req) {
            binding.tvBuyerWasteTitle.setText(String.format(Locale.ENGLISH, "%s (%.1f %s)",
                    req.getWasteName(), req.getRequestedQuantity(), req.getUnit()));
            binding.tvBuyerNameType.setText(String.format(Locale.ENGLISH, "🏢 %s • %s",
                    req.getBuyerName(), req.getBuyerType()));
            binding.chipRequestStatus.setText(req.getStatus());
            binding.tvReqQuantity.setText(String.format(Locale.ENGLISH, "%.1f %s @ ₹%.0f/%s",
                    req.getRequestedQuantity(), req.getUnit(), req.getOfferedPrice(), req.getUnit().toLowerCase()));
            binding.tvReqTotalAmount.setText(String.format(Locale.ENGLISH, "₹%.0f", req.getTotalAmount()));
            binding.tvReqPickupDetails.setText(String.format(Locale.ENGLISH, "🚚 %s • Date: %s",
                    req.getNotes() != null && !req.getNotes().isEmpty() ? req.getNotes() : "Standard Pickup", req.getProposedPickupDate()));
            binding.tvReqBuyerAddress.setText(String.format(Locale.ENGLISH, "📍 Delivery: %s",
                    req.getDeliveryAddress()));

            if ("Waiting for Farmer".equalsIgnoreCase(req.getStatus()) || "Pending".equalsIgnoreCase(req.getStatus())) {
                binding.layoutRequestActions.setVisibility(View.VISIBLE);
                binding.btnAcceptBuyerRequest.setOnClickListener(v -> {
                    if (listener != null) listener.onAccept(req);
                });
                binding.btnRejectBuyerRequest.setOnClickListener(v -> {
                    if (listener != null) listener.onReject(req);
                });
            } else {
                binding.layoutRequestActions.setVisibility(View.GONE);
            }
        }
    }
}
