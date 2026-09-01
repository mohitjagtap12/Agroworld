package com.example.ui.farmer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.adapter.ActivitiesAdapter;
import com.example.databinding.FragmentFarmerActivitiesBinding;
import com.example.model.FarmerActivityItem;
import com.example.viewmodel.FarmerViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/**
 * Fragment displaying unified Farmer activities across Labour, Waste, Produce, and Deals.
 */
public class FarmerActivitiesFragment extends Fragment implements ActivitiesAdapter.OnActivityClickListener {

    private FragmentFarmerActivitiesBinding binding;
    private FarmerViewModel viewModel;
    private ActivitiesAdapter activitiesAdapter;

    public FarmerActivitiesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFarmerActivitiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FarmerViewModel.class);

        setupRecyclerView();
        setupFilterChips();
        observeViewModel();
    }

    private void setupRecyclerView() {
        activitiesAdapter = new ActivitiesAdapter(this);
        binding.rvActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvActivities.setAdapter(activitiesAdapter);
    }

    private void setupFilterChips() {
        binding.chipGroupActivityFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                viewModel.filterActivities("All");
                return;
            }
            int id = checkedIds.get(0);
            if (id == binding.chipFilterPending.getId()) {
                viewModel.filterActivities("Pending");
            } else if (id == binding.chipFilterActive.getId()) {
                viewModel.filterActivities("Active");
            } else if (id == binding.chipFilterCompleted.getId()) {
                viewModel.filterActivities("Completed");
            } else {
                viewModel.filterActivities("All");
            }
        });
    }

    private void observeViewModel() {
        viewModel.getActivitiesLiveData().observe(getViewLifecycleOwner(), this::updateActivitiesList);
    }

    private void updateActivitiesList(List<FarmerActivityItem> activities) {
        if (activities == null || activities.isEmpty()) {
            binding.layoutEmptyActivities.setVisibility(View.VISIBLE);
            binding.rvActivities.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyActivities.setVisibility(View.GONE);
            binding.rvActivities.setVisibility(View.VISIBLE);
            activitiesAdapter.submitList(activities);
        }
    }

    @Override
    public void onActivityClick(FarmerActivityItem item) {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(item.getTypeEmoji() + " " + item.getTitle())
                .setMessage("Category: " + item.getCategory() + "\nStatus: " + item.getStatus() + "\n" + item.getCounterparty() + "\n" + item.getDetails() + "\nDate: " + item.getDate())
                .setPositiveButton("Close", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
