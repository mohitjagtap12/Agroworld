package com.example.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.model.DeliveryJob;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Recycler Adapter for displaying and interacting with Unified Delivery Jobs across:
 * - 🏪 Seller Product Orders
 * - 🌾 Farm Fresh Produce Orders
 * - ♻️ Agri Waste Orders
 * - 📈 Wholesale Broker Deals
 */
public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder> {

    public interface OnDeliveryActionListener {
        void onAccept(DeliveryJob job);
        void onDecline(DeliveryJob job);
        void onSchedulePickup(DeliveryJob job);
        void onMarkPickedUp(DeliveryJob job);
        void onStartInTransit(DeliveryJob job);
        void onDeliver(DeliveryJob job);
        void onCallPhone(String phone);
        void onItemClick(DeliveryJob job);
    }

    private final Context context;
    private final List<DeliveryJob> jobsList = new ArrayList<>();
    private final OnDeliveryActionListener listener;

    public DeliveryAdapter(Context context, OnDeliveryActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setJobs(List<DeliveryJob> newJobs) {
        jobsList.clear();
        if (newJobs != null) {
            jobsList.addAll(newJobs);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delivery_job, parent, false);
        return new DeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        DeliveryJob job = jobsList.get(position);
        holder.bind(job, listener);
    }

    @Override
    public int getItemCount() {
        return jobsList.size();
    }

    static class DeliveryViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvOrderTypeBadge;
        private final TextView tvDeliveryStatusBadge;
        private final TextView tvDeliveryJobId;
        private final TextView tvOrderIdRef;
        private final TextView tvDeliveryFee;
        private final TextView tvItemEmoji;
        private final TextView tvItemsSummary;
        private final TextView tvQuantityAndDistance;
        private final TextView tvSourceName;
        private final TextView tvSourceAddress;
        private final TextView tvDestinationName;
        private final TextView tvDestinationAddress;
        private final ImageButton btnCallSource;
        private final ImageButton btnCallDestination;
        private final TextView tvVehicleRequired;
        private final TextView tvPickupTime;
        private final TextView tvDeliveryNotes;
        private final MaterialButton btnDeclineJob;
        private final MaterialButton btnPrimaryAction;

        public DeliveryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderTypeBadge = itemView.findViewById(R.id.tvOrderTypeBadge);
            tvDeliveryStatusBadge = itemView.findViewById(R.id.tvDeliveryStatusBadge);
            tvDeliveryJobId = itemView.findViewById(R.id.tvDeliveryJobId);
            tvOrderIdRef = itemView.findViewById(R.id.tvOrderIdRef);
            tvDeliveryFee = itemView.findViewById(R.id.tvDeliveryFee);
            tvItemEmoji = itemView.findViewById(R.id.tvItemEmoji);
            tvItemsSummary = itemView.findViewById(R.id.tvItemsSummary);
            tvQuantityAndDistance = itemView.findViewById(R.id.tvQuantityAndDistance);
            tvSourceName = itemView.findViewById(R.id.tvSourceName);
            tvSourceAddress = itemView.findViewById(R.id.tvSourceAddress);
            tvDestinationName = itemView.findViewById(R.id.tvDestinationName);
            tvDestinationAddress = itemView.findViewById(R.id.tvDestinationAddress);
            btnCallSource = itemView.findViewById(R.id.btnCallSource);
            btnCallDestination = itemView.findViewById(R.id.btnCallDestination);
            tvVehicleRequired = itemView.findViewById(R.id.tvVehicleRequired);
            tvPickupTime = itemView.findViewById(R.id.tvPickupTime);
            tvDeliveryNotes = itemView.findViewById(R.id.tvDeliveryNotes);
            btnDeclineJob = itemView.findViewById(R.id.btnDeclineJob);
            btnPrimaryAction = itemView.findViewById(R.id.btnPrimaryAction);
        }

        public void bind(DeliveryJob job, OnDeliveryActionListener listener) {
            // 1. Order Type Badge Styling
            bindOrderTypeBadge(job);

            // 2. Status Badge Styling
            bindStatusBadge(job);

            // 3. ID and Fee
            tvDeliveryJobId.setText(job.getId() != null ? job.getId() : "DEL-JOB");
            tvOrderIdRef.setText("Ref: " + (job.getOrderId() != null ? job.getOrderId() : "N/A"));
            tvDeliveryFee.setText("₹" + String.format("%.0f", job.getDeliveryFee()) + " Payout");

            // 4. Emoji & Items Summary
            tvItemEmoji.setText(job.getItemEmoji() != null ? job.getItemEmoji() : "📦");
            tvItemsSummary.setText(job.getItemsSummary() != null ? job.getItemsSummary() : "Delivery Package");
            tvQuantityAndDistance.setText("Weight: " + job.getQuantity() + " " + (job.getUnit() != null ? job.getUnit() : "kg") +
                    " • Approx " + String.format("%.1f", job.getDistanceKm()) + " km");

            // 5. Source & Destination
            tvSourceName.setText(job.getSourceName() != null ? job.getSourceName() : "Pickup Location");
            tvSourceAddress.setText(job.getSourceLocation() != null ? job.getSourceLocation() : "");
            tvDestinationName.setText(job.getDestinationName() != null ? job.getDestinationName() : "Delivery Destination");
            tvDestinationAddress.setText(job.getDestinationLocation() != null ? job.getDestinationLocation() : "");

            // 6. Vehicle and Pickup Time
            tvVehicleRequired.setText("🚚 " + (job.getVehicleRequired() != null ? job.getVehicleRequired() : "Pickup Truck"));
            tvPickupTime.setText("Pickup: " + (job.getPickupDate() != null ? job.getPickupDate() : "Immediate"));

            // 7. Delivery Notes
            if (job.getDeliveryNotes() != null && !job.getDeliveryNotes().trim().isEmpty()) {
                tvDeliveryNotes.setVisibility(View.VISIBLE);
                tvDeliveryNotes.setText("Note: " + job.getDeliveryNotes());
            } else {
                tvDeliveryNotes.setVisibility(View.GONE);
            }

            // 8. Phone Call Buttons
            btnCallSource.setOnClickListener(v -> {
                if (listener != null && job.getSourcePhone() != null) {
                    listener.onCallPhone(job.getSourcePhone());
                }
            });

            btnCallDestination.setOnClickListener(v -> {
                if (listener != null && job.getDestinationPhone() != null) {
                    listener.onCallPhone(job.getDestinationPhone());
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(job);
                }
            });

            // 9. Status-Driven Action Button Logic
            bindActionButtons(job, listener);
        }

        private void bindOrderTypeBadge(DeliveryJob job) {
            String type = job.getOrderType();
            if (DeliveryJob.TYPE_SELLER_PRODUCT.equalsIgnoreCase(type)) {
                tvOrderTypeBadge.setText("🏪 Seller Product");
                tvOrderTypeBadge.setBackgroundResource(R.drawable.bg_chip_status_green);
                tvOrderTypeBadge.setTextColor(Color.parseColor("#2E7D32"));
            } else if (DeliveryJob.TYPE_FARM_PRODUCE.equalsIgnoreCase(type)) {
                tvOrderTypeBadge.setText("🌾 Fresh Produce");
                tvOrderTypeBadge.setBackgroundResource(R.drawable.bg_chip_status_blue);
                tvOrderTypeBadge.setTextColor(Color.parseColor("#0061A4"));
            } else if (DeliveryJob.TYPE_AGRI_WASTE.equalsIgnoreCase(type)) {
                tvOrderTypeBadge.setText("♻️ Agri Waste");
                tvOrderTypeBadge.setBackgroundResource(R.drawable.bg_chip_status_purple);
                tvOrderTypeBadge.setTextColor(Color.parseColor("#8E24AA"));
            } else if (DeliveryJob.TYPE_BROKER_DEAL.equalsIgnoreCase(type)) {
                tvOrderTypeBadge.setText("📈 Broker Mandi");
                tvOrderTypeBadge.setBackgroundResource(R.drawable.bg_chip_status_gold);
                tvOrderTypeBadge.setTextColor(Color.parseColor("#E65100"));
            } else {
                tvOrderTypeBadge.setText("📦 Logistics");
                tvOrderTypeBadge.setBackgroundResource(R.drawable.bg_chip_status_gray);
                tvOrderTypeBadge.setTextColor(Color.parseColor("#616161"));
            }
        }

        private void bindStatusBadge(DeliveryJob job) {
            String status = job.getStatus() != null ? job.getStatus() : DeliveryJob.STATUS_AVAILABLE;
            tvDeliveryStatusBadge.setText(status.replace("_", " "));

            switch (status.toUpperCase()) {
                case DeliveryJob.STATUS_AVAILABLE:
                case DeliveryJob.STATUS_CREATED:
                    tvDeliveryStatusBadge.setBackgroundResource(R.drawable.bg_chip_status_orange);
                    tvDeliveryStatusBadge.setTextColor(Color.parseColor("#E65100"));
                    break;
                case DeliveryJob.STATUS_ASSIGNED:
                case DeliveryJob.STATUS_PICKUP_SCHEDULED:
                    tvDeliveryStatusBadge.setBackgroundResource(R.drawable.bg_chip_status_blue);
                    tvDeliveryStatusBadge.setTextColor(Color.parseColor("#0061A4"));
                    break;
                case DeliveryJob.STATUS_PICKED_UP:
                case DeliveryJob.STATUS_IN_TRANSIT:
                    tvDeliveryStatusBadge.setBackgroundResource(R.drawable.bg_chip_status_purple);
                    tvDeliveryStatusBadge.setTextColor(Color.parseColor("#8E24AA"));
                    break;
                case DeliveryJob.STATUS_DELIVERED:
                case DeliveryJob.STATUS_COMPLETED:
                    tvDeliveryStatusBadge.setBackgroundResource(R.drawable.bg_chip_status_green);
                    tvDeliveryStatusBadge.setTextColor(Color.parseColor("#2E7D32"));
                    break;
                default:
                    tvDeliveryStatusBadge.setBackgroundResource(R.drawable.bg_chip_status_gray);
                    tvDeliveryStatusBadge.setTextColor(Color.parseColor("#616161"));
                    break;
            }
        }

        private void bindActionButtons(DeliveryJob job, OnDeliveryActionListener listener) {
            String status = job.getStatus() != null ? job.getStatus() : DeliveryJob.STATUS_AVAILABLE;

            switch (status.toUpperCase()) {
                case DeliveryJob.STATUS_AVAILABLE:
                case DeliveryJob.STATUS_CREATED:
                    btnDeclineJob.setVisibility(View.VISIBLE);
                    btnPrimaryAction.setVisibility(View.VISIBLE);
                    btnPrimaryAction.setText("Accept Delivery");
                    btnDeclineJob.setText("Decline");
                    btnDeclineJob.setOnClickListener(v -> {
                        if (listener != null) listener.onDecline(job);
                    });
                    btnPrimaryAction.setOnClickListener(v -> {
                        if (listener != null) listener.onAccept(job);
                    });
                    break;

                case DeliveryJob.STATUS_ASSIGNED:
                    btnDeclineJob.setVisibility(View.VISIBLE);
                    btnDeclineJob.setText("Schedule");
                    btnPrimaryAction.setVisibility(View.VISIBLE);
                    btnPrimaryAction.setText("Mark Picked Up");
                    btnDeclineJob.setOnClickListener(v -> {
                        if (listener != null) listener.onSchedulePickup(job);
                    });
                    btnPrimaryAction.setOnClickListener(v -> {
                        if (listener != null) listener.onMarkPickedUp(job);
                    });
                    break;

                case DeliveryJob.STATUS_PICKUP_SCHEDULED:
                    btnDeclineJob.setVisibility(View.GONE);
                    btnPrimaryAction.setVisibility(View.VISIBLE);
                    btnPrimaryAction.setText("Confirm Goods Picked Up");
                    btnPrimaryAction.setOnClickListener(v -> {
                        if (listener != null) listener.onMarkPickedUp(job);
                    });
                    break;

                case DeliveryJob.STATUS_PICKED_UP:
                    btnDeclineJob.setVisibility(View.GONE);
                    btnPrimaryAction.setVisibility(View.VISIBLE);
                    btnPrimaryAction.setText("Start In-Transit Route");
                    btnPrimaryAction.setOnClickListener(v -> {
                        if (listener != null) listener.onStartInTransit(job);
                    });
                    break;

                case DeliveryJob.STATUS_IN_TRANSIT:
                    btnDeclineJob.setVisibility(View.GONE);
                    btnPrimaryAction.setVisibility(View.VISIBLE);
                    btnPrimaryAction.setText("Confirm Handover & Deliver");
                    btnPrimaryAction.setOnClickListener(v -> {
                        if (listener != null) listener.onDeliver(job);
                    });
                    break;

                case DeliveryJob.STATUS_DELIVERED:
                case DeliveryJob.STATUS_COMPLETED:
                    btnDeclineJob.setVisibility(View.GONE);
                    btnPrimaryAction.setVisibility(View.VISIBLE);
                    btnPrimaryAction.setText("✓ Completed (View Slip)");
                    btnPrimaryAction.setOnClickListener(v -> {
                        if (listener != null) listener.onItemClick(job);
                    });
                    break;

                default:
                    btnDeclineJob.setVisibility(View.GONE);
                    btnPrimaryAction.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
