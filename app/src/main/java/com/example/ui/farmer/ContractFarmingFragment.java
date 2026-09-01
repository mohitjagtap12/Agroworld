package com.example.ui.farmer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.adapter.ContractAdapter;
import com.example.adapter.ContractApplicationAdapter;
import com.example.databinding.DialogApplyContractBinding;
import com.example.databinding.DialogContractDetailsBinding;
import com.example.databinding.FragmentContractFarmingBinding;
import com.example.model.ContractApplication;
import com.example.model.ContractFarmingDeal;
import com.example.model.FarmerCrop;
import com.example.repository.FarmerRepository;
import com.example.viewmodel.ContractFarmingViewModel;
import com.example.viewmodel.FarmerViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Farmer Contract Farming Fragment: Browse corporate buyback deals,
 * review verified specifications, submit farm applications, and track active contracts.
 */
public class ContractFarmingFragment extends Fragment implements
        ContractAdapter.OnContractClickListener,
        ContractApplicationAdapter.OnApplicationClickListener {

    private FragmentContractFarmingBinding binding;
    private ContractFarmingViewModel contractViewModel;
    private FarmerViewModel farmerViewModel;

    private ContractAdapter contractAdapter;
    private ContractApplicationAdapter applicationAdapter;

    private int currentTabPosition = 0; // 0: Available, 1: Applications, 2: Active, 3: Completed
    private String currentCategoryFilter = "All";
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public ContractFarmingFragment() {
        currencyFormat.setMaximumFractionDigits(0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContractFarmingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contractViewModel = new ViewModelProvider(requireActivity()).get(ContractFarmingViewModel.class);
        farmerViewModel = new ViewModelProvider(requireActivity()).get(FarmerViewModel.class);

        setupToolbar();
        setupAdapters();
        setupTabs();
        setupMetricCards();
        setupSearchAndFilters();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbarContractFarming.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        binding.btnRefreshContracts.setOnClickListener(v -> {
            contractViewModel.refreshAllData();
            Toast.makeText(getContext(), "Refreshed corporate contracts", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupAdapters() {
        contractAdapter = new ContractAdapter(this);
        applicationAdapter = new ContractApplicationAdapter(this);
        applicationAdapter.setCompanyView(false);

        binding.rvContractList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvContractList.setAdapter(contractAdapter);
    }

    private void setupTabs() {
        binding.tabLayoutContractSections.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                updateViewForSelectedTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                updateViewForSelectedTab();
            }
        });
    }

    private void setupMetricCards() {
        binding.cardMetricAvailable.setOnClickListener(v -> selectTab(0));
        binding.cardMetricApplications.setOnClickListener(v -> selectTab(1));
        binding.cardMetricActive.setOnClickListener(v -> selectTab(2));
        binding.cardMetricCompleted.setOnClickListener(v -> selectTab(3));
    }

    private void selectTab(int position) {
        TabLayout.Tab tab = binding.tabLayoutContractSections.getTabAt(position);
        if (tab != null) {
            tab.select();
        }
    }

    private void setupSearchAndFilters() {
        binding.etSearchContracts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contractViewModel.filterContracts(s.toString(), currentCategoryFilter);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupContractFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentCategoryFilter = "All";
            } else {
                int id = checkedIds.get(0);
                if (id == binding.chipFilterMatchesCrop.getId()) {
                    currentCategoryFilter = "Matches My Crops";
                } else if (id == binding.chipFilterOnion.getId()) {
                    currentCategoryFilter = "Onions";
                } else if (id == binding.chipFilterWheat.getId()) {
                    currentCategoryFilter = "Wheat";
                } else if (id == binding.chipFilterVegetables.getId()) {
                    currentCategoryFilter = "Vegetables";
                } else if (id == binding.chipFilterPune.getId()) {
                    currentCategoryFilter = "Pune / Local";
                } else if (id == binding.chipFilterHighValue.getId()) {
                    currentCategoryFilter = "High Value";
                } else {
                    currentCategoryFilter = "All";
                }
            }
            String query = binding.etSearchContracts.getText() != null ? binding.etSearchContracts.getText().toString() : "";
            contractViewModel.filterContracts(query, currentCategoryFilter);
        });
    }

    private void observeViewModel() {
        farmerViewModel.getCropsLiveData().observe(getViewLifecycleOwner(), crops -> {
            if (crops != null) {
                contractAdapter.setFarmerCrops(crops);
            }
        });

        contractViewModel.getAvailableContractsLiveData().observe(getViewLifecycleOwner(), contracts -> {
            if (contracts != null) {
                binding.tvCountAvailable.setText(String.valueOf(contracts.size()));
                if (currentTabPosition == 0) {
                    binding.rvContractList.setAdapter(contractAdapter);
                    contractAdapter.submitList(contracts);
                    checkEmptyState(contracts.isEmpty(), "No contract opportunities available right now.",
                            "Try adjusting your filter or check back as companies publish new seasonal demand.");
                }
            }
        });

        contractViewModel.getFarmerApplicationsLiveData().observe(getViewLifecycleOwner(), applications -> {
            if (applications != null) {
                binding.tvCountApplications.setText(String.valueOf(applications.size()));
                // Update applied contract ids
                Set<String> appliedIds = new HashSet<>();
                for (ContractApplication app : applications) {
                    appliedIds.add(app.getContractId());
                }
                List<ContractApplication> activeList = contractViewModel.getActiveContractsLiveData().getValue();
                if (activeList != null) {
                    for (ContractApplication app : activeList) {
                        appliedIds.add(app.getContractId());
                    }
                }
                contractAdapter.setAppliedContractIds(appliedIds);

                if (currentTabPosition == 1) {
                    binding.rvContractList.setAdapter(applicationAdapter);
                    applicationAdapter.submitList(applications);
                    checkEmptyState(applications.isEmpty(), "You have no pending contract applications.",
                            "Browse available corporate deals and apply to secure guaranteed prices.");
                }
            }
        });

        contractViewModel.getActiveContractsLiveData().observe(getViewLifecycleOwner(), activeList -> {
            if (activeList != null) {
                binding.tvCountActive.setText(String.valueOf(activeList.size()));
                if (currentTabPosition == 2) {
                    binding.rvContractList.setAdapter(applicationAdapter);
                    applicationAdapter.submitList(activeList);
                    checkEmptyState(activeList.isEmpty(), "You have no active cultivation contracts.",
                            "Once your application is approved by the company, active contract milestones will appear here.");
                }
            }
        });

        contractViewModel.getCompletedContractsLiveData().observe(getViewLifecycleOwner(), completedList -> {
            if (completedList != null) {
                binding.tvCountCompleted.setText(String.valueOf(completedList.size()));
                if (currentTabPosition == 3) {
                    binding.rvContractList.setAdapter(applicationAdapter);
                    applicationAdapter.submitList(completedList);
                    checkEmptyState(completedList.isEmpty(), "No completed contracts yet.",
                            "Completed and settled corporate buybacks will be archived here.");
                }
            }
        });

        contractViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            binding.pbContractLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        contractViewModel.getStatusMessageLiveData().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateViewForSelectedTab() {
        if (currentTabPosition == 0) {
            binding.layoutSearchAndFilter.setVisibility(View.VISIBLE);
            binding.rvContractList.setAdapter(contractAdapter);
            List<ContractFarmingDeal> list = contractViewModel.getAvailableContractsLiveData().getValue();
            contractAdapter.submitList(list);
            checkEmptyState(list == null || list.isEmpty(),
                    "No contract opportunities available right now.",
                    "Try adjusting your filter or check back as companies publish new seasonal demand.");
        } else if (currentTabPosition == 1) {
            binding.layoutSearchAndFilter.setVisibility(View.GONE);
            binding.rvContractList.setAdapter(applicationAdapter);
            List<ContractApplication> list = contractViewModel.getFarmerApplicationsLiveData().getValue();
            applicationAdapter.submitList(list);
            checkEmptyState(list == null || list.isEmpty(),
                    "You have no pending contract applications.",
                    "Browse available corporate deals and apply to secure guaranteed prices.");
        } else if (currentTabPosition == 2) {
            binding.layoutSearchAndFilter.setVisibility(View.GONE);
            binding.rvContractList.setAdapter(applicationAdapter);
            List<ContractApplication> list = contractViewModel.getActiveContractsLiveData().getValue();
            applicationAdapter.submitList(list);
            checkEmptyState(list == null || list.isEmpty(),
                    "You have no active cultivation contracts.",
                    "Once your application is approved by the company, active contract milestones will appear here.");
        } else {
            binding.layoutSearchAndFilter.setVisibility(View.GONE);
            binding.rvContractList.setAdapter(applicationAdapter);
            List<ContractApplication> list = contractViewModel.getCompletedContractsLiveData().getValue();
            applicationAdapter.submitList(list);
            checkEmptyState(list == null || list.isEmpty(),
                    "No completed contracts yet.",
                    "Completed and settled corporate buybacks will be archived here.");
        }
    }

    private void checkEmptyState(boolean isEmpty, String title, String subtitle) {
        if (isEmpty) {
            binding.layoutEmptyContracts.setVisibility(View.VISIBLE);
            binding.tvEmptyTitle.setText(title);
            binding.tvEmptySubtitle.setText(subtitle);
            binding.rvContractList.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyContracts.setVisibility(View.GONE);
            binding.rvContractList.setVisibility(View.VISIBLE);
        }
    }

    // --- OnContractClickListener Callbacks ---
    @Override
    public void onContractDetailsClick(ContractFarmingDeal deal) {
        showContractDetailsBottomSheet(deal);
    }

    @Override
    public void onApplyContractClick(ContractFarmingDeal deal) {
        showApplyContractDialog(deal);
    }

    private void showContractDetailsBottomSheet(ContractFarmingDeal deal) {
        if (getContext() == null) return;
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        DialogContractDetailsBinding b = DialogContractDetailsBinding.inflate(getLayoutInflater());
        dialog.setContentView(b.getRoot());

        b.tvDetailCropEmoji.setText(deal.getCropEmoji() != null ? deal.getCropEmoji() : "🌾");
        b.tvDetailCropName.setText(deal.getCropName());
        b.tvDetailVariety.setText("Variety: " + (deal.getVariety() != null ? deal.getVariety() : "Standard"));
        b.tvDetailCompanyName.setText(deal.getCompanyName());
        b.tvDetailCompanyPhone.setText("📞 Procurement Desk: " + deal.getCompanyPhone());

        b.tvDetailQuantity.setText(String.format(Locale.getDefault(), "%.0f %s", deal.getRequiredQuantityTons(), deal.getUnit()));
        b.tvDetailPrice.setText(currencyFormat.format(deal.getOfferedPricePerTon()) + " / " + deal.getPriceUnit());
        b.tvDetailAdvance.setText(deal.getAdvancePaymentPercent() + "% Advance Token upon Agreement");
        b.tvDetailHarvestPeriod.setText(deal.getHarvestPeriod());
        b.tvDetailLocation.setText(deal.getLocation());
        b.tvDetailQualitySpecs.setText(deal.getQualitySpecs());
        b.tvDetailPaymentTerms.setText(deal.getPaymentTerms() + "\n\nAdditional Conditions: " + deal.getAdditionalConditions());

        b.btnCloseDetails.setOnClickListener(v -> dialog.dismiss());

        b.btnApplyNowFromDetail.setOnClickListener(v -> {
            dialog.dismiss();
            showApplyContractDialog(deal);
        });

        dialog.show();
    }

    private void showApplyContractDialog(ContractFarmingDeal deal) {
        if (getContext() == null) return;
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        DialogApplyContractBinding b = DialogApplyContractBinding.inflate(getLayoutInflater());
        dialog.setContentView(b.getRoot());

        FarmerRepository farmerRepo = FarmerRepository.getInstance();

        b.tvApplySubtitle.setText("Contract with " + deal.getCompanyName());
        b.tvApplyContractCrop.setText((deal.getCropEmoji() != null ? deal.getCropEmoji() : "🌾") + " " + deal.getCropName() + " (" + deal.getVariety() + ")");
        b.tvApplyOfferedPrice.setText(currencyFormat.format(deal.getOfferedPricePerTon()) + " / " + deal.getPriceUnit());

        b.tvApplyFarmerInfo.setText(farmerRepo.getFarmerName() + " • " + farmerRepo.getFarmerPhone() + " • " +
                farmerRepo.getVillage() + ", " + farmerRepo.getTaluka() + " (" + farmerRepo.getDistrict() + ")");

        // Pre-fill from farmer's matching crop if found
        List<FarmerCrop> crops = farmerRepo.getAllCrops();
        for (FarmerCrop c : crops) {
            if (c.getName().toLowerCase().contains(deal.getCropName().toLowerCase()) ||
                    deal.getCropName().toLowerCase().contains(c.getName().toLowerCase())) {
                b.etLandArea.setText(c.getLandArea());
                double expectedYield = c.getQuantity() > 0 ? (c.getQuantity() / 10.0) : 25.0; // convert to tons
                b.etExpectedYield.setText(String.format(Locale.getDefault(), "%.1f", expectedYield));
                b.etHarvestDate.setText(c.getHarvestDate());
                break;
            }
        }

        // Live calculator for estimated gross value
        TextWatcher calcWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String yieldStr = b.etExpectedYield.getText() != null ? b.etExpectedYield.getText().toString() : "0";
                    double tons = Double.parseDouble(yieldStr.trim());
                    double totalValue = tons * deal.getOfferedPricePerTon();
                    b.tvEstimatedTotalValue.setText(currencyFormat.format(totalValue));
                } catch (Exception ignored) {
                    b.tvEstimatedTotalValue.setText("₹0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        b.etExpectedYield.addTextChangedListener(calcWatcher);
        calcWatcher.onTextChanged("", 0, 0, 0);

        b.btnCancelApply.setOnClickListener(v -> dialog.dismiss());

        b.btnConfirmSubmitApply.setOnClickListener(v -> {
            String landAreaStr = b.etLandArea.getText() != null ? b.etLandArea.getText().toString().trim() : "";
            String yieldStr = b.etExpectedYield.getText() != null ? b.etExpectedYield.getText().toString().trim() : "";
            String harvestDate = b.etHarvestDate.getText() != null ? b.etHarvestDate.getText().toString().trim() : "";
            String qualityNotes = b.etQualityNotes.getText() != null ? b.etQualityNotes.getText().toString().trim() : "";

            if (landAreaStr.isEmpty() || yieldStr.isEmpty() || harvestDate.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in land area, expected volume and harvest date.", Toast.LENGTH_SHORT).show();
                return;
            }

            double landArea = 3.0;
            double expectedYield = 30.0;
            try {
                landArea = Double.parseDouble(landAreaStr);
                expectedYield = Double.parseDouble(yieldStr);
            } catch (Exception ignored) {}

            ContractApplication app = new ContractApplication(
                    "app_" + System.currentTimeMillis(),
                    deal.getId(),
                    deal.getCompanyId(),
                    deal.getCompanyName(),
                    farmerRepo.getFarmerId(),
                    farmerRepo.getFarmerName(),
                    farmerRepo.getFarmerPhone(),
                    farmerRepo.getVillage(),
                    farmerRepo.getTaluka(),
                    farmerRepo.getDistrict(),
                    deal.getCropName(),
                    deal.getCropEmoji(),
                    deal.getVariety(),
                    landArea,
                    expectedYield,
                    harvestDate,
                    qualityNotes,
                    "Committed to required grade standards and agronomic inspections.",
                    deal.getOfferedPricePerTon(),
                    "Under Review",
                    null,
                    null,
                    25,
                    "Application Submitted - Agronomist Review Pending"
            );

            contractViewModel.applyForContract(app);
            dialog.dismiss();
            selectTab(1); // switch to My Applications tab
        });

        dialog.show();
    }

    // --- OnApplicationClickListener Callbacks ---
    @Override
    public void onApplicationClick(ContractApplication application) {
        showApplicationTimelineDialog(application);
    }

    @Override
    public void onAcceptApplication(ContractApplication application) {}

    @Override
    public void onRejectApplication(ContractApplication application) {}

    @Override
    public void onUpdateMilestone(ContractApplication application) {}

    private void showApplicationTimelineDialog(ContractApplication app) {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(app.getCropEmoji() + " " + app.getCropName() + " Contract")
                .setMessage("🏢 Corporate Buyer: " + app.getCompanyName() +
                        "\n\n📊 Status: " + app.getStatus() +
                        "\n🎯 Current Stage: " + app.getCurrentMilestone() +
                        "\n📈 Progress: " + app.getMilestoneProgressPercent() + "%" +
                        "\n\n🌾 Land Area: " + app.getLandAreaAcres() + " Acres" +
                        "\n📦 Target Volume: " + app.getExpectedQuantityTons() + " Tons" +
                        "\n💰 Agreed Rate: " + currencyFormat.format(app.getAgreedPricePerTon()) + " / Ton" +
                        "\n💵 Total Contract Value: " + currencyFormat.format(app.calculateTotalContractValue()) +
                        "\n📅 Expected Harvest: " + app.getExpectedHarvestDate() +
                        "\n📝 Submitted: " + app.getSubmittedDate() +
                        (app.getReviewedDate() != null ? "\n✓ Confirmed: " + app.getReviewedDate() : "") +
                        "\n\n🔍 Quality Notes: " + app.getQualityGradeNotes())
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
