package com.example.ui.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.model.FarmerProduceListing;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for Customer Store to browse fresh produce listings.
 */
public class CustomerProduceAdapter extends RecyclerView.Adapter<CustomerProduceAdapter.ProduceViewHolder> {

    public interface OnProduceClickListener {
        void onBuy(FarmerProduceListing listing);
        void onItemClick(FarmerProduceListing listing);
    }

    private final List<FarmerProduceListing> items = new ArrayList<>();
    private final OnProduceClickListener listener;

    public CustomerProduceAdapter(OnProduceClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<FarmerProduceListing> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProduceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer_produce, parent, false);
        return new ProduceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProduceViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ProduceViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEmoji;
        private final TextView tvProduceName;
        private final TextView tvFarmerName;
        private final TextView tvGrade;
        private final TextView tvStock;
        private final TextView tvLocation;
        private final TextView tvDesc;
        private final TextView tvPrice;
        private final MaterialButton btnBuy;

        public ProduceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tv_cust_produce_emoji);
            tvProduceName = itemView.findViewById(R.id.tv_cust_produce_name);
            tvFarmerName = itemView.findViewById(R.id.tv_cust_farmer_name);
            tvGrade = itemView.findViewById(R.id.tv_cust_produce_grade);
            tvStock = itemView.findViewById(R.id.tv_cust_produce_stock);
            tvLocation = itemView.findViewById(R.id.tv_cust_produce_location);
            tvDesc = itemView.findViewById(R.id.tv_cust_produce_desc);
            tvPrice = itemView.findViewById(R.id.tv_cust_produce_price);
            btnBuy = itemView.findViewById(R.id.btn_cust_buy_produce);
        }

        public void bind(FarmerProduceListing item) {
            tvEmoji.setText(item.getImageEmoji() != null ? item.getImageEmoji() : "🌾");
            tvProduceName.setText(item.getProduceName());
            tvFarmerName.setText("👨‍🌾 Farmer: " + item.getFarmerName());

            String grade = item.getQualityGrade() != null ? item.getQualityGrade() : "Grade A";
            tvGrade.setText(grade);

            tvStock.setText(item.getQuantityAvailable() + " " + item.getUnit() + " available");
            tvLocation.setText("📍 " + item.getFullLocation());

            if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                tvDesc.setText(item.getDescription());
                tvDesc.setVisibility(View.VISIBLE);
            } else {
                tvDesc.setVisibility(View.GONE);
            }

            tvPrice.setText("₹" + item.getPricePerKg() + " / " + item.getPriceUnit());

            if (item.getQuantityAvailable() <= 0 || "Sold Out".equalsIgnoreCase(item.getStatus())) {
                btnBuy.setEnabled(false);
                btnBuy.setText("Sold Out");
                btnBuy.setBackgroundColor(0xFFBDBDBD);
            } else {
                btnBuy.setEnabled(true);
                btnBuy.setText("Buy Direct");
                btnBuy.setBackgroundColor(0xFF2E7D32);
                btnBuy.setOnClickListener(v -> {
                    if (listener != null) listener.onBuy(item);
                });
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });
        }
    }
}
