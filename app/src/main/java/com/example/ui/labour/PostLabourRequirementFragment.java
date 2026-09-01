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
import com.example.adapter.LabourWorkerAdapter;
import com.example.databinding.FragmentPostLabourRequirementBinding;
import com.example.model.LabourRequirement;
import com.example.model.LabourWorker;
import com.example.viewmodel.LabourViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Multi-step wizard fragment for posting a labour requirement and matching nearby workers.
 */
public class PostLabourRequirementFragment extends Fragment implements LabourWorkerAdapter.OnWorkerSelectionListener {

    private FragmentPostLabourRequirementBinding binding;
    private LabourViewModel viewModel;
    private LabourWorkerAdapter workerAdapter;

    private int currentStep = 1;
    private final int TOTAL_STEPS = 7;

    // Form Data
    private String workType = "Sugarcane Harvesting";
    private String crop = "Sugarcane (Co-86032)";
    private String description = "Need skilled sugarcane harvesting squad with cutting tools for 3.5 acres farm.";
    private int workersRequired = 8;
    private String skillLevel = "Skilled";
    private String experienceRequired = "3+ years";
    private final List<String> requiredSkills = new ArrayList<>(Arrays.asList("Sugarcane Harvesting", "Cutting", "Loading"));
    private String startDate = "05 Sept 2026";
    private String endDate = "08 Sept 2026";
    private String startTime = "07:00 AM";
    private int workingHoursPerDay = 8;
    private String wageType = "Per Day";
    private double wageAmount = 500.0;
    private String paymentTerms = "Daily Cash";
    private String village = "Narayangaon";
    private String taluka = "Junnar";
    private String district = "Pune";
    private int searchRadiusKm = 10;
    private String farmLocation = "Near Narayangaon Bypass Canal, Survey No 142";
    private boolean foodProvided = true;
    private boolean transportProvided = false;
    private String requiredEquipment = "Sharp sickle cutting tools (Koyta)";
    private String specialInstructions = "Morning tea and afternoon lunch provided on the field. Work starts sharp at 7 AM.";

    private List<LabourWorker> selectedWorkers = new ArrayList<>();

    public static PostLabourRequirementFragment newInstance() {
        return new PostLabourRequirementFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPostLabourRequirementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LabourViewModel.class);

