package com.example.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.databinding.ItemLabourWorkerBinding;
import com.example.model.LabourWorker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Adapter for selecting matching workers during Farmer Labour Requirement posting.
 */
public class LabourWorkerAdapter extends RecyclerView.Adapter<LabourWorkerAdapter.WorkerViewHolder> {

    private List<LabourWorker> workers = new ArrayList<>();
    private final Set<String> selectedWorkerIds = new HashSet<>();
    private int maxSelectionAllowed = 8;
    private OnWorkerSelectionListener listener;

    public interface OnWorkerSelectionListener {
        void onSelectionChanged(int currentCount, int maxAllowed, List<LabourWorker> selectedWorkers);
    }

    public void setOnWorkerSelectionListener(OnWorkerSelectionListener listener) {
        this.listener = listener;
    }

    public void setWorkers(List<LabourWorker> newWorkers, int maxRequired) {
        this.workers = newWorkers != null ? new ArrayList<>(newWorkers) : new ArrayList<>();
        this.maxSelectionAllowed = Math.max(1, maxRequired);
        notifyDataSetChanged();
    }

    public List<LabourWorker> getSelectedWorkers() {
        List<LabourWorker> selected = new ArrayList<>();
        for (LabourWorker w : workers) {
            if (selectedWorkerIds.contains(w.getId())) {
                selected.add(w);
            }
        }
        return selected;
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLabourWorkerBinding binding = ItemLabourWorkerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new WorkerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerViewHolder holder, int position) {
        holder.bind(workers.get(position));
    }

    @Override
    public int getItemCount() {
        return workers.size();
    }

    class WorkerViewHolder extends RecyclerView.ViewHolder {
        private final ItemLabourWorkerBinding binding;

        WorkerViewHolder(@NonNull ItemLabourWorkerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(LabourWorker worker) {
            binding.tvWorkerAvatar.setText(worker.getAvatarEmoji() != null ? worker.getAvatarEmoji() : "👨‍🌾");
            binding.tvWorkerName.setText(worker.getName());
            binding.tvSquadName.setText(worker.getSquadName() != null ? worker.getSquadName() : "Individual Farm Worker");
            binding.tvLocationDistance.setText(String.format(Locale.ENGLISH, "📍 %s • %.1f km away", worker.getVillage(), worker.getDistanceKm()));
            binding.chipRating.setText(String.format(Locale.ENGLISH, "★ %.1f", worker.getRating()));
            binding.tvExperience.setText(String.format(Locale.ENGLISH, "⏳ %d+ Yrs Exp", worker.getExperienceYears()));
            binding.tvDailyWage.setText(String.format(Locale.ENGLISH, "₹%.0f / day", worker.getDailyWage()));

            if (worker.getSkills() != null && !worker.getSkills().isEmpty()) {
                binding.tvSkills.setText("🛠️ " + String.join(", ", worker.getSkills()));
            } else {
                binding.tvSkills.setText("🛠️ " + worker.getPrimarySkill());
            }

            binding.tvAvailability.setText("📅 " + worker.getAvailableDates());

            boolean isSelected = selectedWorkerIds.contains(worker.getId());
            updateSelectionState(isSelected);

            binding.btnSelectWorker.setOnClickListener(v -> {
                if (selectedWorkerIds.contains(worker.getId())) {
                    selectedWorkerIds.remove(worker.getId());
                    updateSelectionState(false);
                } else {
                    if (selectedWorkerIds.size() < maxSelectionAllowed) {
                        selectedWorkerIds.add(worker.getId());
                        updateSelectionState(true);
                    }
                }
                if (listener != null) {
                    listener.onSelectionChanged(selectedWorkerIds.size(), maxSelectionAllowed, getSelectedWorkers());
                }
            });
        }

        private void updateSelectionState(boolean isSelected) {
            if (isSelected) {
                binding.btnSelectWorker.setText("✓ SELECTED");
                binding.btnSelectWorker.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_primary));
                binding.btnSelectWorker.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
            } else {
                binding.btnSelectWorker.setText("[SELECT]");
                binding.btnSelectWorker.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_light_bg));
                binding.btnSelectWorker.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_primary));
            }
        }
    }
}
