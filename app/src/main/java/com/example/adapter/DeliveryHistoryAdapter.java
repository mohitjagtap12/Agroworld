package com.example.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.model.DeliveryJob;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying completed Delivery History records and trip payout details.
 */
public class DeliveryHistoryAdapter extends RecyclerView.Adapter<DeliveryHistoryAdapter.HistoryViewHolder> {

    private final Context context;
    private final List<DeliveryJob> historyList = new ArrayList<>();

    public DeliveryHistoryAdapter(Context context) {
        this.context = context;
    }

    public void setHistoryList(List<DeliveryJob> list) {
        historyList.clear();
        if (list != null) {
            historyList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delivery_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        DeliveryJob job = historyList.get(position);
        holder.bind(job);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvHistTypeBadge;
        private final TextView tvHistFee;
        private final TextView tvHistEmoji;
        private final TextView tvHistSummary;
        private final TextView tvHistIds;
        private final TextView tvHistRoute;
        private final TextView tvHistCompletedInfo;
        private final TextView tvHistMethod;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistTypeBadge = itemView.findViewById(R.id.tvHistTypeBadge);
            tvHistFee = itemView.findViewById(R.id.tvHistFee);
            tvHistEmoji = itemView.findViewById(R.id.tvHistEmoji);
            tvHistSummary = itemView.findViewById(R.id.tvHistSummary);
            tvHistIds = itemView.findViewById(R.id.tvHistIds);
            tvHistRoute = itemView.findViewById(R.id.tvHistRoute);
            tvHistCompletedInfo = itemView.findViewById(R.id.tvHistCompletedInfo);
            tvHistMethod = itemView.findViewById(R.id.tvHistMethod);
        }

        public void bind(DeliveryJob job) {
            // Type badge
            String type = job.getOrderType();
            if (DeliveryJob.TYPE_SELLER_PRODUCT.equalsIgnoreCase(type)) {
                tvHistTypeBadge.setText("🏪 Seller Product");
                tvHistTypeBadge.setBackgroundResource(R.drawable.bg_chip_status_green);
                tvHistTypeBadge.setTextColor(Color.parseColor("#2E7D32"));
            } else if (DeliveryJob.TYPE_FARM_PRODUCE.equalsIgnoreCase(type)) {
                tvHistTypeBadge.setText("🌾 Fresh Produce");
                tvHistTypeBadge.setBackgroundResource(R.drawable.bg_chip_status_blue);
                tvHistTypeBadge.setTextColor(Color.parseColor("#0061A4"));
            } else if (DeliveryJob.TYPE_AGRI_WASTE.equalsIgnoreCase(type)) {
                tvHistTypeBadge.setText("♻️ Agri Waste");
                tvHistTypeBadge.setBackgroundResource(R.drawable.bg_chip_status_purple);
                tvHistTypeBadge.setTextColor(Color.parseColor("#8E24AA"));
            } else if (DeliveryJob.TYPE_BROKER_DEAL.equalsIgnoreCase(type)) {
                tvHistTypeBadge.setText("📈 Broker Deal");
                tvHistTypeBadge.setBackgroundResource(R.drawable.bg_chip_status_gold);
                tvHistTypeBadge.setTextColor(Color.parseColor("#E65100"));
            } else {
                tvHistTypeBadge.setText("📦 Direct Freight");
                tvHistTypeBadge.setBackgroundResource(R.drawable.bg_chip_status_gray);
                tvHistTypeBadge.setTextColor(Color.parseColor("#616161"));
            }

            tvHistFee.setText("+ ₹" + String.format("%.2f", job.getDeliveryFee()));
            tvHistEmoji.setText(job.getItemEmoji() != null ? job.getItemEmoji() : "📦");
            tvHistSummary.setText(job.getItemsSummary() != null ? job.getItemsSummary() : "Completed Delivery");
            tvHistIds.setText(job.getId() + " • Ref: " + (job.getOrderId() != null ? job.getOrderId() : "N/A"));

            String pickup = job.getSourceLocation() != null ? job.getSourceLocation() : (job.getSourceName() != null ? job.getSourceName() : "Source");
            String drop = job.getDestinationLocation() != null ? job.getDestinationLocation() : (job.getDestinationName() != null ? job.getDestinationName() : "Destination");
            tvHistRoute.setText("Pickup: " + pickup + "\nDrop: " + drop);

            String compTime = job.getCompletedAt() != null ? job.getCompletedAt() : "Completed";
            String recipient = job.getRecipientName() != null ? job.getRecipientName() : (job.getDestinationName() != null ? job.getDestinationName() : "Authorized Person");
            tvHistCompletedInfo.setText("Delivered: " + compTime + " • To: " + recipient);

            tvHistMethod.setText(job.getConfirmationMethod() != null ? job.getConfirmationMethod() : "OTP Verified");
        }
    }
}