        setupRecyclerView();
        setupListeners();
        updateStepUI();
    }

    private void setupRecyclerView() {
        workerAdapter = new LabourWorkerAdapter();
        workerAdapter.setOnWorkerSelectionListener(this);
        binding.rvMatchingWorkers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMatchingWorkers.setAdapter(workerAdapter);
    }

    private void setupListeners() {
        binding.btnBackPostReq.setOnClickListener(v -> {
            if (currentStep > 1) {
                currentStep--;
                updateStepUI();
            } else if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.btnPreviousStep.setOnClickListener(v -> {
            if (currentStep > 1) {
                currentStep--;
                updateStepUI();
            }
        });

        binding.btnNextStep.setOnClickListener(v -> {
            if (validateAndSaveCurrentStep()) {
                if (currentStep < TOTAL_STEPS) {
                    currentStep++;
                    if (currentStep == TOTAL_STEPS) {
                        loadMatchingWorkers();
                    }
                    updateStepUI();
                } else {
                    submitRequirement();
                }
            }
        });

        // Step 1: Work Type Chips
        binding.chipGroupWorkType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipSugarcaneCutting) workType = "Sugarcane Cutting";
            else if (id == R.id.chipHarvesting) workType = "Harvesting";
            else if (id == R.id.chipSowing) workType = "Sowing";
            else if (id == R.id.chipPlanting) workType = "Planting";
            else if (id == R.id.chipWeeding) workType = "Weeding";
            else if (id == R.id.chipIrrigation) workType = "Irrigation";
            else if (id == R.id.chipSpraying) workType = "Pesticide Spraying";
            else if (id == R.id.chipFertilizer) workType = "Fertilizer Application";
            else if (id == R.id.chipFruitPicking) workType = "Fruit Picking";
            else workType = "Other";
        });

        // Step 2: Skill Level Chips
        binding.chipGroupSkillLevel.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipSkillSkilled) skillLevel = "Skilled";
            else if (id == R.id.chipSkillSemiSkilled) skillLevel = "Semi-skilled";
            else if (id == R.id.chipSkillUnskilled) skillLevel = "Unskilled";
            else skillLevel = "Any";
        });

        // Step 4: Wage Type Chips
        binding.chipGroupWageType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipWagePerDay) wageType = "Per Day";
            else if (id == R.id.chipWagePerHour) wageType = "Per Hour";
            else wageType = "Fixed Job";
        });

        viewModel.getMatchedWorkersLiveData().observe(getViewLifecycleOwner(), workers -> {
            if (workers != null && currentStep == TOTAL_STEPS) {
                workerAdapter.setWorkers(workers, workersRequired);
                binding.tvMatchingHeader.setText(String.format(Locale.ENGLISH, "%d Suitable Workers Found Nearby", workers.size()));
            }
        });
    }

    private boolean validateAndSaveCurrentStep() {
        if (currentStep == 1) {
            if (binding.etFormCrop.getText() != null) {
                crop = binding.etFormCrop.getText().toString().trim();
            }
            if (binding.etFormDescription.getText() != null) {
                description = binding.etFormDescription.getText().toString().trim();
            }
            if (crop.isEmpty()) {
                binding.etFormCrop.setError("Please enter crop name");
                return false;
            }
        } else if (currentStep == 2) {
            if (binding.etFormWorkersCount.getText() != null) {
                try {
                    workersRequired = Integer.parseInt(binding.etFormWorkersCount.getText().toString().trim());
                } catch (NumberFormatException ignored) {}
            }
            if (binding.etFormExperience.getText() != null) {
                experienceRequired = binding.etFormExperience.getText().toString().trim();
            }
        } else if (currentStep == 3) {
            if (binding.etFormStartDate.getText() != null) {
                startDate = binding.etFormStartDate.getText().toString().trim();
            }
            if (binding.etFormEndDate.getText() != null) {
                endDate = binding.etFormEndDate.getText().toString().trim();
            }
            if (binding.etFormStartTime.getText() != null) {
                startTime = binding.etFormStartTime.getText().toString().trim();
            }
            if (binding.etFormWorkingHours.getText() != null) {
                try {
                    workingHoursPerDay = Integer.parseInt(binding.etFormWorkingHours.getText().toString().trim());
                } catch (NumberFormatException ignored) {}
            }
        } else if (currentStep == 4) {
            if (binding.etFormWageAmount.getText() != null) {
                try {
                    wageAmount = Double.parseDouble(binding.etFormWageAmount.getText().toString().trim());
                } catch (NumberFormatException ignored) {}
            }
            if (binding.etFormPaymentTerms.getText() != null) {
                paymentTerms = binding.etFormPaymentTerms.getText().toString().trim();
            }
        } else if (currentStep == 5) {
            if (binding.etFormVillage.getText() != null) {
                village = binding.etFormVillage.getText().toString().trim();
            }
            if (binding.etFormTaluka.getText() != null) {
                taluka = binding.etFormTaluka.getText().toString().trim();
            }
            if (binding.etFormDistrict.getText() != null) {
                district = binding.etFormDistrict.getText().toString().trim();
            }
            if (binding.etFormFarmLocation.getText() != null) {
                farmLocation = binding.etFormFarmLocation.getText().toString().trim();
            }
        } else if (currentStep == 6) {
            foodProvided = binding.switchFoodProvided.isChecked();
            transportProvided = binding.switchTransportProvided.isChecked();
            if (binding.etFormEquipment.getText() != null) {
                requiredEquipment = binding.etFormEquipment.getText().toString().trim();
            }
            if (binding.etFormInstructions.getText() != null) {
                specialInstructions = binding.etFormInstructions.getText().toString().trim();
            }
        }
        return true;
    }

    private void updateStepUI() {
        binding.pbFormProgress.setProgress(currentStep);
        binding.btnPreviousStep.setVisibility(currentStep > 1 ? View.VISIBLE : View.GONE);

        binding.layoutStep1.setVisibility(currentStep == 1 ? View.VISIBLE : View.GONE);
        binding.layoutStep2.setVisibility(currentStep == 2 ? View.VISIBLE : View.GONE);
        binding.layoutStep3.setVisibility(currentStep == 3 ? View.VISIBLE : View.GONE);
        binding.layoutStep4.setVisibility(currentStep == 4 ? View.VISIBLE : View.GONE);
        binding.layoutStep5.setVisibility(currentStep == 5 ? View.VISIBLE : View.GONE);
        binding.layoutStep6.setVisibility(currentStep == 6 ? View.VISIBLE : View.GONE);
        binding.layoutStep7.setVisibility(currentStep == 7 ? View.VISIBLE : View.GONE);

        switch (currentStep) {
            case 1:
                binding.tvStepIndicator.setText("Step 1 of 7: Work Details");
                binding.btnNextStep.setText("Next: Labour Details");
                break;
            case 2:
                binding.tvStepIndicator.setText("Step 2 of 7: Labour Requirement");
                binding.btnNextStep.setText("Next: Date & Time");
                break;
            case 3:
                binding.tvStepIndicator.setText("Step 3 of 7: Date & Schedule");
                binding.btnNextStep.setText("Next: Payment Details");
                break;
            case 4:
                binding.tvStepIndicator.setText("Step 4 of 7: Payment & Wage");
                binding.btnNextStep.setText("Next: Location");
                break;
            case 5:
                binding.tvStepIndicator.setText("Step 5 of 7: Location & Radius");
                binding.btnNextStep.setText("Next: Additional Perks");
                break;
            case 6:
                binding.tvStepIndicator.setText("Step 6 of 7: Additional Info");
                binding.btnNextStep.setText("Find Matching Labour");
                break;
            case 7:
                binding.tvStepIndicator.setText("Step 7 of 7: Matching & Select");
                binding.btnNextStep.setText("Send Requests to Selected");
                binding.tvPreviewTitle.setText(String.format(Locale.ENGLISH, "%s (%d Workers)", workType, workersRequired));
                binding.tvPreviewDetails.setText(String.format(Locale.ENGLISH, "📅 %s • %s • ₹%.0f / %s\n📍 %s, %s (Radius: %d km)",
                        startDate, startTime, wageAmount, wageType.toLowerCase(), village, taluka, searchRadiusKm));
                break;
        }
    }

    private void loadMatchingWorkers() {
        viewModel.searchMatchingWorkers(workType, skillLevel, searchRadiusKm);
    }

    private void submitRequirement() {
        LabourRequirement req = new LabourRequirement();
        req.setId("REQ_" + UUID.randomUUID().toString().substring(0, 8));
        req.setFarmerId("FARMER_MH_01");
        req.setFarmerName("Ramesh Patil");
        req.setFarmerPhone("+91 98220 14589");
        req.setWorkType(workType);
        req.setCrop(crop);
        req.setDescription(description);
        req.setWorkersRequired(workersRequired);
        req.setSkillLevel(skillLevel);
        req.setExperienceRequired(experienceRequired);
        req.setRequiredSkills(requiredSkills);
        req.setStartDate(startDate);
        req.setEndDate(endDate);
        req.setStartTime(startTime);
        req.setWorkingHoursPerDay(workingHoursPerDay);
        req.setWageType(wageType);
        req.setWageAmount(wageAmount);
        req.setPaymentTerms(paymentTerms);
        req.setVillage(village);
        req.setTaluka(taluka);
        req.setDistrict(district);
        req.setFarmLocation(farmLocation);
        req.setSearchRadiusKm(searchRadiusKm);
        req.setSpecialInstructions(specialInstructions);
        req.setRequiredEquipment(requiredEquipment);
        req.setFoodProvided(foodProvided);
        req.setTransportProvided(transportProvided);
        req.setStatus("Finding Labour");

        List<LabourWorker> selected = workerAdapter.getSelectedWorkers();
        if (selected.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least 1 matching worker before sending requests.", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.postRequirement(req, selected);
        Toast.makeText(getContext(), "Requirement posted & requests sent to " + selected.size() + " workers!", Toast.LENGTH_LONG).show();

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onSelectionChanged(int currentCount, int maxAllowed, List<LabourWorker> selectedWorkers) {
        this.selectedWorkers = selectedWorkers;
        binding.tvSelectedCount.setText(String.format(Locale.ENGLISH, "Selected: %d / %d", currentCount, maxAllowed));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
