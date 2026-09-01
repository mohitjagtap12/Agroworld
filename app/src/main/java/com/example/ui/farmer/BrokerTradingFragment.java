package com.example.ui.farmer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.BrokerDealAdapter;
import com.example.adapter.BrokerRequirementAdapter;
import com.example.adapter.FarmerOfferAdapter;
import com.example.model.BrokerDeal;
import com.example.model.BrokerRequirement;
import com.example.model.FarmerBrokerOffer;
import com.example.model.FarmerCrop;
import com.example.R;
import com.example.repository.FarmerRepository;
import com.example.viewmodel.BrokerViewModel;
import com.example.viewmodel.FarmerViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Farmer-side Wholesale Broker Trading & Bulk Commodity Selling Fragment.
 * Direct entry point from Farmer Dashboard (🌾 Farmer Dashboard -> 📈 Broker Trading).
 */
public class BrokerTradingFragment extends Fragment implements
        BrokerRequirementAdapter.OnRequirementActionListener,
        FarmerOfferAdapter.OnOfferActionListener,
        BrokerDealAdapter.OnDealActionListener {

    private BrokerViewModel brokerViewModel;
    private FarmerViewModel farmerViewModel;

    private MaterialToolbar toolbar;
    private MaterialButton btnSwitchToBrokerPortal;
    private TextView tvStatTotalDemands;
    private TextView tvStatMyOffers;
    private TextView tvStatActiveDeals;
    private EditText etSearchBroker;
    private ImageButton btnClearSearch;
    private ChipGroup chipGroupFilters;

    private MaterialButton tabBulkRequirements;
    private MaterialButton tabMyOffers;
    private MaterialButton tabActiveDeals;
    private MaterialButton tabCompletedDeals;

    private ProgressBar progressBar;
    private LinearLayout llEmptyState;
    private TextView tvEmptyIcon;
    private TextView tvEmptyTitle;
    private TextView tvEmptyMessage;
    private RecyclerView rvBrokerTrading;

    private BrokerRequirementAdapter requirementAdapter;
    private FarmerOfferAdapter offerAdapter;
    private BrokerDealAdapter dealAdapter;

    private List<FarmerCrop> cachedFarmerCrops = new ArrayList<>();
    private String currentTab = "DEMANDS"; // "DEMANDS", "OFFERS", "ACTIVE_DEALS", "COMPLETED"

    public static BrokerTradingFragment newInstance() {
        return new BrokerTradingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_broker_trading, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        brokerViewModel = new ViewModelProvider(requireActivity()).get(BrokerViewModel.class);
        farmerViewModel = new ViewModelProvider(requireActivity()).get(FarmerViewModel.class);

        initViews(view);
        setupAdapters();
        setupListeners();
        setupObservers();

        brokerViewModel.refreshData();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbarBrokerTrading);
        btnSwitchToBrokerPortal = view.findViewById(R.id.btnSwitchToBrokerPortal);
        tvStatTotalDemands = view.findViewById(R.id.tvStatTotalDemands);
        tvStatMyOffers = view.findViewById(R.id.tvStatMyOffers);
        tvStatActiveDeals = view.findViewById(R.id.tvStatActiveDeals);
        etSearchBroker = view.findViewById(R.id.etSearchBroker);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);
        chipGroupFilters = view.findViewById(R.id.chipGroupFilters);

        tabBulkRequirements = view.findViewById(R.id.tabBulkRequirements);
        tabMyOffers = view.findViewById(R.id.tabMyOffers);
        tabActiveDeals = view.findViewById(R.id.tabActiveDeals);
        tabCompletedDeals = view.findViewById(R.id.tabCompletedDeals);

        progressBar = view.findViewById(R.id.progressBarBrokerTrading);
        llEmptyState = view.findViewById(R.id.llEmptyStateTrading);
        tvEmptyIcon = view.findViewById(R.id.tvEmptyIcon);
        tvEmptyTitle = view.findViewById(R.id.tvEmptyTitle);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        rvBrokerTrading = view.findViewById(R.id.rvBrokerTrading);

        rvBrokerTrading.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupAdapters() {
        requirementAdapter = new BrokerRequirementAdapter(this);
        offerAdapter = new FarmerOfferAdapter(false, this);
        dealAdapter = new BrokerDealAdapter(this);

        rvBrokerTrading.setAdapter(requirementAdapter);
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnSwitchToBrokerPortal.setOnClickListener(v -> {
            if (getActivity() instanceof FarmerContainerActivity) {
                ((FarmerContainerActivity) getActivity()).loadFragment(BrokerFragment.newInstance(), true);
            }
        });

        // Search text watcher
        etSearchBroker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                brokerViewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearchBroker.setText("");
            brokerViewModel.setSearchQuery("");
        });

        // Chip Filters
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                brokerViewModel.setCategoryFilter("All");
                return;
            }
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipFilterAll) {
                brokerViewModel.setCategoryFilter("All");
            } else if (checkedId == R.id.chipFilterMatches) {
                brokerViewModel.setCategoryFilter("Matches My Crops");
            } else if (checkedId == R.id.chipFilterWheat) {
                brokerViewModel.setCategoryFilter("Wheat");
            } else if (checkedId == R.id.chipFilterOnions) {
                brokerViewModel.setCategoryFilter("Onions");
            } else if (checkedId == R.id.chipFilterRice) {
                brokerViewModel.setCategoryFilter("Rice");
            } else if (checkedId == R.id.chipFilterHighPrice) {
                brokerViewModel.setCategoryFilter("High Price");
            }
        });

        // Tabs
        tabBulkRequirements.setOnClickListener(v -> selectTab("DEMANDS"));
        tabMyOffers.setOnClickListener(v -> selectTab("OFFERS"));
        tabActiveDeals.setOnClickListener(v -> selectTab("ACTIVE_DEALS"));
        tabCompletedDeals.setOnClickListener(v -> selectTab("COMPLETED"));
    }

    private void selectTab(String tab) {
        this.currentTab = tab;
        updateTabStyles();
        refreshCurrentTabDisplay();
    }

    private void updateTabStyles() {
        int primaryColor = requireContext().getColor(R.color.farmer_primary);
        int surfaceColor = requireContext().getColor(R.color.farmer_surface);
        int textColor = requireContext().getColor(R.color.farmer_text_primary);
        int whiteColor = requireContext().getColor(R.color.white);

        // Reset all to outlined style
        tabBulkRequirements.setBackgroundTintList(android.content.res.ColorStateList.valueOf(surfaceColor));
        tabBulkRequirements.setTextColor(textColor);
        tabMyOffers.setBackgroundTintList(android.content.res.ColorStateList.valueOf(surfaceColor));
        tabMyOffers.setTextColor(textColor);
        tabActiveDeals.setBackgroundTintList(android.content.res.ColorStateList.valueOf(surfaceColor));
        tabActiveDeals.setTextColor(textColor);
        tabCompletedDeals.setBackgroundTintList(android.content.res.ColorStateList.valueOf(surfaceColor));
        tabCompletedDeals.setTextColor(textColor);

        // Activate selected tab
        if ("DEMANDS".equals(currentTab)) {
            tabBulkRequirements.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
            tabBulkRequirements.setTextColor(whiteColor);
        } else if ("OFFERS".equals(currentTab)) {
            tabMyOffers.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
            tabMyOffers.setTextColor(whiteColor);
        } else if ("ACTIVE_DEALS".equals(currentTab)) {
            tabActiveDeals.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
            tabActiveDeals.setTextColor(whiteColor);
        } else if ("COMPLETED".equals(currentTab)) {
            tabCompletedDeals.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
            tabCompletedDeals.setTextColor(whiteColor);
        }
    }

    private void setupObservers() {
        farmerViewModel.getCropsLiveData().observe(getViewLifecycleOwner(), crops -> {
            if (crops != null) {
                cachedFarmerCrops = crops;
                List<BrokerRequirement> reqs = brokerViewModel.getRequirementsLiveData().getValue();
                if (reqs != null) {
                    requirementAdapter.updateData(reqs, cachedFarmerCrops);
                }
            }
        });

        brokerViewModel.getRequirementsLiveData().observe(getViewLifecycleOwner(), reqs -> {
            if (reqs != null) {
                tvStatTotalDemands.setText(reqs.size() + " Open");
                if ("DEMANDS".equals(currentTab)) {
                    requirementAdapter.updateData(reqs, cachedFarmerCrops);
                    checkEmptyState(reqs.isEmpty(), "📈", "No Bulk Demands Match", "Try adjusting your search keywords or switching category filters.");
                }
            }
        });

        brokerViewModel.getFarmerOffersLiveData().observe(getViewLifecycleOwner(), offers -> {
            if (offers != null) {
                int activeCount = 0;
                for (FarmerBrokerOffer o : offers) {
                    if (!"Completed".equalsIgnoreCase(o.getStatus()) && !"Rejected".equalsIgnoreCase(o.getStatus()) && !"Cancelled".equalsIgnoreCase(o.getStatus())) {
                        activeCount++;
                    }
                }
                tvStatMyOffers.setText(activeCount + " Active");
                if ("OFFERS".equals(currentTab)) {
                    offerAdapter.updateData(offers);
                    checkEmptyState(offers.isEmpty(), "📩", "No Offers Submitted Yet", "Browse open bulk demands and submit crop supply offers to wholesale brokers.");
                }
            }
        });

        brokerViewModel.getFarmerDealsLiveData().observe(getViewLifecycleOwner(), deals -> {
            if (deals != null) {
                int activeCount = 0;
                for (BrokerDeal d : deals) {
                    if (!"Completed".equalsIgnoreCase(d.getStatus()) && !"Cancelled".equalsIgnoreCase(d.getStatus())) {
                        activeCount++;
                    }
                }
                tvStatActiveDeals.setText(activeCount + " In Progress");
                if ("ACTIVE_DEALS".equals(currentTab) || "COMPLETED".equals(currentTab)) {
                    refreshCurrentTabDisplay();
                }
            }
        });

        brokerViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), loading -> {
            if (loading != null) {
                progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });

        brokerViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty() && getContext() != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshCurrentTabDisplay() {
        if ("DEMANDS".equals(currentTab)) {
            rvBrokerTrading.setAdapter(requirementAdapter);
            List<BrokerRequirement> reqs = brokerViewModel.getRequirementsLiveData().getValue();
            requirementAdapter.updateData(reqs, cachedFarmerCrops);
            checkEmptyState(reqs == null || reqs.isEmpty(), "📈", "No Bulk Demands Match", "Try adjusting your search keywords or category filters.");
        } else if ("OFFERS".equals(currentTab)) {
            rvBrokerTrading.setAdapter(offerAdapter);
            List<FarmerBrokerOffer> offers = brokerViewModel.getFarmerOffersLiveData().getValue();
            offerAdapter.updateData(offers);
            checkEmptyState(offers == null || offers.isEmpty(), "📩", "No Offers Submitted Yet", "Browse open bulk demands and submit crop supply offers to wholesale brokers.");
        } else if ("ACTIVE_DEALS".equals(currentTab)) {
            rvBrokerTrading.setAdapter(dealAdapter);
            List<BrokerDeal> allDeals = brokerViewModel.getFarmerDealsLiveData().getValue();
            List<BrokerDeal> active = new ArrayList<>();
            if (allDeals != null) {
                for (BrokerDeal d : allDeals) {
                    if (!"Completed".equalsIgnoreCase(d.getStatus()) && !"Cancelled".equalsIgnoreCase(d.getStatus())) {
                        active.add(d);
                    }
                }
            }
            dealAdapter.updateData(active);
            checkEmptyState(active.isEmpty(), "🤝", "No Active Deals", "Accept broker counter-offers to confirm bulk crop trading agreements.");
        } else if ("COMPLETED".equals(currentTab)) {
            rvBrokerTrading.setAdapter(dealAdapter);
            List<BrokerDeal> allDeals = brokerViewModel.getFarmerDealsLiveData().getValue();
            List<BrokerDeal> completed = new ArrayList<>();
            if (allDeals != null) {
                for (BrokerDeal d : allDeals) {
                    if ("Completed".equalsIgnoreCase(d.getStatus())) {
                        completed.add(d);
                    }
                }
            }
            dealAdapter.updateData(completed);
            checkEmptyState(completed.isEmpty(), "✅", "No Completed Deals", "Deals marked as finished will appear in this history archive.");
        }
    }

    private void checkEmptyState(boolean isEmpty, String icon, String title, String msg) {
        if (isEmpty) {
            llEmptyState.setVisibility(View.VISIBLE);
            tvEmptyIcon.setText(icon);
            tvEmptyTitle.setText(title);
            tvEmptyMessage.setText(msg);
            rvBrokerTrading.setVisibility(View.GONE);
        } else {
            llEmptyState.setVisibility(View.GONE);
            rvBrokerTrading.setVisibility(View.VISIBLE);
        }
    }

    // --- OnRequirementActionListener ---

    @Override
    public void onMakeOfferClicked(BrokerRequirement requirement) {
        showMakeOfferDialog(requirement);
    }

    private void showMakeOfferDialog(BrokerRequirement req) {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_broker_offer, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvSummary = dialogView.findViewById(R.id.tvRequirementSummary);
        TextView tvLivePayout = dialogView.findViewById(R.id.tvLiveCalculatedPayout);
        EditText etQty = dialogView.findViewById(R.id.etAvailableQty);
        EditText etPrice = dialogView.findViewById(R.id.etExpectedPrice);
        EditText etDate = dialogView.findViewById(R.id.etAvailableDate);
        EditText etQuality = dialogView.findViewById(R.id.etQualityDetails);
        EditText etMessage = dialogView.findViewById(R.id.etMessage);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelOfferDialog);
        MaterialButton btnSubmit = dialogView.findViewById(R.id.btnSubmitOfferDialog);

        tvTitle.setText((req.getCropEmoji() != null ? req.getCropEmoji() : "🌾") + " Submit Bulk Offer for " + req.getCrop());
        tvSummary.setText("Broker " + (req.getBrokerFirmName() != null ? req.getBrokerFirmName() : req.getBrokerName()) +
                " is buying @ ₹" + (int) req.getOfferedPrice() + "/" + req.getPriceUnit());

        // Pre-fill expected price with broker's offered price or slightly higher
        etPrice.setText(String.valueOf((int) req.getOfferedPrice()));

        // Helper calculation listener
        TextWatcher calcWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePayoutPreview();
            }

            @Override
            public void afterTextChanged(Editable s) {}

            private void updatePayoutPreview() {
                try {
                    double q = Double.parseDouble(etQty.getText().toString().trim());
                    double p = Double.parseDouble(etPrice.getText().toString().trim());
                    // 1 Ton = 10 Quintals
                    double total = BrokerDeal.calculateTotalValue(q, "Tons", p, req.getPriceUnit());
                    NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                    nf.setMaximumFractionDigits(0);
                    tvLivePayout.setText(nf.format(total) + " (" + q + " Tons = " + (int)(q * 10) + " Quintals × ₹" + (int) p + ")");
                } catch (Exception ignored) {
                    tvLivePayout.setText("Enter valid quantity and price");
                }
            }
        };

        etQty.addTextChangedListener(calcWatcher);
        etPrice.addTextChangedListener(calcWatcher);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String qtyStr = etQty.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String dateStr = etDate.getText().toString().trim();
            String qualityStr = etQuality.getText().toString().trim();
            String msgStr = etMessage.getText().toString().trim();

            if (qtyStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter quantity and expected price.", Toast.LENGTH_SHORT).show();
                return;
            }

            double qty = Double.parseDouble(qtyStr);
            double price = Double.parseDouble(priceStr);

            String farmerId = FarmerRepository.getInstance().getFarmerId();
            String farmerName = FarmerRepository.getInstance().getFarmerName();
            String farmerPhone = FarmerRepository.getInstance().getFarmerPhone();
            String farmerLoc = FarmerRepository.getInstance().getVillage() + ", " + FarmerRepository.getInstance().getDistrict();

            FarmerBrokerOffer offer = new FarmerBrokerOffer(
                    "bo_" + System.currentTimeMillis(),
                    req.getId(),
                    farmerId,
                    farmerName,
                    farmerPhone,
                    farmerLoc,
                    "crop_" + req.getCrop().toLowerCase().replace(" ", "_"),
                    req.getCrop(),
                    req.getCropEmoji(),
                    qty,
                    "Tons",
                    price,
                    req.getPriceUnit(),
                    0.0,
                    0.0,
                    dateStr.isEmpty() ? "15 Nov 2026" : dateStr,
                    qualityStr,
                    msgStr,
                    "Pending",
                    "FARMER",
                    "Offer submitted by farmer at ₹" + (int) price + "/" + req.getPriceUnit(),
                    null,
                    null
            );

            brokerViewModel.submitFarmerOffer(offer, () -> {
                dialog.dismiss();
                selectTab("OFFERS");
            });
        });

        dialog.show();
    }

    // --- OnOfferActionListener ---

    @Override
    public void onAcceptClicked(FarmerBrokerOffer offer) {
        new AlertDialog.Builder(requireContext())
                .setTitle("🤝 Confirm Deal")
                .setMessage("Accept agreed price of ₹" + (int)(offer.getFinalAgreedPrice() > 0 ? offer.getFinalAgreedPrice() : offer.getCounterPrice()) +
                        "/" + offer.getPriceUnit() + " for " + offer.getAvailableQuantity() + " " + offer.getUnit() + " " + offer.getCropName() + "?\n\nTotal Payout: ₹" +
                        (int) offer.calculateTotalValue(offer.getFinalAgreedPrice() > 0 ? offer.getFinalAgreedPrice() : offer.getCounterPrice()))
                .setPositiveButton("Confirm Deal", (d, w) -> {
                    brokerViewModel.farmerAcceptCounterOffer(offer.getId(), () -> selectTab("ACTIVE_DEALS"));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onCounterClicked(FarmerBrokerOffer offer) {
        showFarmerCounterDialog(offer);
    }

    private void showFarmerCounterDialog(FarmerBrokerOffer offer) {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_broker_negotiate, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvNegotiateDialogTitle);
        TextView tvSubtitle = dialogView.findViewById(R.id.tvNegotiateSubtitle);
        TextView tvHistory = dialogView.findViewById(R.id.tvNegotiationHistoryText);
        EditText etCounterPrice = dialogView.findViewById(R.id.etCounterPrice);
        EditText etNote = dialogView.findViewById(R.id.etNegotiateNote);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelNegotiateDialog);
        MaterialButton btnSubmit = dialogView.findViewById(R.id.btnSubmitCounterDialog);

        tvTitle.setText("📈 Place Counter-Offer");
        tvSubtitle.setText(offer.getCropName() + " • " + offer.getAvailableQuantity() + " " + offer.getUnit());
        tvHistory.setText("Farmer Original: ₹" + (int) offer.getExpectedPrice() + "/" + offer.getPriceUnit() +
                (offer.getCounterPrice() > 0 ? "\nBroker Counter: ₹" + (int) offer.getCounterPrice() + "/" + offer.getPriceUnit() : "") +
                (offer.getNegotiationNote() != null ? "\nLatest: " + offer.getNegotiationNote() : ""));

        etCounterPrice.setText(String.valueOf((int) (offer.getCounterPrice() > 0 ? offer.getCounterPrice() : offer.getExpectedPrice())));

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String cpStr = etCounterPrice.getText().toString().trim();
            String noteStr = etNote.getText().toString().trim();
            if (cpStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a counter price.", Toast.LENGTH_SHORT).show();
                return;
            }
            double cp = Double.parseDouble(cpStr);
            brokerViewModel.farmerCounterOffer(offer.getId(), cp, noteStr, dialog::dismiss);
        });

        dialog.show();
    }

    @Override
    public void onRejectOrWithdrawClicked(FarmerBrokerOffer offer) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Withdraw Offer")
                .setMessage("Are you sure you want to withdraw this supply offer?")
                .setPositiveButton("Withdraw", (d, w) -> brokerViewModel.farmerCancelOffer(offer.getId(), null))
                .setNegativeButton("Cancel", null)
                .show();
    }

    // --- OnDealActionListener ---

    @Override
    public void onDealActionClicked(BrokerDeal deal, String nextStatus) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Update Deal Status")
                .setMessage("Advance deal status to: \"" + nextStatus + "\"?")
                .setPositiveButton("Update", (d, w) -> brokerViewModel.updateDealStatus(deal.getId(), nextStatus, null))
                .setNegativeButton("Cancel", null)
                .show();
    }
}
