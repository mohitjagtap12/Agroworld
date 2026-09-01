package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.model.ProductOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for Farmer to view history and status tracking of purchased farming products.
 */
public class FarmerProductOrderAdapter extends RecyclerView.Adapter<FarmerProductOrderAdapter.FarmerOrderViewHolder> {

    private List<ProductOrder> ordersList = new ArrayList<>();

    public void setOrders(List<ProductOrder> orders) {
        this.ordersList = (orders != null) ? new ArrayList<>(orders) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FarmerOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farmer_product_order, parent, false);
        return new FarmerOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmerOrderViewHolder holder, int position) {
        ProductOrder order = ordersList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class FarmerOrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFarmerOrderNumber;
        private final TextView tvFarmerOrderDate;
        private final TextView tvFarmerOrderStatus;
        private final TextView tvFarmerOrderEmoji;
        private final TextView tvFarmerOrderProductName;
        private final TextView tvFarmerOrderSellerName;
        private final TextView tvFarmerOrderQtyAndTotal;
        private final TextView tvFarmerOrderDeliveryStatus;
        private final TextView tvFarmerOrderAddress;

        public FarmerOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFarmerOrderNumber = itemView.findViewById(R.id.tvFarmerOrderNumber);
            tvFarmerOrderDate = itemView.findViewById(R.id.tvFarmerOrderDate);
            tvFarmerOrderStatus = itemView.findViewById(R.id.tvFarmerOrderStatus);
            tvFarmerOrderEmoji = itemView.findViewById(R.id.tvFarmerOrderEmoji);
            tvFarmerOrderProductName = itemView.findViewById(R.id.tvFarmerOrderProductName);
            tvFarmerOrderSellerName = itemView.findViewById(R.id.tvFarmerOrderSellerName);
            tvFarmerOrderQtyAndTotal = itemView.findViewById(R.id.tvFarmerOrderQtyAndTotal);
            tvFarmerOrderDeliveryStatus = itemView.findViewById(R.id.tvFarmerOrderDeliveryStatus);
            tvFarmerOrderAddress = itemView.findViewById(R.id.tvFarmerOrderAddress);
        }

        public void bind(ProductOrder order) {
            tvFarmerOrderNumber.setText(order.getOrderNumber());
            tvFarmerOrderDate.setText("Ordered: " + order.getOrderDate());
            tvFarmerOrderStatus.setText(order.getStatus());

            tvFarmerOrderEmoji.setText(order.getProductEmoji() != null ? order.getProductEmoji() : "📦");
            tvFarmerOrderProductName.setText(order.getProductName());
            tvFarmerOrderSellerName.setText("Seller: " + order.getSellerName());
            tvFarmerOrderQtyAndTotal.setText(String.format("%d %s • Total: ₹%.0f", order.getQuantity(), order.getUnit(), order.getTotalAmount()));

            String status = order.getStatus();
            if (ProductOrder.STATUS_ORDER_PLACED.equalsIgnoreCase(status)) {
                tvFarmerOrderStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_warning));
                tvFarmerOrderDeliveryStatus.setText("⏳ Awaiting seller confirmation & inventory dispatch");
            } else if (ProductOrder.STATUS_CONFIRMED.equalsIgnoreCase(status)) {
                tvFarmerOrderStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_primary));
                tvFarmerOrderDeliveryStatus.setText("✅ Order confirmed by seller. Packing in progress.");
            } else if (ProductOrder.STATUS_PACKED.equalsIgnoreCase(status)) {
                tvFarmerOrderStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_accent));
                tvFarmerOrderDeliveryStatus.setText("📦 Order packed and ready for AgroWorld Logistics pickup.");
            } else if (ProductOrder.STATUS_OUT_FOR_DELIVERY.equalsIgnoreCase(status)) {
                tvFarmerOrderStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_info));
                tvFarmerOrderDeliveryStatus.setText("🚚 Out for delivery by AgroWorld Partner to your farm.");
            } else if (ProductOrder.STATUS_DELIVERED.equalsIgnoreCase(status) || ProductOrder.STATUS_COMPLETED.equalsIgnoreCase(status)) {
                tvFarmerOrderStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_success));
                tvFarmerOrderDeliveryStatus.setText("🎉 Successfully delivered to your farm.");
            } else if (ProductOrder.STATUS_REJECTED.equalsIgnoreCase(status)) {
                tvFarmerOrderStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_error));
                tvFarmerOrderDeliveryStatus.setText("❌ Order could not be fulfilled by seller.");
            }

            tvFarmerOrderAddress.setText(String.format("📍 Delivery to: %s, %s, %s", order.getDeliveryAddress(), order.getVillage(), order.getDistrict()));
        }
    }
}
