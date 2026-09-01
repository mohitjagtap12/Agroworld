package com.example.ui.labour;

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

import com.example.R;
import com.example.adapter.LabourRequirementAdapter;
import com.example.databinding.FragmentLabourHubBinding;
import com.example.model.LabourRequirement;
import com.example.viewmodel.LabourViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for Farmer to manage nearby labour requirements and bookings.
 */
public class LabourHubFragment extends Fragment implements LabourRequirementAdapter.OnRequirementActionListener {

    private FragmentLabourHubBinding binding;
    private LabourViewModel viewModel;
    private LabourRequirementAdapter adapter;
    private List<LabourRequirement> allRequirements = new ArrayList<>();
    private int currentFilterIndex = 0; // 0: All, 1: Active, 2: Scheduled, 3: Completed

    public static LabourHubFragment newInstance() {
        return new LabourHubFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLabourHubBinding.inflate(inflater, container, false);
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
        adapter = new LabourRequirementAdapter();
        adapter.setOnRequirementActionListener(this);
        binding.rvLabourRequirements.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvLabourRequirements.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.btnBackLabourHub.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.btnHeaderPostReq.setOnClickListener(v -> openPostRequirement());
        binding.btnEmptyPostReq.setOnClickListener(v -> openPostRequirement());

        binding.chipGroupLabourFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipFilterAll) {
                currentFilterIndex = 0;
            } else if (id == R.id.chipFilterActive) {
                currentFilterIndex = 1;
            } else if (id == R.id.chipFilterScheduled) {
                currentFilterIndex = 2;
            } else if (id == R.id.chipFilterCompleted) {
                currentFilterIndex = 3;
            }
            filterList();
        });
    }

    private void observeViewModel() {
        binding.progressBarLabour.setVisibility(View.VISIBLE);
        viewModel.getRequirementsLiveData().observe(getViewLifecycleOwner(), requirements -> {
            binding.progressBarLabour.setVisibility(View.GONE);
            allRequirements = requirements != null ? new ArrayList<>(requirements) : new ArrayList<>();
            filterList();
        });
    }

    private void filterList() {
        List<LabourRequirement> filtered = new ArrayList<>();
        for (LabourRequirement req : allRequirements) {
            String status = req.getStatus() != null ? req.getStatus() : "";
            if (currentFilterIndex == 0) {
                filtered.add(req);
            } else if (currentFilterIndex == 1) {
                if ("Finding Labour".equalsIgnoreCase(status) || "Request Sent".equalsIgnoreCase(status) || "Accepted".equalsIgnoreCase(status)) {
                    filtered.add(req);
                }
            } else if (currentFilterIndex == 2) {
                if ("Confirmed".equalsIgnoreCase(status) || "Scheduled".equalsIgnoreCase(status) || "Work Started".equalsIgnoreCase(status)) {
                    filtered.add(req);
                }
            } else if (currentFilterIndex == 3) {
                if ("Completed".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status)) {
                    filtered.add(req);
                }
            }
        }

        adapter.setRequirements(filtered);
        if (filtered.isEmpty()) {
            binding.layoutEmptyLabour.setVisibility(View.VISIBLE);
            binding.rvLabourRequirements.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyLabour.setVisibility(View.GONE);
            binding.rvLabourRequirements.setVisibility(View.VISIBLE);
        }
    }

    private void openPostRequirement() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.farmerFragmentContainer, PostLabourRequirementFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onConfirmWorker(LabourRequirement requirement) {
        if (requirement != null && requirement.getWorkerIdsAccepted() != null && !requirement.getWorkerIdsAccepted().isEmpty()) {
            String firstWorker = requirement.getWorkerIdsAccepted().get(0);
            viewModel.confirmWorker(requirement.getId(), firstWorker);
            Toast.makeText(getContext(), "Worker booking confirmed! Job status updated to Scheduled.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No workers accepted yet. Waiting for workers to accept.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewRequirement(LabourRequirement requirement) {
        Toast.makeText(getContext(), "Requirement: " + requirement.getWorkType() + " (" + requirement.getStatus() + ")", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
