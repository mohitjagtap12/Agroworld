package com.example.ui.farmer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.example.R;
import com.example.viewmodel.BrokerViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Wholesale Broker & Bulk Crop Buyer Portal.
 * Enables brokers to publish buying requirements, review farmer offers, negotiate rates, and manage deliveries.
 */
public class BrokerFragment extends Fragment implements
        BrokerRequirementAdapter.OnRequirementActionListener,
        FarmerOfferAdapter.OnOfferActionListener,
        BrokerDealAdapter.OnDealActionListener {

    private BrokerViewModel brokerViewModel;

    private MaterialToolbar toolbar;
    private MaterialButton btnSwitchToFarmerTrading;
    private MaterialButton btnCreateRequirement;
    private TextView tvBrokerStatReqs;
    private TextView tvBrokerStatOffers;
    private TextView tvBrokerStatDeals;

    private MaterialButton tabBrokerRequirements;
    private MaterialButton tabBrokerOffers;
    private MaterialButton tabBrokerActiveDeals;
    private MaterialButton tabBrokerHistory;

    private ProgressBar progressBar;
    private LinearLayout llEmptyState;
    private TextView tvEmptyIcon;
    private TextView tvEmptyTitle;
    private TextView tvEmptyMessage;
    private RecyclerView rvBrokerDashboard;

    private BrokerRequirementAdapter requirementAdapter;
    private FarmerOfferAdapter offerAdapter;
    private BrokerDealAdapter dealAdapter;

    private String currentTab = "REQUIREMENTS"; // "REQUIREMENTS", "OFFERS", "DEALS", "HISTORY"

    public static BrokerFragment newInstance() {
        return new BrokerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_broker_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        brokerViewModel = new ViewModelProvider(requireActivity()).get(BrokerViewModel.class);

        initViews(view);
        setupAdapters();
        setupListeners();
        setupObservers();

        brokerViewModel.refreshData();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbarBrokerDashboard);
        btnSwitchToFarmerTrading = view.findViewById(R.id.btnSwitchToFarmerTrading);
        btnCreateRequirement = view.findViewById(R.id.btnCreateRequirement);
        tvBrokerStatReqs = view.findViewById(R.id.tvBrokerStatReqs);
        tvBrokerStatOffers = view.findViewById(R.id.tvBrokerStatOffers);
        tvBrokerStatDeals = view.findViewById(R.id.tvBrokerStatDeals);

        tabBrokerRequirements = view.findViewById(R.id.tabBrokerRequirements);
        tabBrokerOffers = view.findViewById(R.id.tabBrokerOffers);
        tabBrokerActiveDeals = view.findViewById(R.id.tabBrokerActiveDeals);
        tabBrokerHistory = view.findViewById(R.id.tabBrokerHistory);

        progressBar = view.findViewById(R.id.progressBarBrokerDashboard);
        llEmptyState = view.findViewById(R.id.llEmptyStateBroker);
        tvEmptyIcon = view.findViewById(R.id.tvEmptyIconBroker);
        tvEmptyTitle = view.findViewById(R.id.tvEmptyTitleBroker);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessageBroker);
        rvBrokerDashboard = view.findViewById(R.id.rvBrokerDashboard);

        rvBrokerDashboard.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupAdapters() {
        requirementAdapter = new BrokerRequirementAdapter(this);
        offerAdapter = new FarmerOfferAdapter(true, this);
        dealAdapter = new BrokerDealAdapter(this);

        rvBrokerDashboard.setAdapter(requirementAdapter);
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnSwitchToFarmerTrading.setOnClickListener(v -> {
            if (getActivity() instanceof FarmerContainerActivity) {
                ((FarmerContainerActivity) getActivity()).loadFragment(BrokerTradingFragment.newInstance(), false);
            }
        });

        btnCreateRequirement.setOnClickListener(v -> showCreateRequirementDialog());

        tabBrokerRequirements.setOnClickListener(v -> selectTab("REQUIREMENTS"));
        tabBrokerOffers.setOnClickListener(v -> selectTab("OFFERS"));
        tabBrokerActiveDeals.setOnClickListener(v -> selectTab("DEALS"));
        tabBrokerHistory.setOnClickListener(v -> selectTab("HISTORY"));
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

        tabBrokerRequirements.setBackgroundTintList(android.content.res.ColorStateList.valueOf(surfaceColor));
        tabBrokerRequirements.setTextColor(textColor);
        tabBrokerOffers.setBackgroundTintList(android.content.res.ColorStateList.valueOf(surfaceColor));
        tabBrokerOffers.setTextColor(textColor);
        tabBrokerActiveDeals.setBackgroundTintList(android.content.res.ColorStateList.valueOf(surfaceColor));
        tabBrokerActiveDeals.setTextColor(textColor);
        tabBrokerHistory.setBackgroundTintList(android.content.res.ColorStateList.valueOf(surfaceColor));
        tabBrokerHistory.setTextColor(textColor);

        if ("REQUIREMENTS".equals(currentTab)) {
            tabBrokerRequirements.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
            tabBrokerRequirements.setTextColor(whiteColor);
        } else if ("OFFERS".equals(currentTab)) {
            tabBrokerOffers.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
            tabBrokerOffers.setTextColor(whiteColor);
        } else if ("DEALS".equals(currentTab)) {
            tabBrokerActiveDeals.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
            tabBrokerActiveDeals.setTextColor(whiteColor);
        } else if ("HISTORY".equals(currentTab)) {
            tabBrokerHistory.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
            tabBrokerHistory.setTextColor(whiteColor);
        }
    }

    private void setupObservers() {
        brokerViewModel.getBrokerMyRequirementsLiveData().observe(getViewLifecycleOwner(), reqs -> {
            if (reqs != null) {
                tvBrokerStatReqs.setText(reqs.size() + " Active");
                if ("REQUIREMENTS".equals(currentTab)) {
                    requirementAdapter.updateData(reqs, new ArrayList<>());
                    checkEmptyState(reqs.isEmpty(), "📢", "No Demands Published", "Publish wholesale crop buying demands to invite supply offers from farmers.");
                }
            }
        });

        brokerViewModel.getBrokerIncomingOffersLiveData().observe(getViewLifecycleOwner(), offers -> {
            if (offers != null) {
                int incomingCount = 0;
                for (FarmerBrokerOffer o : offers) {
                    if (!"Completed".equalsIgnoreCase(o.getStatus()) && !"Rejected".equalsIgnoreCase(o.getStatus())) {
                        incomingCount++;
                    }
                }
                tvBrokerStatOffers.setText(incomingCount + " Incoming");
                if ("OFFERS".equals(currentTab)) {
                    offerAdapter.updateData(offers);
                    checkEmptyState(offers.isEmpty(), "📩", "No Incoming Farmer Offers", "When farmers submit bulk offers to your requirements, they will appear here.");
                }
            }
        });

        brokerViewModel.getBrokerDealsLiveData().observe(getViewLifecycleOwner(), deals -> {
            if (deals != null) {
                tvBrokerStatDeals.setText(deals.size() + " Deals");
                if ("DEALS".equals(currentTab) || "HISTORY".equals(currentTab)) {
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
        if ("REQUIREMENTS".equals(currentTab)) {
            rvBrokerDashboard.setAdapter(requirementAdapter);
            List<BrokerRequirement> reqs = brokerViewModel.getBrokerMyRequirementsLiveData().getValue();
            requirementAdapter.updateData(reqs, new ArrayList<>());
            checkEmptyState(reqs == null || reqs.isEmpty(), "📢", "No Demands Published", "Publish wholesale crop buying demands to invite supply offers from farmers.");
        } else if ("OFFERS".equals(currentTab)) {
            rvBrokerDashboard.setAdapter(offerAdapter);
            List<FarmerBrokerOffer> offers = brokerViewModel.getBrokerIncomingOffersLiveData().getValue();
            offerAdapter.updateData(offers);
            checkEmptyState(offers == null || offers.isEmpty(), "📩", "No Incoming Farmer Offers", "When farmers submit bulk offers to your requirements, they will appear here.");
        } else if ("DEALS".equals(currentTab)) {
            rvBrokerDashboard.setAdapter(dealAdapter);
            List<BrokerDeal> allDeals = brokerViewModel.getBrokerDealsLiveData().getValue();
            List<BrokerDeal> active = new ArrayList<>();
            if (allDeals != null) {
                for (BrokerDeal d : allDeals) {
                    if (!"Completed".equalsIgnoreCase(d.getStatus()) && !"Cancelled".equalsIgnoreCase(d.getStatus())) {
                        active.add(d);
                    }
                }
            }
            dealAdapter.updateData(active);
            checkEmptyState(active.isEmpty(), "🤝", "No Active Deals", "Accepted offers create confirmed procurement deals.");
        } else if ("HISTORY".equals(currentTab)) {
            rvBrokerDashboard.setAdapter(dealAdapter);
            List<BrokerDeal> allDeals = brokerViewModel.getBrokerDealsLiveData().getValue();
            List<BrokerDeal> completed = new ArrayList<>();
            if (allDeals != null) {
                for (BrokerDeal d : allDeals) {
                    if ("Completed".equalsIgnoreCase(d.getStatus())) {
                        completed.add(d);
                    }
                }
            }
            dealAdapter.updateData(completed);
            checkEmptyState(completed.isEmpty(), "✅", "No Completed Procurements", "Completed deliveries and settled trade deals will appear here.");
        }
    }

    private void checkEmptyState(boolean isEmpty, String icon, String title, String msg) {
        if (isEmpty) {
            llEmptyState.setVisibility(View.VISIBLE);
            tvEmptyIcon.setText(icon);
            tvEmptyTitle.setText(title);
            tvEmptyMessage.setText(msg);
            rvBrokerDashboard.setVisibility(View.GONE);
        } else {
            llEmptyState.setVisibility(View.GONE);
            rvBrokerDashboard.setVisibility(View.VISIBLE);
        }
    }

    private void showCreateRequirementDialog() {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_broker_requirement, null);

        EditText etCrop = dialogView.findViewById(R.id.etReqCropName);
        EditText etQty = dialogView.findViewById(R.id.etReqQuantity);
        EditText etPrice = dialogView.findViewById(R.id.etReqPrice);
        EditText etQuality = dialogView.findViewById(R.id.etReqQuality);
        EditText etDate = dialogView.findViewById(R.id.etReqDate);
        EditText etLocation = dialogView.findViewById(R.id.etReqLocation);
        EditText etTerms = dialogView.findViewById(R.id.etReqPaymentTerms);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelReqDialog);
        MaterialButton btnPublish = dialogView.findViewById(R.id.btnPublishReqDialog);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnPublish.setOnClickListener(v -> {
            String crop = etCrop.getText().toString().trim();
            String qtyStr = etQty.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String quality = etQuality.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String terms = etTerms.getText().toString().trim();

            if (crop.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill crop, volume, and offered price.", Toast.LENGTH_SHORT).show();
                return;
            }

            double qty = Double.parseDouble(qtyStr);
            double price = Double.parseDouble(priceStr);

            String emoji = "🌾";
            if (crop.toLowerCase().contains("onion")) emoji = "🧅";
            else if (crop.toLowerCase().contains("rice") || crop.toLowerCase().contains("paddy")) emoji = "🌾";
            else if (crop.toLowerCase().contains("soy")) emoji = "🌱";
            else if (crop.toLowerCase().contains("cotton")) emoji = "☁️";
            else if (crop.toLowerCase().contains("sugar")) emoji = "🎋";
            else if (crop.toLowerCase().contains("tomato")) emoji = "🍅";
            else if (crop.toLowerCase().contains("potato")) emoji = "🥔";

            BrokerRequirement req = new BrokerRequirement(
                    "req_" + System.currentTimeMillis(),
                    "broker_01",
                    "Dinesh Deshmukh",
                    "Deshmukh Wholesale Trading Corp",
                    "+91 98220 11223",
                    crop,
                    emoji,
                    qty,
                    "Tons",
                    price,
                    "quintal",
                    quality.isEmpty() ? "FAQ Grade A standard moisture" : quality,
                    date.isEmpty() ? "30 Nov 2026" : date,
                    location.isEmpty() ? "Pune APMC Hub & Farm Gate" : location,
                    terms.isEmpty() ? "Immediate RTGS on weighbridge slip" : terms,
                    "Active wholesale procurement for milling operations",
                    price * 0.98,
                    "Open",
                    "Today",
                    "Today"
            );

            brokerViewModel.createBrokerRequirement(req, () -> {
                dialog.dismiss();
                selectTab("REQUIREMENTS");
            });
        });

        dialog.show();
    }

    // --- OnRequirementActionListener ---

    @Override
    public void onMakeOfferClicked(BrokerRequirement requirement) {
        Toast.makeText(getContext(), "You are viewing as Broker. Farmers make offers to this requirement.", Toast.LENGTH_SHORT).show();
    }

    // --- OnOfferActionListener ---

    @Override
    public void onAcceptClicked(FarmerBrokerOffer offer) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Accept Offer & Confirm Deal")
                .setMessage("Accept supply offer from " + offer.getFarmerName() + " for " + offer.getAvailableQuantity() + " " + offer.getUnit() + " " + offer.getCropName() + " @ ₹" + (int)(offer.getCounterPrice() > 0 ? offer.getCounterPrice() : offer.getExpectedPrice()) + "/" + offer.getPriceUnit() + "?\n\nTotal Deal Value: ₹" + (int) offer.calculateTotalValue(offer.getCounterPrice() > 0 ? offer.getCounterPrice() : offer.getExpectedPrice()))
                .setPositiveButton("Accept & Confirm", (d, w) -> {
                    brokerViewModel.brokerAcceptOffer(offer.getId(), offer.getAvailableDate(), offer.getFarmerLocation(), () -> selectTab("DEALS"));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onCounterClicked(FarmerBrokerOffer offer) {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_broker_negotiate, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvNegotiateDialogTitle);
        TextView tvSubtitle = dialogView.findViewById(R.id.tvNegotiateSubtitle);
        TextView tvHistory = dialogView.findViewById(R.id.tvNegotiationHistoryText);
        EditText etCounterPrice = dialogView.findViewById(R.id.etCounterPrice);
        EditText etNote = dialogView.findViewById(R.id.etNegotiateNote);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelNegotiateDialog);
        MaterialButton btnSubmit = dialogView.findViewById(R.id.btnSubmitCounterDialog);

        tvTitle.setText("📈 Counter Farmer's Offer");
        tvSubtitle.setText("Farmer: " + offer.getFarmerName() + " • " + offer.getCropName() + " (" + offer.getAvailableQuantity() + " " + offer.getUnit() + ")");
        tvHistory.setText("Farmer Expected: ₹" + (int) offer.getExpectedPrice() + "/" + offer.getPriceUnit() +
                (offer.getCounterPrice() > 0 ? "\nPrevious Counter: ₹" + (int) offer.getCounterPrice() + "/" + offer.getPriceUnit() : "") +
                (offer.getNegotiationNote() != null ? "\nLatest: " + offer.getNegotiationNote() : ""));

        etCounterPrice.setText(String.valueOf((int) (offer.getCounterPrice() > 0 ? offer.getCounterPrice() : offer.getExpectedPrice() * 0.98)));

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
            brokerViewModel.brokerCounterOffer(offer.getId(), cp, noteStr, dialog::dismiss);
        });

        dialog.show();
    }

    @Override
    public void onRejectOrWithdrawClicked(FarmerBrokerOffer offer) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Decline Offer")
                .setMessage("Are you sure you want to decline this supply offer from " + offer.getFarmerName() + "?")
                .setPositiveButton("Decline", (d, w) -> brokerViewModel.brokerRejectOffer(offer.getId(), "Price/spec not aligned with procurement quota", null))
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
