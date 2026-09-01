package com.example.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databinding.ItemAgriWasteCardBinding;
import com.example.model.AgriWasteItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for Farmer's own Agri Waste listings.
 */
public class AgriWasteAdapter extends RecyclerView.Adapter<AgriWasteAdapter.WasteViewHolder> {

    private List<AgriWasteItem> listings = new ArrayList<>();
    private OnWasteItemActionListener listener;

    public interface OnWasteItemActionListener {
        void onView(AgriWasteItem item);
        void onEdit(AgriWasteItem item);
        void onDelete(AgriWasteItem item);
    }

    public void setOnWasteItemActionListener(OnWasteItemActionListener listener) {
        this.listener = listener;
    }

    public void setListings(List<AgriWasteItem> newListings) {
        this.listings = newListings != null ? new ArrayList<>(newListings) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WasteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAgriWasteCardBinding binding = ItemAgriWasteCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new WasteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WasteViewHolder holder, int position) {
        holder.bind(listings.get(position));
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    class WasteViewHolder extends RecyclerView.ViewHolder {
        private final ItemAgriWasteCardBinding binding;

        WasteViewHolder(@NonNull ItemAgriWasteCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AgriWasteItem item) {
            binding.tvWasteImageEmoji.setText(item.getImageEmoji() != null ? item.getImageEmoji() : "🌾");
            binding.tvWasteName.setText(item.getWasteName());
            binding.tvWasteCategory.setText("Category: " + item.getWasteType());
            binding.chipWasteStatus.setText(item.getStatus());
            binding.tvWasteQuantity.setText(String.format(Locale.ENGLISH, "%.1f %s", item.getQuantity(), item.getUnit()));
            binding.tvWastePrice.setText(String.format(Locale.ENGLISH, "₹%.0f / %s", item.getPrice(), item.getPriceUnit()));
            binding.tvWasteLocation.setText(String.format(Locale.ENGLISH, "📍 %s, %s • Pickup: %s",
                    item.getVillage(), item.getTaluka(), item.getPickupPreference()));

            binding.btnViewWaste.setOnClickListener(v -> {
                if (listener != null) listener.onView(item);
            });
            binding.btnEditWaste.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(item);
            });
            binding.btnDeleteWaste.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(item);
            });
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onView(item);
            });
        }
    }
}
