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

import com.example.adapter.ContractAdapter;
import com.example.adapter.ContractApplicationAdapter;
import com.example.databinding.DialogCreateContractBinding;
import com.example.databinding.FragmentCompanyBinding;
import com.example.model.CompanyProfile;
import com.example.model.ContractApplication;
import com.example.model.ContractFarmingDeal;
import com.example.viewmodel.ContractFarmingViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Company Contract Farming Management Portal: Publish corporate buyback demand,
 * review incoming farmer applications, approve contracts, and update cultivation milestones.
 */
public class CompanyFragment extends Fragment implements
        ContractAdapter.OnContractClickListener,
        ContractApplicationAdapter.OnApplicationClickListener {

    private FragmentCompanyBinding binding;
    private ContractFarmingViewModel contractViewModel;

    private ContractAdapter contractAdapter;
    private ContractApplicationAdapter applicationAdapter;

    private int currentTabPosition = 0; // 0: Published Deals, 1: Farmer Applications, 2: Active, 3: Completed
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public CompanyFragment() {
        currencyFormat.setMaximumFractionDigits(0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCompanyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contractViewModel = new ViewModelProvider(requireActivity()).get(ContractFarmingViewModel.class);

        setupToolbar();
        setupAdapters();
        setupTabs();
        setupPublishAction();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbarCompanyPortal.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        binding.btnRefreshCompany.setOnClickListener(v -> {
            contractViewModel.refreshAllData();
            Toast.makeText(getContext(), "Refreshed company dashboard", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupAdapters() {
        contractAdapter = new ContractAdapter(this);
        applicationAdapter = new ContractApplicationAdapter(this);
        applicationAdapter.setCompanyView(true);

        binding.rvCompanyList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCompanyList.setAdapter(contractAdapter);
    }

    private void setupTabs() {
        binding.tabLayoutCompanySections.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    private void setupPublishAction() {
        binding.btnOpenCreateContract.setOnClickListener(v -> showCreateContractDialog());
    }

    private void observeViewModel() {
        contractViewModel.getCompanyProfileLiveData().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                binding.tvCompanyName.setText(profile.getCompanyName());
                binding.tvCompanyType.setText(profile.getBusinessType());
                binding.tvCompanyContactDetails.setText("Contact: " + profile.getContactPerson() + " • " +
                        profile.getPhone() + " • " + profile.getLocation() + "\n" + profile.getFssaiGstNumber());
            }
        });

        contractViewModel.getCompanyContractsLiveData().observe(getViewLifecycleOwner(), contracts -> {
            if (contracts != null) {
                binding.tvCompanyPublishedCount.setText(String.valueOf(contracts.size()));
                if (currentTabPosition == 0) {
                    binding.rvCompanyList.setAdapter(contractAdapter);
                    contractAdapter.submitList(contracts);
                    checkEmptyState(contracts.isEmpty(), "No published contracts found.",
                            "Tap 'Publish New Crop Contract' above to invite verified farmer supply.");
                }
            }
        });

        contractViewModel.getCompanyApplicationsLiveData().observe(getViewLifecycleOwner(), allApps -> {
            if (allApps != null) {
                List<ContractApplication> pendingApps = new ArrayList<>();
                List<ContractApplication> activeApps = new ArrayList<>();
                List<ContractApplication> completedApps = new ArrayList<>();

                for (ContractApplication app : allApps) {
                    String status = app.getStatus();
                    if ("Completed".equalsIgnoreCase(status)) {
                        completedApps.add(app);
                    } else if ("Active".equalsIgnoreCase(status) || "Accepted".equalsIgnoreCase(status) ||
                            "Confirmed".equalsIgnoreCase(status) || "Harvest Ready".equalsIgnoreCase(status) ||
                            "Delivered".equalsIgnoreCase(status)) {
                        activeApps.add(app);
                    } else {
                        pendingApps.add(app);
                    }
                }

                binding.tvCompanyAppsCount.setText(String.valueOf(pendingApps.size()));
                binding.tvCompanyActiveCount.setText(String.valueOf(activeApps.size()));
                binding.tvCompanyCompletedCount.setText(String.valueOf(completedApps.size()));

                if (currentTabPosition == 1) {
                    binding.rvCompanyList.setAdapter(applicationAdapter);
                    applicationAdapter.submitList(pendingApps);
                    checkEmptyState(pendingApps.isEmpty(), "No pending farmer applications.",
                            "When farmers apply to your published deals, they will appear here for review.");
                } else if (currentTabPosition == 2) {
                    binding.rvCompanyList.setAdapter(applicationAdapter);
                    applicationAdapter.submitList(activeApps);
                    checkEmptyState(activeApps.isEmpty(), "No active contracts at this time.",
                            "Approved farmer applications in active cultivation will show here.");
                } else if (currentTabPosition == 3) {
                    binding.rvCompanyList.setAdapter(applicationAdapter);
                    applicationAdapter.submitList(completedApps);
                    checkEmptyState(completedApps.isEmpty(), "No fulfilled contracts yet.",
                            "Completed crop deliveries will appear here.");
                }
            }
        });

        contractViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            binding.pbCompanyLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        contractViewModel.getStatusMessageLiveData().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateViewForSelectedTab() {
        if (currentTabPosition == 0) {
            binding.rvCompanyList.setAdapter(contractAdapter);
            List<ContractFarmingDeal> list = contractViewModel.getCompanyContractsLiveData().getValue();
            contractAdapter.submitList(list);
            checkEmptyState(list == null || list.isEmpty(),
                    "No published contracts found.",
                    "Tap 'Publish New Crop Contract' above to invite verified farmer supply.");
        } else if (currentTabPosition == 1) {
            binding.rvCompanyList.setAdapter(applicationAdapter);
            List<ContractApplication> allApps = contractViewModel.getCompanyApplicationsLiveData().getValue();
            List<ContractApplication> pending = filterListByTab(allApps, 1);
            applicationAdapter.submitList(pending);
            checkEmptyState(pending.isEmpty(),
                    "No pending farmer applications.",
                    "When farmers apply to your published deals, they will appear here for review.");
        } else if (currentTabPosition == 2) {
            binding.rvCompanyList.setAdapter(applicationAdapter);
            List<ContractApplication> allApps = contractViewModel.getCompanyApplicationsLiveData().getValue();
            List<ContractApplication> active = filterListByTab(allApps, 2);
            applicationAdapter.submitList(active);
            checkEmptyState(active.isEmpty(),
                    "No active contracts at this time.",
                    "Approved farmer applications in active cultivation will show here.");
        } else {
            binding.rvCompanyList.setAdapter(applicationAdapter);
            List<ContractApplication> allApps = contractViewModel.getCompanyApplicationsLiveData().getValue();
            List<ContractApplication> completed = filterListByTab(allApps, 3);
            applicationAdapter.submitList(completed);
            checkEmptyState(completed.isEmpty(),
                    "No fulfilled contracts yet.",
                    "Completed crop deliveries will appear here.");
        }
    }

    private List<ContractApplication> filterListByTab(List<ContractApplication> all, int tab) {
        List<ContractApplication> result = new ArrayList<>();
        if (all == null) return result;
        for (ContractApplication app : all) {
            String s = app.getStatus();
            if (tab == 1 && ("Submitted".equalsIgnoreCase(s) || "Under Review".equalsIgnoreCase(s))) {
                result.add(app);
            } else if (tab == 2 && ("Active".equalsIgnoreCase(s) || "Accepted".equalsIgnoreCase(s) || "Confirmed".equalsIgnoreCase(s) || "Harvest Ready".equalsIgnoreCase(s) || "Delivered".equalsIgnoreCase(s))) {
                result.add(app);
            } else if (tab == 3 && "Completed".equalsIgnoreCase(s)) {
                result.add(app);
            }
        }
        return result;
    }

    private void checkEmptyState(boolean isEmpty, String title, String subtitle) {
        if (isEmpty) {
            binding.layoutCompanyEmpty.setVisibility(View.VISIBLE);
            binding.tvCompanyEmptyTitle.setText(title);
            binding.tvCompanyEmptySubtitle.setText(subtitle);
            binding.rvCompanyList.setVisibility(View.GONE);
        } else {
            binding.layoutCompanyEmpty.setVisibility(View.GONE);
            binding.rvCompanyList.setVisibility(View.VISIBLE);
        }
    }

    private void showCreateContractDialog() {
        if (getContext() == null) return;
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        DialogCreateContractBinding b = DialogCreateContractBinding.inflate(getLayoutInflater());
        dialog.setContentView(b.getRoot());

        CompanyProfile profile = contractViewModel.getCompanyProfileLiveData().getValue();
        String compName = profile != null ? profile.getCompanyName() : "Sahyadri Farmers Producer Co. Ltd.";
        String compPhone = profile != null ? profile.getPhone() : "+91 20 2554 8899";
        String compId = profile != null ? profile.getId() : "comp_01";

        b.btnCancelCreateContract.setOnClickListener(v -> dialog.dismiss());

        b.btnConfirmPublishContract.setOnClickListener(v -> {
            String cropName = b.etCreateCropName.getText() != null ? b.etCreateCropName.getText().toString().trim() : "";
            String variety = b.etCreateVariety.getText() != null ? b.etCreateVariety.getText().toString().trim() : "";
            String emoji = b.etCreateEmoji.getText() != null ? b.etCreateEmoji.getText().toString().trim() : "🌾";
            String qtyStr = b.etCreateQuantity.getText() != null ? b.etCreateQuantity.getText().toString().trim() : "";
            String priceStr = b.etCreatePrice.getText() != null ? b.etCreatePrice.getText().toString().trim() : "";
            String advanceStr = b.etCreateAdvance.getText() != null ? b.etCreateAdvance.getText().toString().trim() : "20";
            String deadline = b.etCreateDeadline.getText() != null ? b.etCreateDeadline.getText().toString().trim() : "";
            String harvestPeriod = b.etCreateHarvestPeriod.getText() != null ? b.etCreateHarvestPeriod.getText().toString().trim() : "";
            String location = b.etCreateLocation.getText() != null ? b.etCreateLocation.getText().toString().trim() : "";
            String qualitySpecs = b.etCreateQualitySpecs.getText() != null ? b.etCreateQualitySpecs.getText().toString().trim() : "";
            String paymentTerms = b.etCreatePaymentTerms.getText() != null ? b.etCreatePaymentTerms.getText().toString().trim() : "";

            if (cropName.isEmpty() || variety.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all mandatory contract fields marked with *", Toast.LENGTH_SHORT).show();
                return;
            }

            double qty = 50.0;
            double price = 20000.0;
            int advance = 20;
            try {
                qty = Double.parseDouble(qtyStr);
                price = Double.parseDouble(priceStr);
                advance = Integer.parseInt(advanceStr);
            } catch (Exception ignored) {}

            ContractFarmingDeal newDeal = new ContractFarmingDeal(
                    "cf_" + System.currentTimeMillis(),
                    compId,
                    compName,
                    compPhone,
                    cropName,
                    emoji.isEmpty() ? "🌾" : emoji,
                    variety,
                    qty,
                    "Tons",
                    price,
                    "Ton",
                    harvestPeriod.isEmpty() ? "Seasonal Harvest 2026" : harvestPeriod,
                    location.isEmpty() ? "Pune Agro-Industrial Cluster" : location,
                    qualitySpecs.isEmpty() ? "Standard FAQ Specs" : qualitySpecs,
                    "Assured procurement upon quality inspection.",
                    advance,
                    deadline,
                    "6 Months",
                    paymentTerms.isEmpty() ? "Advance upon agreement, remaining upon delivery." : paymentTerms,
                    "Direct farm gate or collection center delivery as agreed.",
                    "Open",
                    null
            );

            contractViewModel.createCompanyContract(newDeal);
            dialog.dismiss();
        });

        dialog.show();
    }

    // --- OnContractClickListener Callbacks ---
    @Override
    public void onContractDetailsClick(ContractFarmingDeal deal) {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(deal.getCropEmoji() + " " + deal.getCropName() + " (Published Deal)")
                .setMessage("🏢 Company: " + deal.getCompanyName() +
                        "\n🌾 Variety: " + deal.getVariety() +
                        "\n📦 Target Volume: " + deal.getRequiredQuantityTons() + " Tons" +
                        "\n💰 Price: " + currencyFormat.format(deal.getOfferedPricePerTon()) + " / Ton" +
                        "\n💳 Advance: " + deal.getAdvancePaymentPercent() + "%" +
                        "\n📅 Harvest: " + deal.getHarvestPeriod() +
                        "\n📍 Location: " + deal.getLocation() +
                        "\n⏰ Deadline: " + deal.getApplicationDeadline() +
                        "\n\n🔍 Specs: " + deal.getQualitySpecs() +
                        "\n\n📜 Payment Terms: " + deal.getPaymentTerms())
                .setPositiveButton("OK", null)
                .setNegativeButton("Close Contract", (d, w) -> {
                    contractViewModel.closeContract(deal.getId());
                })
                .show();
    }

    @Override
    public void onApplyContractClick(ContractFarmingDeal deal) {}

    // --- OnApplicationClickListener Callbacks ---
    @Override
    public void onApplicationClick(ContractApplication application) {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(application.getCropEmoji() + " " + application.getCropName() + " Application")
                .setMessage("👨‍🌾 Farmer: " + application.getFarmerName() + " (" + application.getFarmerPhone() + ")" +
                        "\n📍 Village: " + application.getVillage() + ", " + application.getTaluka() + " (" + application.getDistrict() + ")" +
                        "\n\n📊 Status: " + application.getStatus() +
                        "\n🎯 Milestone: " + application.getCurrentMilestone() + " (" + application.getMilestoneProgressPercent() + "%)" +
                        "\n🌾 Land Area: " + application.getLandAreaAcres() + " Acres" +
                        "\n📦 Committed Volume: " + application.getExpectedQuantityTons() + " Tons" +
                        "\n💰 Total Value: " + currencyFormat.format(application.calculateTotalContractValue()) +
                        "\n📅 Harvest Target: " + application.getExpectedHarvestDate() +
                        "\n\n🔍 Farmer Notes: " + application.getQualityGradeNotes() +
                        (application.getAdditionalMessage() != null ? "\n" + application.getAdditionalMessage() : ""))
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onAcceptApplication(ContractApplication application) {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Accept Farmer Contract Application")
                .setMessage("Accept " + application.getFarmerName() + " for " + application.getExpectedQuantityTons() +
                        " Tons of " + application.getCropName() + " at " + currencyFormat.format(application.getAgreedPricePerTon()) + "/Ton?\n\nContract will move to Active status.")
                .setPositiveButton("Accept & Confirm", (d, w) -> {
                    contractViewModel.updateApplicationStatus(application.getId(), "Active", "Contract Confirmed & Active Cultivation Started");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onRejectApplication(ContractApplication application) {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Decline Application")
                .setMessage("Are you sure you want to decline this application from " + application.getFarmerName() + "?")
                .setPositiveButton("Decline", (d, w) -> {
                    contractViewModel.updateApplicationStatus(application.getId(), "Rejected", "Application Declined by Procurement Team");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onUpdateMilestone(ContractApplication application) {
        if (getContext() == null) return;
        final String[] milestones = new String[]{
                "1. Crop Sown & Field Inspected (30%)",
                "2. Active Growth & Weeding Complete (50%)",
                "3. Flowering / Fruit Setting Stage (70%)",
                "4. Crop Ready for Harvest Inspection (85%)",
                "5. Harvest Delivered to Warehouse (95%)",
                "6. Full Contract Settled & Paid (100%)"
        };
        final int[] progressPercents = new int[]{30, 50, 70, 85, 95, 100};

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Update Cultivation Milestone")
                .setItems(milestones, (dialog, which) -> {
                    int percent = progressPercents[which];
                    String milestoneText = milestones[which];
                    contractViewModel.updateMilestoneProgress(application.getId(), percent, milestoneText);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
