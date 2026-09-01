package com.example.ui.labour;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.R;
import com.example.adapter.LabourJobRequestAdapter;
import com.example.databinding.FragmentLabourPortalBinding;
import com.example.model.LabourApplication;
import com.example.model.LabourWorker;
import com.example.viewmodel.LabourViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Labour Portal Fragment for workers and squad leaders (Mukadam).
 */
public class LabourPortalFragment extends Fragment implements LabourJobRequestAdapter.OnJobRequestActionListener {

    private FragmentLabourPortalBinding binding;
    private LabourViewModel viewModel;
    private LabourJobRequestAdapter adapter;

    private List<LabourApplication> allApplications = new ArrayList<>();
    private int currentTab = 0; // 0: Requests, 1: Upcoming, 2: Completed, 3: Profile
    private LabourWorker currentWorker;

    public static LabourPortalFragment newInstance() {
        return new LabourPortalFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLabourPortalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LabourViewModel.class);

        setupRecyclerView();
        setupListeners();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new LabourJobRequestAdapter();
        adapter.setOnJobRequestActionListener(this);
        binding.rvLabourPortalJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvLabourPortalJobs.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.btnBackLabourPortal.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.chipLabourAvailabilityToggle.setOnClickListener(v -> toggleAvailability());

        binding.chipGroupLabourPortal.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipTabRequests) {
                currentTab = 0;
            } else if (id == R.id.chipTabUpcoming) {
                currentTab = 1;
            } else if (id == R.id.chipTabCompleted) {
                currentTab = 2;
            } else if (id == R.id.chipTabProfile) {
                currentTab = 3;
            }
            updateTabContent();
        });

        binding.btnSetAvailability.setOnClickListener(v -> showAvailabilityDialog());
        binding.btnEditLabourProfile.setOnClickListener(v -> showEditProfileDialog());
    }

    private void observeViewModel() {
        viewModel.getCurrentWorkerProfileLiveData().observe(getViewLifecycleOwner(), worker -> {
            if (worker != null) {
                currentWorker = worker;
                binding.tvLabourHeaderSquad.setText(String.format(Locale.ENGLISH, "%s • %s",
                        worker.getSquadName(), worker.getVillage()));
                binding.tvProfileName.setText(worker.getName());
                binding.tvProfileSquad.setText(worker.getSquadName());
                binding.tvProfilePhoneLocation.setText(String.format(Locale.ENGLISH, "%s • %s, %s",
                        worker.getPhone(), worker.getVillage(), worker.getTaluka()));
                binding.tvProfileStats.setText(String.format(Locale.ENGLISH,
                        "⏳ Experience: %d+ Years\n👥 Squad Size: 8 Workers\n💰 Daily Wage: ₹%.0f / worker / day\n⭐ Rating: %.1f (%d completed jobs)\n📍 Service Radius: %d km\n📅 Availability: %s",
                        worker.getExperienceYears(), worker.getDailyWage(), worker.getRating(), worker.getCompletedJobs(), worker.getWorkingRadiusKm(), worker.getAvailableDates()));

                updateAvailabilityBadge(worker.isAvailable());
            }
        });

        viewModel.getJobApplicationsLiveData().observe(getViewLifecycleOwner(), apps -> {
            allApplications = apps != null ? new ArrayList<>(apps) : new ArrayList<>();
            updateCounts();
            updateTabContent();
        });
    }

    private void updateCounts() {
        int pending = 0;
        int active = 0;
        int completed = 0;

        for (LabourApplication app : allApplications) {
            String status = app.getStatus() != null ? app.getStatus() : "";
            if ("Pending".equalsIgnoreCase(status) || "Applied".equalsIgnoreCase(status)) {
                pending++;
            } else if ("Accepted".equalsIgnoreCase(status) || "Confirmed".equalsIgnoreCase(status) || "Scheduled".equalsIgnoreCase(status) || "In Progress".equalsIgnoreCase(status)) {
                active++;
            } else if ("Completed".equalsIgnoreCase(status)) {
                completed++;
            }
        }

        binding.tvCountRequests.setText(String.valueOf(pending));
        binding.tvCountActiveJobs.setText(String.valueOf(active));
        binding.tvCountCompletedJobs.setText(String.valueOf(completed + 64)); // adding baseline
    }

    private void updateTabContent() {
        if (currentTab == 3) {
            binding.rvLabourPortalJobs.setVisibility(View.GONE);
            binding.layoutLabourProfile.setVisibility(View.VISIBLE);
            return;
        }

        binding.layoutLabourProfile.setVisibility(View.GONE);
        binding.rvLabourPortalJobs.setVisibility(View.VISIBLE);

        List<LabourApplication> filtered = new ArrayList<>();
        for (LabourApplication app : allApplications) {
            String status = app.getStatus() != null ? app.getStatus() : "";
            if (currentTab == 0) {
                if ("Pending".equalsIgnoreCase(status) || "Applied".equalsIgnoreCase(status)) {
                    filtered.add(app);
                }
            } else if (currentTab == 1) {
                if ("Accepted".equalsIgnoreCase(status) || "Confirmed".equalsIgnoreCase(status) || "Scheduled".equalsIgnoreCase(status) || "In Progress".equalsIgnoreCase(status)) {
                    filtered.add(app);
                }
            } else if (currentTab == 2) {
                if ("Completed".equalsIgnoreCase(status)) {
                    filtered.add(app);
                }
            }
        }

        adapter.setApplications(filtered);
    }

    private void updateAvailabilityBadge(boolean isAvailable) {
        if (isAvailable) {
            binding.chipLabourAvailabilityToggle.setText("● Available");
            binding.chipLabourAvailabilityToggle.setTextColor(ContextCompat.getColor(requireContext(), R.color.farmer_success));
            binding.chipLabourAvailabilityToggle.setChipBackgroundColorResource(R.color.farmer_light_bg);
        } else {
            binding.chipLabourAvailabilityToggle.setText("● Unavailable");
            binding.chipLabourAvailabilityToggle.setTextColor(ContextCompat.getColor(requireContext(), R.color.farmer_error));
            binding.chipLabourAvailabilityToggle.setChipBackgroundColorResource(R.color.farmer_stroke);
        }
    }

    private void toggleAvailability() {
        if (currentWorker != null) {
            boolean newState = !currentWorker.isAvailable();
            viewModel.updateWorkerAvailability(currentWorker.getId(), newState, null);
            Toast.makeText(getContext(), newState ? "Marked as Available for work!" : "Marked as Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAvailabilityDialog() {
        if (getContext() == null || currentWorker == null) return;
        EditText input = new EditText(getContext());
        input.setHint("Available Dates (e.g. 5 Sept - 15 Sept)");
        input.setText(currentWorker.getAvailableDates());

        new AlertDialog.Builder(getContext())
                .setTitle("Update Available Dates")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String dates = input.getText().toString().trim();
                    viewModel.updateWorkerAvailability(currentWorker.getId(), true, dates);
                    Toast.makeText(getContext(), "Availability updated!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditProfileDialog() {
        Toast.makeText(getContext(), "Profile details are synced with AgroWorld Labour Network.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccept(LabourApplication application) {
        viewModel.respondToJobRequest(application.getId(), true);
        Toast.makeText(getContext(), "Job Request Accepted! Farmer will confirm schedule.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReject(LabourApplication application) {
        viewModel.respondToJobRequest(application.getId(), false);
        Toast.makeText(getContext(), "Job Request Declined.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
