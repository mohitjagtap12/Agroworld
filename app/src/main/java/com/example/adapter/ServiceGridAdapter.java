package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databinding.ItemFarmerServiceBinding;
import com.example.model.FarmerServiceItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the 9 Farmer Dashboard services displayed in a 2-column or 3-column Grid.
 */
public class ServiceGridAdapter extends RecyclerView.Adapter<ServiceGridAdapter.ServiceViewHolder> {

    public interface OnServiceClickListener {
        void onServiceClick(FarmerServiceItem service);
    }

    private final List<FarmerServiceItem> items = new ArrayList<>();
    private final OnServiceClickListener listener;

    public ServiceGridAdapter(OnServiceClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<FarmerServiceItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFarmerServiceBinding binding = ItemFarmerServiceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ServiceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        private final ItemFarmerServiceBinding binding;

        public ServiceViewHolder(ItemFarmerServiceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final FarmerServiceItem item) {
            binding.tvServiceIcon.setText(item.getIconEmoji());
            binding.tvServiceTitle.setText(item.getTitle());
            binding.tvServiceDescription.setText(item.getDescription());

            if (item.getBadgeText() != null && !item.getBadgeText().isEmpty()) {
                binding.tvServiceBadge.setText(item.getBadgeText());
                binding.tvServiceBadge.setVisibility(View.VISIBLE);
            } else {
                binding.tvServiceBadge.setVisibility(View.GONE);
            }

            binding.cardService.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onServiceClick(item);
                }
            });
        }
    }
}
