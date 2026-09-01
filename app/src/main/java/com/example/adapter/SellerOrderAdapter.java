package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.model.ProductOrder;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for Seller to view incoming Farmer orders and manage fulfillment lifecycle.
 */
public class SellerOrderAdapter extends RecyclerView.Adapter<SellerOrderAdapter.SellerOrderViewHolder> {

    public interface OnOrderActionListener {
        void onAcceptOrder(ProductOrder order);
        void onRejectOrder(ProductOrder order);
        void onMarkPacked(ProductOrder order);
        void onReadyForDelivery(ProductOrder order);
    }

    private List<ProductOrder> ordersList = new ArrayList<>();
    private final OnOrderActionListener listener;

    public SellerOrderAdapter(OnOrderActionListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<ProductOrder> orders) {
        this.ordersList = (orders != null) ? new ArrayList<>(orders) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SellerOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seller_order, parent, false);
        return new SellerOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerOrderViewHolder holder, int position) {
        ProductOrder order = ordersList.get(position);
        holder.bind(order, listener);
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class SellerOrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderNumber;
        private final TextView tvOrderDate;
        private final TextView tvOrderStatusBadge;
        private final TextView tvOrderProductEmoji;
        private final TextView tvOrderProductName;
        private final TextView tvOrderQuantityAndPrice;
        private final TextView tvFarmerCustomerInfo;
        private final TextView tvDeliveryLocation;
        private final TextView tvPaymentAndDeliveryInfo;
        private final TextView tvOrderTotalAmount;

        private final LinearLayout layoutSellerOrderActions;
        private final MaterialButton btnRejectOrder;
        private final MaterialButton btnAcceptOrder;
        private final MaterialButton btnMarkPacked;
        private final MaterialButton btnReadyForDelivery;

        public SellerOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatusBadge = itemView.findViewById(R.id.tvOrderStatusBadge);
            tvOrderProductEmoji = itemView.findViewById(R.id.tvOrderProductEmoji);
            tvOrderProductName = itemView.findViewById(R.id.tvOrderProductName);
            tvOrderQuantityAndPrice = itemView.findViewById(R.id.tvOrderQuantityAndPrice);
            tvFarmerCustomerInfo = itemView.findViewById(R.id.tvFarmerCustomerInfo);
            tvDeliveryLocation = itemView.findViewById(R.id.tvDeliveryLocation);
            tvPaymentAndDeliveryInfo = itemView.findViewById(R.id.tvPaymentAndDeliveryInfo);
            tvOrderTotalAmount = itemView.findViewById(R.id.tvOrderTotalAmount);

            layoutSellerOrderActions = itemView.findViewById(R.id.layoutSellerOrderActions);
            btnRejectOrder = itemView.findViewById(R.id.btnRejectOrder);
            btnAcceptOrder = itemView.findViewById(R.id.btnAcceptOrder);
            btnMarkPacked = itemView.findViewById(R.id.btnMarkPacked);
            btnReadyForDelivery = itemView.findViewById(R.id.btnReadyForDelivery);
        }

        public void bind(ProductOrder order, OnOrderActionListener listener) {
            tvOrderNumber.setText(order.getOrderNumber());
            tvOrderDate.setText("Ordered on " + order.getOrderDate());
            tvOrderStatusBadge.setText(order.getStatus());

            tvOrderProductEmoji.setText(order.getProductEmoji() != null ? order.getProductEmoji() : "📦");
            tvOrderProductName.setText(order.getProductName());
            tvOrderQuantityAndPrice.setText(String.format("Qty: %d %s × ₹%.0f", order.getQuantity(), order.getUnit(), order.getPricePerUnit()));

            tvFarmerCustomerInfo.setText(String.format("Farmer: %s (%s)", order.getFarmerName(), order.getFarmerPhone()));
            tvDeliveryLocation.setText(String.format("📍 %s, %s (%s)", order.getDeliveryAddress(), order.getTaluka(), order.getDistrict()));

            tvPaymentAndDeliveryInfo.setText(String.format("🚚 %s • 💵 %s", order.getDeliveryMethod(), order.getPaymentMethod()));
            tvOrderTotalAmount.setText(String.format("₹%.0f", order.getTotalAmount()));

            // Reset action buttons
            btnRejectOrder.setVisibility(View.GONE);
            btnAcceptOrder.setVisibility(View.GONE);
            btnMarkPacked.setVisibility(View.GONE);
            btnReadyForDelivery.setVisibility(View.GONE);

            String status = order.getStatus();
            if (ProductOrder.STATUS_ORDER_PLACED.equalsIgnoreCase(status)) {
                tvOrderStatusBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_warning));
                btnRejectOrder.setVisibility(View.VISIBLE);
                btnAcceptOrder.setVisibility(View.VISIBLE);

                btnRejectOrder.setOnClickListener(v -> {
                    if (listener != null) listener.onRejectOrder(order);
                });

                btnAcceptOrder.setOnClickListener(v -> {
                    if (listener != null) listener.onAcceptOrder(order);
                });
            } else if (ProductOrder.STATUS_CONFIRMED.equalsIgnoreCase(status)) {
                tvOrderStatusBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_primary));
                btnMarkPacked.setVisibility(View.VISIBLE);

                btnMarkPacked.setOnClickListener(v -> {
                    if (listener != null) listener.onMarkPacked(order);
                });
            } else if (ProductOrder.STATUS_PACKED.equalsIgnoreCase(status)) {
                tvOrderStatusBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_accent));
                btnReadyForDelivery.setVisibility(View.VISIBLE);

                btnReadyForDelivery.setOnClickListener(v -> {
                    if (listener != null) listener.onReadyForDelivery(order);
                });
            } else if (ProductOrder.STATUS_OUT_FOR_DELIVERY.equalsIgnoreCase(status)) {
                tvOrderStatusBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_info));
            } else if (ProductOrder.STATUS_DELIVERED.equalsIgnoreCase(status) || ProductOrder.STATUS_COMPLETED.equalsIgnoreCase(status)) {
                tvOrderStatusBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_success));
            } else if (ProductOrder.STATUS_REJECTED.equalsIgnoreCase(status)) {
                tvOrderStatusBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_error));
            }
        }
    }
}
