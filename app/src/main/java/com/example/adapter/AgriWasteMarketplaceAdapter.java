package com.example.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databinding.ItemBuyerWasteCardBinding;
import com.example.model.AgriWasteItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for Buyer Marketplace listings.
 */
public class AgriWasteMarketplaceAdapter extends RecyclerView.Adapter<AgriWasteMarketplaceAdapter.MarketplaceViewHolder> {

    private List<AgriWasteItem> items = new ArrayList<>();
    private OnMarketplaceActionListener listener;

    public interface OnMarketplaceActionListener {
        void onViewDetails(AgriWasteItem item);
        void onBuyWaste(AgriWasteItem item);
    }

    public void setOnMarketplaceActionListener(OnMarketplaceActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<AgriWasteItem> newItems) {
        this.items = newItems != null ? new ArrayList<>(newItems) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MarketplaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBuyerWasteCardBinding binding = ItemBuyerWasteCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MarketplaceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketplaceViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MarketplaceViewHolder extends RecyclerView.ViewHolder {
        private final ItemBuyerWasteCardBinding binding;

        MarketplaceViewHolder(@NonNull ItemBuyerWasteCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AgriWasteItem item) {
            binding.tvBuyerItemEmoji.setText(item.getImageEmoji() != null ? item.getImageEmoji() : "🌾");
            binding.tvBuyerItemName.setText(item.getWasteName());
            binding.tvBuyerItemFarmer.setText(String.format(Locale.ENGLISH, "👨‍🌾 %s • %s", item.getFarmerName(), item.getVillage()));
            binding.tvBuyerItemDistance.setText(String.format(Locale.ENGLISH, "%.1f km", item.getDistanceKm()));
            binding.tvBuyerItemQty.setText(String.format(Locale.ENGLISH, "%.1f %s", item.getQuantity(), item.getUnit()));
            binding.tvBuyerItemPrice.setText(String.format(Locale.ENGLISH, "₹%.0f / %s", item.getPrice(), item.getPriceUnit()));
            binding.tvBuyerItemDesc.setText(item.getDescription());

            binding.btnBuyerItemDetails.setOnClickListener(v -> {
                if (listener != null) listener.onViewDetails(item);
            });
            binding.btnBuyerItemBuy.setOnClickListener(v -> {
                if (listener != null) listener.onBuyWaste(item);
            });
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onViewDetails(item);
            });
        }
    }
}
