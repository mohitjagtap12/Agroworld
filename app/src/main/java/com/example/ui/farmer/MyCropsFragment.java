package com.example.ui.farmer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.adapter.CropsAdapter;
import com.example.databinding.DialogAddCropBinding;
import com.example.databinding.FragmentMyCropsBinding;
import com.example.model.FarmerCrop;
import com.example.viewmodel.FarmerViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/**
 * Screen displaying the farmer's registered crops, irrigation methods, and AI health status.
 */
public class MyCropsFragment extends Fragment implements CropsAdapter.OnCropActionListener {

    private FragmentMyCropsBinding binding;
    private FarmerViewModel viewModel;
    private CropsAdapter cropsAdapter;

    public MyCropsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyCropsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FarmerViewModel.class);

        setupRecyclerView();
        observeViewModel();
        setupClickListeners();
    }

    private void setupRecyclerView() {
        cropsAdapter = new CropsAdapter(this);
        binding.rvCrops.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCrops.setAdapter(cropsAdapter);
    }

    private void observeViewModel() {
        viewModel.getCropsLiveData().observe(getViewLifecycleOwner(), this::updateCropsList);
        viewModel.getMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCropsList(List<FarmerCrop> crops) {
        if (crops == null || crops.isEmpty()) {
            binding.layoutEmptyCrops.setVisibility(View.VISIBLE);
            binding.rvCrops.setVisibility(View.GONE);
            binding.tvTotalCropsCount.setText("0 Crops Active");
            binding.tvTotalLandMonitored.setText("Tap + Add Crop to monitor your fields");
        } else {
            binding.layoutEmptyCrops.setVisibility(View.GONE);
            binding.rvCrops.setVisibility(View.VISIBLE);
            cropsAdapter.submitList(crops);

            double totalAcres = 0;
            for (FarmerCrop c : crops) {
                try {
                    totalAcres += Double.parseDouble(c.getLandArea());
                } catch (Exception ignored) {}
            }
            binding.tvTotalCropsCount.setText(crops.size() + " Crops Active");
            binding.tvTotalLandMonitored.setText(String.format("%.1f Acres actively monitored in Junnar cluster", totalAcres));
        }
    }

    private void setupClickListeners() {
        binding.fabAddCrop.setOnClickListener(v -> showAddCropDialog());

        binding.btnQuickAiAllCrops.setOnClickListener(v -> {
            if (getActivity() instanceof FarmerDashboardFragment.OnFarmerNavListener) {
                ((FarmerDashboardFragment.OnFarmerNavListener) getActivity()).onNavigateToAiDisease(null, "Tomato");
            }
        });
    }

    private void showAddCropDialog() {
        if (getContext() == null) return;

        DialogAddCropBinding dialogBinding = DialogAddCropBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext())
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnCancelAddCrop.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnSubmitAddCrop.setOnClickListener(v -> {
            String name = dialogBinding.etCropName.getText() != null ? dialogBinding.etCropName.getText().toString().trim() : "";
            String variety = dialogBinding.etCropVariety.getText() != null ? dialogBinding.etCropVariety.getText().toString().trim() : "";
            String category = dialogBinding.etCropCategory.getText() != null ? dialogBinding.etCropCategory.getText().toString().trim() : "Cash Crop";
            String landArea = dialogBinding.etLandArea.getText() != null ? dialogBinding.etLandArea.getText().toString().trim() : "1.0";
            String sowingDate = dialogBinding.etSowingDate.getText() != null ? dialogBinding.etSowingDate.getText().toString().trim() : "15 June 2026";
            String harvestDate = dialogBinding.etHarvestDate.getText() != null ? dialogBinding.etHarvestDate.getText().toString().trim() : "20 Oct 2026";
            String irrigation = dialogBinding.etIrrigationType.getText() != null ? dialogBinding.etIrrigationType.getText().toString().trim() : "Drip Irrigation";

            if (name.isEmpty()) {
                dialogBinding.etCropName.setError("Crop name is required");
                return;
            }

            String id = "crop_" + System.currentTimeMillis();
            String emoji = "🌱";
            if (name.toLowerCase().contains("onion")) emoji = "🧅";
            else if (name.toLowerCase().contains("tomato")) emoji = "🍅";
            else if (name.toLowerCase().contains("rice")) emoji = "🌾";
            else if (name.toLowerCase().contains("sugar")) emoji = "🎋";
            else if (name.toLowerCase().contains("chilli")) emoji = "🌶️";
            else if (name.toLowerCase().contains("cotton")) emoji = "☁️";

            FarmerCrop newCrop = new FarmerCrop(
                    id, name, variety.isEmpty() ? "Standard Grade" : variety, category,
                    landArea.isEmpty() ? "1.0" : landArea, "Acres",
                    sowingDate, harvestDate, irrigation,
                    10.0, 2000.0, "Healthy field cultivation in Junnar block",
                    "Growing", emoji
            );

            viewModel.addCrop(newCrop);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onCropClick(FarmerCrop crop) {
        Toast.makeText(getContext(), crop.getName() + " - Stage: " + crop.getStatus(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckDiseaseClick(FarmerCrop crop) {
        if (getActivity() instanceof FarmerDashboardFragment.OnFarmerNavListener) {
            ((FarmerDashboardFragment.OnFarmerNavListener) getActivity()).onNavigateToAiDisease(crop.getId(), crop.getName());
        }
    }

    @Override
    public void onDiseaseHistoryClick(FarmerCrop crop) {
        if (getActivity() instanceof FarmerDashboardFragment.OnFarmerNavListener) {
            ((FarmerDashboardFragment.OnFarmerNavListener) getActivity()).onNavigateToAiDisease(crop.getId(), crop.getName());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
