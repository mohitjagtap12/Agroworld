package com.example.ui.farmer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.model.FarmerDirectOrder;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying customer orders placed directly for farmer produce.
 */
public class FarmerProduceOrdersAdapter extends RecyclerView.Adapter<FarmerProduceOrdersAdapter.OrderViewHolder> {

    public interface OnOrderActionListener {
        void onStatusChange(FarmerDirectOrder order, String nextStatus);
        void onReject(FarmerDirectOrder order);
        void onItemClick(FarmerDirectOrder order);
    }

    private final List<FarmerDirectOrder> orders = new ArrayList<>();
    private final OnOrderActionListener listener;

    public FarmerProduceOrdersAdapter(OnOrderActionListener listener) {
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
                .inflate(R.layout.item_farmer_direct_order, parent, false);
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
        private final TextView tvProduceTitle;
        private final TextView tvStatus;
        private final TextView tvQty;
        private final TextView tvUnitPrice;
        private final TextView tvTotalPrice;
        private final TextView tvCustomerName;
        private final TextView tvCustomerPhone;
        private final TextView tvDeliveryAddress;
        private final TextView tvOrderDate;
        private final TextView tvPaymentStatus;
        private final LinearLayout layoutActions;
        private final MaterialButton btnReject;
        private final MaterialButton btnPrimaryAction;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tv_order_emoji);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvProduceTitle = itemView.findViewById(R.id.tv_order_produce_title);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            tvQty = itemView.findViewById(R.id.tv_order_qty);
            tvUnitPrice = itemView.findViewById(R.id.tv_order_unit_price);
            tvTotalPrice = itemView.findViewById(R.id.tv_order_total_price);
            tvCustomerName = itemView.findViewById(R.id.tv_order_customer_name);
            tvCustomerPhone = itemView.findViewById(R.id.tv_order_customer_phone);
            tvDeliveryAddress = itemView.findViewById(R.id.tv_order_delivery_address);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvPaymentStatus = itemView.findViewById(R.id.tv_order_payment_status);
            layoutActions = itemView.findViewById(R.id.layout_order_actions);
            btnReject = itemView.findViewById(R.id.btn_order_reject);
            btnPrimaryAction = itemView.findViewById(R.id.btn_order_primary_action);
        }

        public void bind(FarmerDirectOrder order) {
            tvEmoji.setText(order.getImageEmoji() != null ? order.getImageEmoji() : "🌾");
            tvOrderId.setText(order.getId());
            tvProduceTitle.setText(order.getProduceName());

            String status = order.getStatus() != null ? order.getStatus() : "Order Placed";
            tvStatus.setText(status);

            // Configure status chip & actions
            if ("Order Placed".equalsIgnoreCase(status) || "Pending".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_orange);
                tvStatus.setTextColor(0xFFE65100);
                layoutActions.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.VISIBLE);
                btnPrimaryAction.setText("Accept Order");
                btnPrimaryAction.setOnClickListener(v -> {
                    if (listener != null) listener.onStatusChange(order, "Accepted");
                });
                btnReject.setOnClickListener(v -> {
                    if (listener != null) listener.onReject(order);
                });
            } else if ("Accepted".equalsIgnoreCase(status) || "Confirmed".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_green);
                tvStatus.setTextColor(0xFF2E7D32);
                layoutActions.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.GONE);
                btnPrimaryAction.setText("Start Packing");
                btnPrimaryAction.setOnClickListener(v -> {
                    if (listener != null) listener.onStatusChange(order, "Preparing");
                });
            } else if ("Preparing".equalsIgnoreCase(status) || "Packed".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_blue);
                tvStatus.setTextColor(0xFF1976D2);
                layoutActions.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.GONE);
                btnPrimaryAction.setText("Mark Ready for Pickup");
                btnPrimaryAction.setOnClickListener(v -> {
                    if (listener != null) listener.onStatusChange(order, "Ready for Pickup");
                });
            } else if ("Ready for Pickup".equalsIgnoreCase(status) || "Ready".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_blue);
                tvStatus.setTextColor(0xFF1976D2);
                layoutActions.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.GONE);
                btnPrimaryAction.setText("Out for Delivery");
                btnPrimaryAction.setOnClickListener(v -> {
                    if (listener != null) listener.onStatusChange(order, "Out for Delivery");
                });
            } else if ("Out for Delivery".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_orange);
                tvStatus.setTextColor(0xFFE65100);
                layoutActions.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.GONE);
                btnPrimaryAction.setText("Mark Delivered");
                btnPrimaryAction.setOnClickListener(v -> {
                    if (listener != null) listener.onStatusChange(order, "Delivered");
                });
            } else if ("Delivered".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_green);
                tvStatus.setTextColor(0xFF2E7D32);
                layoutActions.setVisibility(View.GONE);
            } else { // Cancelled or Rejected
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_gray);
                tvStatus.setTextColor(0xFF757575);
                layoutActions.setVisibility(View.GONE);
            }

            tvQty.setText(order.getQuantity() + " " + order.getUnit());
            tvUnitPrice.setText("₹" + order.getPricePerUnit() + "/" + order.getUnit());
            tvTotalPrice.setText("₹" + order.getTotalPrice());

            tvCustomerName.setText(order.getCustomerName());
            tvCustomerPhone.setText(order.getCustomerPhone());
            tvDeliveryAddress.setText(order.getDeliveryAddress());
            tvOrderDate.setText("Ordered: " + order.getOrderDate());
            tvPaymentStatus.setText(order.getPaymentStatus() != null ? "💳 " + order.getPaymentStatus() : "💳 Paid Online");

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(order);
            });
        }
    }
}
