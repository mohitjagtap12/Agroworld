package com.example.ui.agriwaste;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
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
import com.example.adapter.AgriWasteAdapter;
import com.example.adapter.AgriWasteRequestAdapter;
import com.example.databinding.DialogListAgriWasteBinding;
import com.example.databinding.DialogWasteDetailsBinding;
import com.example.databinding.FragmentFarmerAgriWasteBinding;
import com.example.model.AgriWasteItem;
import com.example.model.AgriWastePurchaseRequest;
import com.example.viewmodel.AgriWasteViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Farmer Agri Waste Hub Fragment - Strict Role: Supplier & Seller.
 * Allows farmer to list waste, edit/delete listings, and accept/reject buyer requests.
 */
public class FarmerAgriWasteFragment extends Fragment implements
        AgriWasteAdapter.OnWasteItemActionListener,
        AgriWasteRequestAdapter.OnBuyerRequestActionListener {

    private FragmentFarmerAgriWasteBinding binding;
    private AgriWasteViewModel viewModel;
    private AgriWasteAdapter listingsAdapter;
    private AgriWasteRequestAdapter requestsAdapter;

    private List<AgriWasteItem> myListings = new ArrayList<>();
    private List<AgriWastePurchaseRequest> myRequests = new ArrayList<>();
    private int currentTab = 0; // 0: My Listings, 1: Buyer Requests, 2: Sales Orders

    public static FarmerAgriWasteFragment newInstance() {
        return new FarmerAgriWasteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFarmerAgriWasteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AgriWasteViewModel.class);

        setupAdapters();
        setupListeners();
        observeViewModel();
    }

    private void setupAdapters() {
        listingsAdapter = new AgriWasteAdapter();
        listingsAdapter.setOnWasteItemActionListener(this);

        requestsAdapter = new AgriWasteRequestAdapter();
        requestsAdapter.setOnBuyerRequestActionListener(this);

        binding.rvAgriWaste.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAgriWaste.setAdapter(listingsAdapter);
    }

    private void setupListeners() {
        binding.btnBackAgriWaste.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.btnAddWasteListing.setOnClickListener(v -> showAddWasteDialog(null));
        binding.btnEmptyListWaste.setOnClickListener(v -> showAddWasteDialog(null));

        binding.chipGroupWasteTabs.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipTabMyListings) {
                currentTab = 0;
            } else if (id == R.id.chipTabBuyerRequests) {
                currentTab = 1;
            } else if (id == R.id.chipTabSalesOrders) {
                currentTab = 2;
            }
            updateTabContent();
        });
    }

    private void observeViewModel() {
        binding.progressBarWaste.setVisibility(View.VISIBLE);

        viewModel.getFarmerListingsLiveData().observe(getViewLifecycleOwner(), listings -> {
            binding.progressBarWaste.setVisibility(View.GONE);
            myListings = listings != null ? new ArrayList<>(listings) : new ArrayList<>();
            binding.chipTabMyListings.setText(String.format("📋 My Listings (%d)", myListings.size()));
            if (currentTab == 0) updateTabContent();
        });

        viewModel.getBuyerRequestsLiveData().observe(getViewLifecycleOwner(), requests -> {
            myRequests = requests != null ? new ArrayList<>(requests) : new ArrayList<>();
            binding.chipTabBuyerRequests.setText(String.format("📩 Buyer Requests (%d)", myRequests.size()));
            if (currentTab == 1) updateTabContent();
        });

        viewModel.getOrdersLiveData().observe(getViewLifecycleOwner(), orders -> {
            int count = orders != null ? orders.size() : 0;
            binding.chipTabSalesOrders.setText(String.format("📦 Sales Orders (%d)", count));
        });
    }

    private void updateTabContent() {
        if (currentTab == 0) {
            binding.rvAgriWaste.setAdapter(listingsAdapter);
            listingsAdapter.setListings(myListings);
            toggleEmptyState(myListings.isEmpty(), "No waste listings yet",
                    "List your crop residue (straw, sugarcane trash, stalks) and earn extra income from verified buyers.");
        } else if (currentTab == 1) {
            binding.rvAgriWaste.setAdapter(requestsAdapter);
            requestsAdapter.setRequests(myRequests);
            toggleEmptyState(myRequests.isEmpty(), "No buyer requests yet",
                    "When biomass factories, pellet units, or cattle farms request your waste, they will appear here.");
        } else {
            binding.rvAgriWaste.setAdapter(listingsAdapter);
            List<AgriWasteItem> sold = new ArrayList<>();
            for (AgriWasteItem item : myListings) {
                if ("Sold Out".equalsIgnoreCase(item.getStatus()) || "Partially Sold".equalsIgnoreCase(item.getStatus())) {
                    sold.add(item);
                }
            }
            listingsAdapter.setListings(sold);
            toggleEmptyState(sold.isEmpty(), "No completed sales orders yet",
                    "Your completed and delivered agri waste transactions will appear here.");
        }
    }

    private void toggleEmptyState(boolean isEmpty, String title, String subtitle) {
        if (isEmpty) {
            binding.layoutEmptyWaste.setVisibility(View.VISIBLE);
            binding.rvAgriWaste.setVisibility(View.GONE);
            binding.tvEmptyTitle.setText(title);
            binding.tvEmptySubtitle.setText(subtitle);
        } else {
            binding.layoutEmptyWaste.setVisibility(View.GONE);
            binding.rvAgriWaste.setVisibility(View.VISIBLE);
        }
    }

    private void showAddWasteDialog(AgriWasteItem editItem) {
        if (getContext() == null) return;
        Dialog dialog = new Dialog(getContext());
        DialogListAgriWasteBinding dialogBinding = DialogListAgriWasteBinding.inflate(getLayoutInflater());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (editItem != null) {
            dialogBinding.tvDialogWasteTitle.setText("✏️ Edit Agri Waste Listing");
            dialogBinding.etDialogWasteName.setText(editItem.getWasteName());
            dialogBinding.etDialogWasteQuantity.setText(String.valueOf(editItem.getQuantity()));
            dialogBinding.etDialogWasteUnit.setText(editItem.getUnit());
            dialogBinding.etDialogWastePrice.setText(String.valueOf(editItem.getPrice()));
            dialogBinding.etDialogWastePriceUnit.setText(editItem.getPriceUnit());
            dialogBinding.etDialogWasteAvailableDate.setText(editItem.getAvailableDate());
            dialogBinding.etDialogWastePickupOption.setText(editItem.getPickupPreference());
            dialogBinding.etDialogWasteVillage.setText(editItem.getVillage());
            dialogBinding.etDialogWasteTaluka.setText(editItem.getTaluka());
            dialogBinding.etDialogWasteDescription.setText(editItem.getDescription());
            dialogBinding.btnPublishWasteDialog.setText("Update Listing");
        }

        dialogBinding.btnCancelWasteDialog.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnPublishWasteDialog.setOnClickListener(v -> {
            String name = dialogBinding.etDialogWasteName.getText() != null ? dialogBinding.etDialogWasteName.getText().toString().trim() : "";
            String qtyStr = dialogBinding.etDialogWasteQuantity.getText() != null ? dialogBinding.etDialogWasteQuantity.getText().toString().trim() : "10";
            String unit = dialogBinding.etDialogWasteUnit.getText() != null ? dialogBinding.etDialogWasteUnit.getText().toString().trim() : "Tons";
            String priceStr = dialogBinding.etDialogWastePrice.getText() != null ? dialogBinding.etDialogWastePrice.getText().toString().trim() : "1500";
            String priceUnit = dialogBinding.etDialogWastePriceUnit.getText() != null ? dialogBinding.etDialogWastePriceUnit.getText().toString().trim() : "per ton";
            String date = dialogBinding.etDialogWasteAvailableDate.getText() != null ? dialogBinding.etDialogWasteAvailableDate.getText().toString().trim() : "Immediate";
            String pickup = dialogBinding.etDialogWastePickupOption.getText() != null ? dialogBinding.etDialogWastePickupOption.getText().toString().trim() : "Buyer Pickup & Delivery";
            String village = dialogBinding.etDialogWasteVillage.getText() != null ? dialogBinding.etDialogWasteVillage.getText().toString().trim() : "Narayangaon";
            String taluka = dialogBinding.etDialogWasteTaluka.getText() != null ? dialogBinding.etDialogWasteTaluka.getText().toString().trim() : "Junnar";
            String desc = dialogBinding.etDialogWasteDescription.getText() != null ? dialogBinding.etDialogWasteDescription.getText().toString().trim() : "";

            if (name.isEmpty()) {
                dialogBinding.etDialogWasteName.setError("Please enter waste name");
                return;
            }

            double qty = 10.0;
            double price = 1500.0;
            try { qty = Double.parseDouble(qtyStr); } catch (NumberFormatException ignored) {}
            try { price = Double.parseDouble(priceStr); } catch (NumberFormatException ignored) {}

            String wasteType = "Straw";
            String emoji = "🌾";
            int checkedId = dialogBinding.chipGroupDialogWasteType.getCheckedChipId();
            if (checkedId == R.id.chipTypeSugarcane) {
                wasteType = "Sugarcane Residue";
                emoji = "🎋";
            } else if (checkedId == R.id.chipTypeMaize) {
                wasteType = "Crop Stalks";
                emoji = "🌱";
            } else if (checkedId == R.id.chipTypeCotton) {
                wasteType = "Cotton Stalk";
                emoji = "☁️";
            } else if (checkedId == R.id.chipTypeHusk) {
                wasteType = "Husk / Shell";
                emoji = "🥥";
            } else if (checkedId == R.id.chipTypeOther) {
                wasteType = "Other";
                emoji = "♻️";
            }

            if (editItem != null) {
                editItem.setWasteName(name);
                editItem.setWasteType(wasteType);
                editItem.setImageEmoji(emoji);
                editItem.setQuantity(qty);
                editItem.setUnit(unit);
                editItem.setPrice(price);
                editItem.setPriceUnit(priceUnit);
                editItem.setAvailableDate(date);
                editItem.setPickupPreference(pickup);
                editItem.setVillage(village);
                editItem.setTaluka(taluka);
                editItem.setDescription(desc);
                viewModel.updateListing(editItem);
                Toast.makeText(getContext(), "Listing updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                AgriWasteItem newItem = new AgriWasteItem(
                        "WST_" + UUID.randomUUID().toString().substring(0, 8),
                        "FARMER_MH_01",
                        "Ramesh Patil",
                        "+91 98220 14589",
                        wasteType,
                        name,
                        "Crop Residue",
                        qty,
                        qty,
                        unit,
                        price,
                        priceUnit,
                        date,
                        village,
                        taluka,
                        "Pune",
                        2.5,
                        desc,
                        emoji,
                        pickup,
                        "Available",
                        "Today",
                        "Today"
                );
                viewModel.addListing(newItem);
                Toast.makeText(getContext(), "Agri waste published to marketplace!", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onView(AgriWasteItem item) {
        if (getContext() == null) return;
        Dialog dialog = new Dialog(getContext());
        DialogWasteDetailsBinding dialogBinding = DialogWasteDetailsBinding.inflate(getLayoutInflater());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogBinding.tvDetailsImageEmoji.setText(item.getImageEmoji());
        dialogBinding.tvDetailsWasteName.setText(item.getWasteName());
        dialogBinding.tvDetailsCategory.setText("Category: " + item.getWasteType() + " • " + item.getStatus());

        String info = String.format("📦 Available Quantity: %.1f %s\n💰 Price: ₹%.0f %s\n👨‍🌾 Farmer: %s (%s)\n📍 Location: %s, %s, %s\n🚚 Pickup Method: %s\n📅 Listed Date: %s\n\n📝 Description:\n%s",
                item.getQuantity(), item.getUnit(), item.getPrice(), item.getPriceUnit(),
                item.getFarmerName(), item.getFarmerPhone(), item.getVillage(), item.getTaluka(), item.getDistrict(),
                item.getPickupPreference(), item.getAvailableDate(), item.getDescription());
        dialogBinding.tvDetailsFullInfo.setText(info);

        dialogBinding.btnCloseDetails.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onEdit(AgriWasteItem item) {
        showAddWasteDialog(item);
    }

    @Override
    public void onDelete(AgriWasteItem item) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Listing")
                .setMessage("Are you sure you want to remove " + item.getWasteName() + " from the marketplace?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteListing(item.getId());
                    Toast.makeText(getContext(), "Listing deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onAccept(AgriWastePurchaseRequest request) {
        viewModel.respondToBuyerRequest(request.getId(), true);
        Toast.makeText(getContext(), "Purchase Request Accepted! Buyer and Delivery partner notified.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReject(AgriWastePurchaseRequest request) {
        viewModel.respondToBuyerRequest(request.getId(), false);
        Toast.makeText(getContext(), "Purchase Request Rejected.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
