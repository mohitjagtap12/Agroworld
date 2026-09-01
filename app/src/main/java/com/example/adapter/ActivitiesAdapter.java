package com.example.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.databinding.ItemActivityCardBinding;
import com.example.model.FarmerActivityItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying Farmer activities across Labour, Agri Waste, Direct Orders, and Contracts.
 */
public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ActivityViewHolder> {

    public interface OnActivityClickListener {
        void onActivityClick(FarmerActivityItem item);
    }

    private final List<FarmerActivityItem> items = new ArrayList<>();
    private final OnActivityClickListener listener;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public ActivitiesAdapter(OnActivityClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<FarmerActivityItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActivityCardBinding binding = ItemActivityCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ActivityViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ActivityViewHolder extends RecyclerView.ViewHolder {
        private final ItemActivityCardBinding binding;

        public ActivityViewHolder(ItemActivityCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final FarmerActivityItem item) {
            binding.tvActivityEmoji.setText(item.getTypeEmoji() != null ? item.getTypeEmoji() : "📋");
            binding.tvActivityTitle.setText(item.getTitle());
            binding.tvActivityCategory.setText(item.getCategory());
            binding.tvActivityStatus.setText(item.getStatus());

            if ("Pending".equalsIgnoreCase(item.getStatus()) || "Waiting for Farmer".equalsIgnoreCase(item.getStatus())) {
                binding.tvActivityStatus.setTextColor(binding.getRoot().getContext().getColor(R.color.farmer_accent));
            } else if ("Completed".equalsIgnoreCase(item.getStatus()) || "Delivered".equalsIgnoreCase(item.getStatus())) {
                binding.tvActivityStatus.setTextColor(binding.getRoot().getContext().getColor(R.color.farmer_success));
            } else {
                binding.tvActivityStatus.setTextColor(binding.getRoot().getContext().getColor(R.color.farmer_primary));
            }

            binding.tvActivityCounterparty.setText(item.getCounterparty() != null ? item.getCounterparty() : "");
            binding.tvActivityDate.setText(item.getDate() != null ? item.getDate() : "");
            binding.tvActivityDetails.setText(item.getDetails() != null ? item.getDetails() : "");

            if (item.getAmount() > 0) {
                binding.tvActivityAmount.setText("₹" + String.format(Locale.getDefault(), "%,.0f", item.getAmount()));
                binding.tvActivityAmount.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.tvActivityAmount.setVisibility(android.view.View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onActivityClick(item);
            });
        }
    }
}
