package com.example.ui.agriwaste;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.R;
import com.example.adapter.AgriWasteMarketplaceAdapter;
import com.example.databinding.DialogBuyAgriWasteBinding;
import com.example.databinding.DialogWasteDetailsBinding;
import com.example.databinding.FragmentAgriWasteMarketplaceBinding;
import com.example.model.AgriWasteItem;
import com.example.model.AgriWastePurchaseRequest;
import com.example.viewmodel.AgriWasteViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Buyer Agri Waste Marketplace Fragment - Strict Role: Purchaser.
 * Allows buyers, biomass companies, and factories to search, view details, and purchase agri waste.
 */
public class AgriWasteMarketplaceFragment extends Fragment implements AgriWasteMarketplaceAdapter.OnMarketplaceActionListener {

    private FragmentAgriWasteMarketplaceBinding binding;
    private AgriWasteViewModel viewModel;
    private AgriWasteMarketplaceAdapter adapter;

    private String currentCategory = "All";
    private String currentSearchQuery = "";

    public static AgriWasteMarketplaceFragment newInstance() {
        return new AgriWasteMarketplaceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAgriWasteMarketplaceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AgriWasteViewModel.class);

        setupRecyclerView();
        setupListeners();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new AgriWasteMarketplaceAdapter();
        adapter.setOnMarketplaceActionListener(this);
        binding.rvMarketplace.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMarketplace.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.btnBackMarketplace.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.etSearchMarketplace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s != null ? s.toString() : "";
                viewModel.filterMarketplace(currentSearchQuery, currentCategory);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupMarketplaceCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipCatStraw) {
                currentCategory = "Straw";
            } else if (id == R.id.chipCatStalks) {
                currentCategory = "Crop Stalks";
            } else if (id == R.id.chipCatSugarcane) {
                currentCategory = "Sugarcane Residue";
            } else if (id == R.id.chipCatHusk) {
                currentCategory = "Husk / Shell";
            } else if (id == R.id.chipCatOther) {
                currentCategory = "Other";
            } else {
                currentCategory = "All";
            }
            viewModel.filterMarketplace(currentSearchQuery, currentCategory);
        });
    }

    private void observeViewModel() {
        viewModel.getMarketplaceListingsLiveData().observe(getViewLifecycleOwner(), items -> {
            List<AgriWasteItem> list = items != null ? new ArrayList<>(items) : new ArrayList<>();
            adapter.setItems(list);

            if (list.isEmpty()) {
                binding.layoutEmptyMarketplace.setVisibility(View.VISIBLE);
                binding.rvMarketplace.setVisibility(View.GONE);
            } else {
                binding.layoutEmptyMarketplace.setVisibility(View.GONE);
                binding.rvMarketplace.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onViewDetails(AgriWasteItem item) {
        if (getContext() == null) return;
        Dialog dialog = new Dialog(getContext());
        DialogWasteDetailsBinding dialogBinding = DialogWasteDetailsBinding.inflate(getLayoutInflater());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogBinding.tvDetailsImageEmoji.setText(item.getImageEmoji());
        dialogBinding.tvDetailsWasteName.setText(item.getWasteName());
        dialogBinding.tvDetailsCategory.setText("Category: " + item.getWasteType() + " • Distance: " + item.getDistanceKm() + " km");

        String info = String.format(Locale.ENGLISH,
                "📦 Available: %.1f %s\n💰 Price: ₹%.0f %s\n👨‍🌾 Farmer: %s (%s)\n📍 Location: %s, %s\n🚚 Pickup Method: %s\n📅 Listed Date: %s\n\n📝 Description:\n%s",
                item.getQuantity(), item.getUnit(), item.getPrice(), item.getPriceUnit(),
                item.getFarmerName(), item.getFarmerPhone(), item.getVillage(), item.getTaluka(),
                item.getPickupPreference(), item.getAvailableDate(), item.getDescription());
        dialogBinding.tvDetailsFullInfo.setText(info);

        dialogBinding.btnCloseDetails.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onBuyWaste(AgriWasteItem item) {
        if (getContext() == null) return;
        Dialog dialog = new Dialog(getContext());
        DialogBuyAgriWasteBinding dialogBinding = DialogBuyAgriWasteBinding.inflate(getLayoutInflater());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogBinding.tvBuyDialogWasteInfo.setText(String.format(Locale.ENGLISH, "%s • Farmer: %s",
                item.getWasteName(), item.getFarmerName()));
        dialogBinding.tvBuyUnitPrice.setText(String.format(Locale.ENGLISH, "₹%.0f / %s",
                item.getPrice(), item.getPriceUnit()));

        final double unitPrice = item.getPrice();
        final double[] currentQty = {5.0};
        double total = currentQty[0] * unitPrice;
        dialogBinding.tvBuyTotalAmount.setText(String.format(Locale.ENGLISH, "₹%.0f", total));

        dialogBinding.etBuyQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double q = Double.parseDouble(s.toString());
                    currentQty[0] = q;
                    dialogBinding.tvBuyTotalAmount.setText(String.format(Locale.ENGLISH, "₹%.0f", q * unitPrice));
                } catch (NumberFormatException ignored) {}
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dialogBinding.btnCancelBuyDialog.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnConfirmBuyOrder.setOnClickListener(v -> {
            String buyerName = dialogBinding.etBuyBuyerName.getText() != null ? dialogBinding.etBuyBuyerName.getText().toString().trim() : "BioEnergy Pellets Pvt Ltd";
            String date = dialogBinding.etBuyDate.getText() != null ? dialogBinding.etBuyDate.getText().toString().trim() : "06 Sept 2026";
            String address = dialogBinding.etBuyAddress.getText() != null ? dialogBinding.etBuyAddress.getText().toString().trim() : "Junnar Biomass Cluster";

            String pickupMethod = dialogBinding.chipPickupDelivery.isChecked() ? "Delivery Partner" : "Buyer Pickup";

            AgriWastePurchaseRequest req = new AgriWastePurchaseRequest(
                    "REQ_ORD_" + UUID.randomUUID().toString().substring(0, 8),
                    item.getId(),
                    item.getFarmerId(),
                    item.getFarmerName(),
                    "BUYER_01",
                    buyerName,
                    "+91 91223 45678",
                    "Biomass Processing Unit",
                    item.getWasteName(),
                    currentQty[0],
                    item.getUnit(),
                    unitPrice,
                    item.getPriceUnit(),
                    currentQty[0] * unitPrice,
                    date,
                    address,
                    "Waiting for Farmer",
                    "Today",
                    pickupMethod
            );

            viewModel.submitPurchaseRequest(req);
            Toast.makeText(getContext(), "Purchase Order Placed! Request sent to farmer.", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
