package com.example.ui.farmer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import com.example.R;
import com.example.model.FarmerDirectOrder;
import com.example.model.FarmerProduceListing;
import com.example.viewmodel.FarmerViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment allowing farmers to list fresh produce directly for customer & wholesale sale,
 * manage stock, and process customer orders through fulfillment stages.
 */
public class FarmerProduceFragment extends Fragment implements
        FarmerProduceAdapter.OnProduceActionListener,
        FarmerProduceOrdersAdapter.OnOrderActionListener {

    private FarmerViewModel viewModel;
    private MaterialToolbar toolbar;
    private TextView tvStatListings;
    private TextView tvStatOrders;
    private TextView tvStatActiveSales;
    private TextView tvStatCompletedSales;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyEmoji;
    private TextView tvEmptyTitle;
    private TextView tvEmptySubtitle;
    private MaterialButton btnEmptyAction;
    private ProgressBar progressBar;
    private ExtendedFloatingActionButton fabListProduce;

    private FarmerProduceAdapter produceAdapter;
    private FarmerProduceOrdersAdapter ordersAdapter;

    private int currentTabPosition = 0; // 0: My Listings, 1: Customer Orders, 2: Active Sales, 3: Completed Sales
    private List<FarmerProduceListing> allListings = new ArrayList<>();
    private List<FarmerDirectOrder> allOrders = new ArrayList<>();

    public static FarmerProduceFragment newInstance() {
        return new FarmerProduceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_farmer_produce, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModel();
        setupListeners();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar_farmer_produce);
        tvStatListings = view.findViewById(R.id.tv_stat_active_listings);
        tvStatOrders = view.findViewById(R.id.tv_stat_orders_count);
        tvStatActiveSales = view.findViewById(R.id.tv_stat_active_sales);
        tvStatCompletedSales = view.findViewById(R.id.tv_stat_completed_sales);
        tabLayout = view.findViewById(R.id.tabs_farmer_produce);
        recyclerView = view.findViewById(R.id.rv_farmer_produce);
        layoutEmpty = view.findViewById(R.id.layout_farmer_produce_empty);
        tvEmptyEmoji = view.findViewById(R.id.tv_empty_produce_emoji);
        tvEmptyTitle = view.findViewById(R.id.tv_empty_produce_title);
        tvEmptySubtitle = view.findViewById(R.id.tv_empty_produce_subtitle);
        btnEmptyAction = view.findViewById(R.id.btn_empty_list_produce);
        progressBar = view.findViewById(R.id.pb_farmer_produce);
        fabListProduce = view.findViewById(R.id.fab_list_produce);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        produceAdapter = new FarmerProduceAdapter(this);
        ordersAdapter = new FarmerProduceOrdersAdapter(this);
        recyclerView.setAdapter(produceAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(FarmerViewModel.class);

        viewModel.getProduceListingsLiveData().observe(getViewLifecycleOwner(), listings -> {
            this.allListings = listings != null ? listings : new ArrayList<>();
            updateStats();
            renderCurrentTab();
        });

        viewModel.getDirectOrdersLiveData().observe(getViewLifecycleOwner(), orders -> {
            this.allOrders = orders != null ? orders : new ArrayList<>();
            updateStats();
            renderCurrentTab();
        });

        viewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), loading -> {
            progressBar.setVisibility(loading != null && loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty() && isAdded()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.loadProduceListings();
        viewModel.loadDirectOrders();
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                renderCurrentTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        fabListProduce.setOnClickListener(v -> showAddOrEditProduceDialog(null));
        btnEmptyAction.setOnClickListener(v -> showAddOrEditProduceDialog(null));
    }

    private void updateStats() {
        int activeListingsCount = 0;
        for (FarmerProduceListing l : allListings) {
            if (!"Cancelled".equalsIgnoreCase(l.getStatus()) && !"Paused".equalsIgnoreCase(l.getStatus())) {
                activeListingsCount++;
            }
        }
        tvStatListings.setText(String.valueOf(activeListingsCount));
        tvStatOrders.setText(String.valueOf(allOrders.size()));

        double activeSalesVal = 0.0;
        double completedSalesVal = 0.0;

        for (FarmerDirectOrder order : allOrders) {
            if ("Delivered".equalsIgnoreCase(order.getStatus()) || "Completed".equalsIgnoreCase(order.getStatus())) {
                completedSalesVal += order.getTotalPrice();
            } else if (!"Cancelled".equalsIgnoreCase(order.getStatus()) && !"Rejected".equalsIgnoreCase(order.getStatus())) {
                activeSalesVal += order.getTotalPrice();
            }
        }

        tvStatActiveSales.setText("₹" + (int) activeSalesVal);
        tvStatCompletedSales.setText("₹" + (int) completedSalesVal);
    }

    private void renderCurrentTab() {
        if (currentTabPosition == 0) {
            // My Listings
            recyclerView.setAdapter(produceAdapter);
            fabListProduce.setVisibility(View.VISIBLE);
            produceAdapter.submitList(allListings);

            if (allListings.isEmpty()) {
                layoutEmpty.setVisibility(View.VISIBLE);
                tvEmptyEmoji.setText("🛒");
                tvEmptyTitle.setText("No Produce Listed Yet");
                tvEmptySubtitle.setText("List your fresh crops, vegetables, grains or fruits to sell directly to local consumers!");
                btnEmptyAction.setVisibility(View.VISIBLE);
                btnEmptyAction.setText("+ List Produce");
            } else {
                layoutEmpty.setVisibility(View.GONE);
            }
        } else if (currentTabPosition == 1) {
            // All Customer Orders
            recyclerView.setAdapter(ordersAdapter);
            fabListProduce.setVisibility(View.GONE);
            ordersAdapter.submitList(allOrders);

            if (allOrders.isEmpty()) {
                layoutEmpty.setVisibility(View.VISIBLE);
                tvEmptyEmoji.setText("📦");
                tvEmptyTitle.setText("No Customer Orders Yet");
                tvEmptySubtitle.setText("Orders placed by consumers in the Customer Store will appear here for you to accept and prepare.");
                btnEmptyAction.setVisibility(View.GONE);
            } else {
                layoutEmpty.setVisibility(View.GONE);
            }
        } else if (currentTabPosition == 2) {
            // Active Sales (Pending, Accepted, Preparing, Ready, Out for Delivery)
            recyclerView.setAdapter(ordersAdapter);
            fabListProduce.setVisibility(View.GONE);
            List<FarmerDirectOrder> active = new ArrayList<>();
            for (FarmerDirectOrder o : allOrders) {
                if (!"Delivered".equalsIgnoreCase(o.getStatus()) &&
                        !"Completed".equalsIgnoreCase(o.getStatus()) &&
                        !"Cancelled".equalsIgnoreCase(o.getStatus()) &&
                        !"Rejected".equalsIgnoreCase(o.getStatus())) {
                    active.add(o);
                }
            }
            ordersAdapter.submitList(active);

            if (active.isEmpty()) {
                layoutEmpty.setVisibility(View.VISIBLE);
                tvEmptyEmoji.setText("⚡");
                tvEmptyTitle.setText("No Active Orders");
                tvEmptySubtitle.setText("You currently have no ongoing orders in preparation or transit.");
                btnEmptyAction.setVisibility(View.GONE);
            } else {
                layoutEmpty.setVisibility(View.GONE);
            }
        } else if (currentTabPosition == 3) {
            // Completed Sales (Delivered / Completed)
            recyclerView.setAdapter(ordersAdapter);
            fabListProduce.setVisibility(View.GONE);
            List<FarmerDirectOrder> completed = new ArrayList<>();
            for (FarmerDirectOrder o : allOrders) {
                if ("Delivered".equalsIgnoreCase(o.getStatus()) || "Completed".equalsIgnoreCase(o.getStatus())) {
                    completed.add(o);
                }
            }
            ordersAdapter.submitList(completed);

            if (completed.isEmpty()) {
                layoutEmpty.setVisibility(View.VISIBLE);
                tvEmptyEmoji.setText("✅");
                tvEmptyTitle.setText("No Completed Sales Yet");
                tvEmptySubtitle.setText("Delivered orders and received payments will appear here as your completed sales history.");
                btnEmptyAction.setVisibility(View.GONE);
            } else {
                layoutEmpty.setVisibility(View.GONE);
            }
        }
    }

    private void showAddOrEditProduceDialog(@Nullable FarmerProduceListing existingListing) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_produce);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvTitle = dialog.findViewById(R.id.tv_dialog_produce_title);
        ChipGroup chipGroupEmoji = dialog.findViewById(R.id.chipgroup_produce_emojis);
        TextInputEditText etName = dialog.findViewById(R.id.et_produce_name);
        AutoCompleteTextView actvCategory = dialog.findViewById(R.id.actv_produce_category);
        TextInputEditText etQty = dialog.findViewById(R.id.et_produce_qty);
        AutoCompleteTextView actvUnit = dialog.findViewById(R.id.actv_produce_unit);
        TextInputEditText etPrice = dialog.findViewById(R.id.et_produce_price);
        AutoCompleteTextView actvGrade = dialog.findViewById(R.id.actv_produce_grade);
        TextInputEditText etHarvestDate = dialog.findViewById(R.id.et_produce_harvest_date);
        TextInputEditText etValidUntil = dialog.findViewById(R.id.et_produce_valid_until);
        TextInputEditText etVillage = dialog.findViewById(R.id.et_produce_village);
        AutoCompleteTextView actvTaluka = dialog.findViewById(R.id.actv_produce_taluka);
        TextInputEditText etDesc = dialog.findViewById(R.id.et_produce_desc);
        MaterialButton btnCancel = dialog.findViewById(R.id.btn_cancel_produce);
        MaterialButton btnPublish = dialog.findViewById(R.id.btn_publish_produce);

        // Setup dropdown adapters
        String[] categories = new String[]{"Vegetables", "Fruits", "Cereals", "Pulses", "Spices", "Oilseeds", "Other Crops"};
        actvCategory.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories));

        String[] units = new String[]{"kg", "quintal", "ton", "crate", "dozen", "piece"};
        actvUnit.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, units));

        String[] grades = new String[]{"Grade A Export", "Grade B Standard", "Organic Certified", "FAQ Standard"};
        actvGrade.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, grades));

        String[] talukas = new String[]{"Baramati", "Haveli", "Junnar", "Khed", "Maval", "Shirur", "Indapur", "Daund", "Purandar"};
        actvTaluka.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, talukas));

        final String[] selectedEmoji = {"🍅"};
        chipGroupEmoji.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_emoji_tomato) selectedEmoji[0] = "🍅";
            else if (checkedId == R.id.chip_emoji_onion) selectedEmoji[0] = "🧅";
            else if (checkedId == R.id.chip_emoji_grain) selectedEmoji[0] = "🌾";
            else if (checkedId == R.id.chip_emoji_mango) selectedEmoji[0] = "🥭";
            else if (checkedId == R.id.chip_emoji_turmeric) selectedEmoji[0] = "🫚";
            else if (checkedId == R.id.chip_emoji_capsicum) selectedEmoji[0] = "🫑";
            else if (checkedId == R.id.chip_emoji_chilli) selectedEmoji[0] = "🌶️";
            else if (checkedId == R.id.chip_emoji_potato) selectedEmoji[0] = "🥔";
        });

        if (existingListing != null) {
            tvTitle.setText("✏️ Edit Produce Listing");
            btnPublish.setText("Save Changes");
            etName.setText(existingListing.getProduceName());
            actvCategory.setText(existingListing.getCategory(), false);
            etQty.setText(String.valueOf(existingListing.getQuantityAvailable()));
            actvUnit.setText(existingListing.getUnit(), false);
            etPrice.setText(String.valueOf(existingListing.getPricePerKg()));
            actvGrade.setText(existingListing.getQualityGrade(), false);
            etHarvestDate.setText(existingListing.getHarvestDate());
            etValidUntil.setText(existingListing.getAvailableUntil());
            etVillage.setText(existingListing.getVillage());
            actvTaluka.setText(existingListing.getTaluka(), false);
            etDesc.setText(existingListing.getDescription());
            if (existingListing.getImageEmoji() != null) {
                selectedEmoji[0] = existingListing.getImageEmoji();
            }
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
            etHarvestDate.setText(sdf.format(new Date()));
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnPublish.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String category = actvCategory.getText() != null ? actvCategory.getText().toString().trim() : "Vegetables";
            String qtyStr = etQty.getText() != null ? etQty.getText().toString().trim() : "0";
            String unit = actvUnit.getText() != null ? actvUnit.getText().toString().trim() : "kg";
            String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "0";
            String grade = actvGrade.getText() != null ? actvGrade.getText().toString().trim() : "Grade A Export";
            String harvestDate = etHarvestDate.getText() != null ? etHarvestDate.getText().toString().trim() : "Today";
            String validUntil = etValidUntil.getText() != null ? etValidUntil.getText().toString().trim() : "30 Days";
            String village = etVillage.getText() != null ? etVillage.getText().toString().trim() : "Baramati";
            String taluka = actvTaluka.getText() != null ? actvTaluka.getText().toString().trim() : "Baramati";
            String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";

            if (name.isEmpty()) {
                etName.setError("Enter produce name");
                return;
            }

            double qty;
            try {
                qty = Double.parseDouble(qtyStr);
                if (qty <= 0) throw new NumberFormatException();
            } catch (Exception e) {
                etQty.setError("Enter valid quantity");
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) throw new NumberFormatException();
            } catch (Exception e) {
                etPrice.setError("Enter valid price");
                return;
            }

            if (existingListing != null) {
                existingListing.setProduceName(name);
                existingListing.setCategory(category);
                existingListing.setQuantityAvailable(qty);
                existingListing.setUnit(unit);
                existingListing.setPricePerKg(price);
                existingListing.setPriceUnit(unit);
                existingListing.setQualityGrade(grade);
                existingListing.setHarvestDate(harvestDate);
                existingListing.setAvailableUntil(validUntil);
                existingListing.setVillage(village);
                existingListing.setTaluka(taluka);
                existingListing.setDescription(desc);
                existingListing.setImageEmoji(selectedEmoji[0]);

                viewModel.updateProduceListing(existingListing);
            } else {
                String id = "prod_" + (100 + (int)(Math.random() * 900));
                String farmerId = viewModel.getFarmerRepository().getFarmerId();
                String farmerName = viewModel.getFarmerRepository().getFarmerName();
                String farmerPhone = viewModel.getFarmerRepository().getFarmerPhone();

                FarmerProduceListing newListing = new FarmerProduceListing(
                        id,
                        farmerId,
                        farmerName,
                        farmerPhone,
                        name,
                        category,
                        qty,
                        unit,
                        price,
                        unit,
                        grade,
                        harvestDate,
                        "Today",
                        validUntil,
                        village,
                        taluka,
                        "Pune",
                        desc,
                        "Available",
                        selectedEmoji[0]
                );

                viewModel.addProduceListing(newListing);
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    // --- OnProduceActionListener Callbacks ---
    @Override
    public void onEdit(FarmerProduceListing listing) {
        showAddOrEditProduceDialog(listing);
    }

    @Override
    public void onPauseResume(FarmerProduceListing listing) {
        viewModel.pauseOrResumeListing(listing.getId());
    }

    @Override
    public void onDelete(FarmerProduceListing listing) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Listing")
                .setMessage("Are you sure you want to remove " + listing.getProduceName() + " from your store listings?")
                .setPositiveButton("Delete", (d, w) -> viewModel.deleteProduceListing(listing.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onItemClick(FarmerProduceListing listing) {
        // Can view detailed information
        showProduceDetailDialog(listing);
    }

    private void showProduceDetailDialog(FarmerProduceListing listing) {
        new AlertDialog.Builder(requireContext())
                .setTitle(listing.getImageEmoji() + " " + listing.getProduceName())
                .setMessage("Category: " + listing.getCategory() +
                        "\nAvailable: " + listing.getQuantityAvailable() + " " + listing.getUnit() +
                        "\nPrice: ₹" + listing.getPricePerKg() + " / " + listing.getPriceUnit() +
                        "\nGrade: " + listing.getQualityGrade() +
                        "\nHarvest Date: " + listing.getHarvestDate() +
                        "\nLocation: " + listing.getFullLocation() +
                        "\nStatus: " + listing.getStatus() +
                        "\n\nDescription: " + (listing.getDescription() != null ? listing.getDescription() : "Direct from field."))
                .setPositiveButton("Close", null)
                .setNeutralButton("Edit", (d, w) -> showAddOrEditProduceDialog(listing))
                .show();
    }

    // --- OnOrderActionListener Callbacks ---
    @Override
    public void onStatusChange(FarmerDirectOrder order, String nextStatus) {
        viewModel.updateDirectOrderStatus(order.getId(), nextStatus);
    }

    @Override
    public void onReject(FarmerDirectOrder order) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Reject Order")
                .setMessage("Are you sure you want to reject order #" + order.getId() + " from " + order.getCustomerName() + "? Stock will be restored to your listing.")
                .setPositiveButton("Reject Order", (d, w) -> {
                    viewModel.updateDirectOrderStatus(order.getId(), "Rejected");
                    // Restore stock
                    FarmerProduceListing l = viewModel.getFarmerRepository().getProduceListingById(order.getListingId());
                    if (l != null) {
                        l.setQuantityAvailable(l.getQuantityAvailable() + order.getQuantity());
                        viewModel.updateProduceListing(l);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onItemClick(FarmerDirectOrder order) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Order Details: #" + order.getId())
                .setMessage("Produce: " + order.getProduceName() + " (" + order.getQuantity() + " " + order.getUnit() + ")" +
                        "\nTotal: ₹" + order.getTotalPrice() + " (" + order.getPaymentStatus() + ")" +
                        "\nCustomer: " + order.getCustomerName() + " (" + order.getCustomerPhone() + ")" +
                        "\nAddress: " + order.getDeliveryAddress() +
                        "\nOrder Date: " + order.getOrderDate() +
                        "\nCurrent Status: " + order.getStatus())
                .setPositiveButton("OK", null)
                .show();
    }
}
