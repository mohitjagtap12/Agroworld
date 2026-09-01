package com.example.ui.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.model.FarmerDirectOrder;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for Customer Orders list with tracking and cancellation.
 */
public class CustomerOrdersAdapter extends RecyclerView.Adapter<CustomerOrdersAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onTrack(FarmerDirectOrder order);
        void onCancel(FarmerDirectOrder order);
        void onItemClick(FarmerDirectOrder order);
    }

    private final List<FarmerDirectOrder> orders = new ArrayList<>();
    private final OnOrderClickListener listener;

    public CustomerOrdersAdapter(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<FarmerDirectOrder> newOrders) {
        orders.clear();
        if (newOrders != null) {
            orders.addAll(newOrders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEmoji;
        private final TextView tvOrderId;
        private final TextView tvProduceName;
        private final TextView tvStatus;
        private final TextView tvQuantity;
        private final TextView tvFarmer;
        private final TextView tvTotal;
        private final TextView tvAddress;
        private final TextView tvDate;
        private final TextView tvPaymentMode;
        private final MaterialButton btnCancel;
        private final MaterialButton btnTrack;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tv_cust_order_emoji);
            tvOrderId = itemView.findViewById(R.id.tv_cust_order_id);
            tvProduceName = itemView.findViewById(R.id.tv_cust_order_produce_name);
            tvStatus = itemView.findViewById(R.id.tv_cust_order_status);
            tvQuantity = itemView.findViewById(R.id.tv_cust_order_quantity);
            tvFarmer = itemView.findViewById(R.id.tv_cust_order_farmer);
            tvTotal = itemView.findViewById(R.id.tv_cust_order_total);
            tvAddress = itemView.findViewById(R.id.tv_cust_order_address);
            tvDate = itemView.findViewById(R.id.tv_cust_order_date);
            tvPaymentMode = itemView.findViewById(R.id.tv_cust_order_payment_mode);
            btnCancel = itemView.findViewById(R.id.btn_cust_order_cancel);
            btnTrack = itemView.findViewById(R.id.btn_cust_order_track);
        }

        public void bind(FarmerDirectOrder order) {
            tvEmoji.setText(order.getImageEmoji() != null ? order.getImageEmoji() : "🌾");
            tvOrderId.setText(order.getId());
            tvProduceName.setText(order.getProduceName());

            String status = order.getStatus() != null ? order.getStatus() : "Order Placed";
            tvStatus.setText(status);

            // Status chip colors
            if ("Order Placed".equalsIgnoreCase(status) || "Pending".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_orange);
                tvStatus.setTextColor(0xFFE65100);
                btnCancel.setVisibility(View.VISIBLE);
            } else if ("Accepted".equalsIgnoreCase(status) || "Preparing".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_blue);
                tvStatus.setTextColor(0xFF1976D2);
                btnCancel.setVisibility(View.GONE);
            } else if ("Ready for Pickup".equalsIgnoreCase(status) || "Out for Delivery".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_blue);
                tvStatus.setTextColor(0xFF1976D2);
                btnCancel.setVisibility(View.GONE);
            } else if ("Delivered".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_green);
                tvStatus.setTextColor(0xFF2E7D32);
                btnCancel.setVisibility(View.GONE);
            } else {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_gray);
                tvStatus.setTextColor(0xFF757575);
                btnCancel.setVisibility(View.GONE);
            }

            tvQuantity.setText(order.getQuantity() + " " + order.getUnit());
            tvFarmer.setText(order.getFarmerName());
            tvTotal.setText("₹" + order.getTotalPrice());
            tvAddress.setText("📍 Delivery: " + order.getDeliveryAddress());
            tvDate.setText("📅 " + order.getOrderDate());
            tvPaymentMode.setText(order.getPaymentStatus() != null ? "💳 " + order.getPaymentStatus() : "💳 Paid Online");

            btnCancel.setOnClickListener(v -> {
                if (listener != null) listener.onCancel(order);
            });

            btnTrack.setOnClickListener(v -> {
                if (listener != null) listener.onTrack(order);
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(order);
            });
        }
    }
}
