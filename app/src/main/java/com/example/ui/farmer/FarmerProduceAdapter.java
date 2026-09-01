package com.example.ui.farmer;

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
 * Adapter for displaying Farmer's direct produce listings.
 */
public class FarmerProduceAdapter extends RecyclerView.Adapter<FarmerProduceAdapter.ProduceViewHolder> {

    public interface OnProduceActionListener {
        void onEdit(FarmerProduceListing listing);
        void onPauseResume(FarmerProduceListing listing);
        void onDelete(FarmerProduceListing listing);
        void onItemClick(FarmerProduceListing listing);
    }

    private final List<FarmerProduceListing> items = new ArrayList<>();
    private final OnProduceActionListener listener;

    public FarmerProduceAdapter(OnProduceActionListener listener) {
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
                .inflate(R.layout.item_farmer_produce, parent, false);
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
        private final TextView tvCategoryGrade;
        private final TextView tvStatus;
        private final TextView tvQuantity;
        private final TextView tvPrice;
        private final TextView tvLocation;
        private final TextView tvHarvestDate;
        private final TextView tvDescription;
        private final MaterialButton btnPause;
        private final MaterialButton btnEdit;
        private final MaterialButton btnDelete;

        public ProduceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tv_produce_emoji);
            tvProduceName = itemView.findViewById(R.id.tv_produce_name);
            tvCategoryGrade = itemView.findViewById(R.id.tv_produce_category_grade);
            tvStatus = itemView.findViewById(R.id.tv_produce_status);
            tvQuantity = itemView.findViewById(R.id.tv_produce_quantity);
            tvPrice = itemView.findViewById(R.id.tv_produce_price);
            tvLocation = itemView.findViewById(R.id.tv_produce_location);
            tvHarvestDate = itemView.findViewById(R.id.tv_produce_harvest_date);
            tvDescription = itemView.findViewById(R.id.tv_produce_description);
            btnPause = itemView.findViewById(R.id.btn_pause_produce);
            btnEdit = itemView.findViewById(R.id.btn_edit_produce);
            btnDelete = itemView.findViewById(R.id.btn_delete_produce);
        }

        public void bind(FarmerProduceListing item) {
            if (item.getImageEmoji() != null && !item.getImageEmoji().isEmpty()) {
                tvEmoji.setText(item.getImageEmoji());
            } else {
                tvEmoji.setText("🌾");
            }

            tvProduceName.setText(item.getProduceName());

            String category = item.getCategory() != null ? item.getCategory() : "Produce";
            String grade = item.getQualityGrade() != null ? item.getQualityGrade() : "Grade A";
            tvCategoryGrade.setText(category + " • " + grade);

            String status = item.getStatus() != null ? item.getStatus() : "Available";
            tvStatus.setText(status);

            if ("Paused".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_orange);
                tvStatus.setTextColor(0xFFE65100);
                btnPause.setText("Resume");
                btnPause.setTextColor(0xFF2E7D32);
                btnPause.setStrokeColorResource(R.color.forest_green);
            } else if ("Sold Out".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_gray);
                tvStatus.setTextColor(0xFF757575);
                btnPause.setText("Pause");
                btnPause.setTextColor(0xFFE65100);
                btnPause.setStrokeColorResource(android.R.color.holo_orange_dark);
            } else {
                tvStatus.setBackgroundResource(R.drawable.bg_chip_status_green);
                tvStatus.setTextColor(0xFF2E7D32);
                btnPause.setText("Pause");
                btnPause.setTextColor(0xFFE65100);
                btnPause.setStrokeColorResource(android.R.color.holo_orange_dark);
            }

            tvQuantity.setText(item.getQuantityAvailable() + " " + item.getUnit());
            tvPrice.setText("₹" + item.getPricePerKg() + " / " + item.getPriceUnit());

            tvLocation.setText(item.getFullLocation());
            if (item.getHarvestDate() != null && !item.getHarvestDate().isEmpty()) {
                tvHarvestDate.setText("Harvest: " + item.getHarvestDate());
                tvHarvestDate.setVisibility(View.VISIBLE);
            } else {
                tvHarvestDate.setVisibility(View.GONE);
            }

            if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                tvDescription.setText(item.getDescription());
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            btnPause.setOnClickListener(v -> {
                if (listener != null) listener.onPauseResume(item);
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(item);
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(item);
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });
        }
    }
}
