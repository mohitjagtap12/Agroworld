package com.example.ui.customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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
import com.example.viewmodel.CustomerViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment allowing customers to browse fresh produce directly from verified farmers,
 * filter by category and location, order directly with inventory validation,
 * and track order fulfillment lifecycle across 6 stages.
 */
public class CustomerFragment extends Fragment implements
        CustomerProduceAdapter.OnProduceClickListener,
        CustomerOrdersAdapter.OnOrderClickListener {

    private CustomerViewModel viewModel;
    private MaterialToolbar toolbar;
    private EditText etSearch;
    private ImageButton btnClearSearch;
    private TabLayout tabLayout;
    private HorizontalScrollView scrollCategoryFilters;
    private View viewFilterDivider;
    private ChipGroup chipGroupCategories;
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyEmoji;
    private TextView tvEmptyTitle;
    private TextView tvEmptySubtitle;
    private ProgressBar progressBar;

    private CustomerProduceAdapter produceAdapter;
    private CustomerOrdersAdapter ordersAdapter;

    private int currentTabPosition = 0; // 0: Browse Produce, 1: My Orders
    private List<FarmerProduceListing> currentListings = new ArrayList<>();
    private List<FarmerDirectOrder> currentOrders = new ArrayList<>();

    public static CustomerFragment newInstance() {
        return new CustomerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModel();
        setupListeners();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar_customer_store);
        etSearch = view.findViewById(R.id.et_search_customer_produce);
        btnClearSearch = view.findViewById(R.id.btn_clear_search);
        tabLayout = view.findViewById(R.id.tabs_customer_portal);
        scrollCategoryFilters = view.findViewById(R.id.scroll_category_filters);
        viewFilterDivider = view.findViewById(R.id.view_filter_divider);
        chipGroupCategories = view.findViewById(R.id.chipgroup_customer_categories);
        recyclerView = view.findViewById(R.id.rv_customer_content);
        layoutEmpty = view.findViewById(R.id.layout_customer_empty);
        tvEmptyEmoji = view.findViewById(R.id.tv_customer_empty_emoji);
        tvEmptyTitle = view.findViewById(R.id.tv_customer_empty_title);
        tvEmptySubtitle = view.findViewById(R.id.tv_customer_empty_subtitle);
        progressBar = view.findViewById(R.id.pb_customer_loading);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        produceAdapter = new CustomerProduceAdapter(this);
        ordersAdapter = new CustomerOrdersAdapter(this);
        recyclerView.setAdapter(produceAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CustomerViewModel.class);

        viewModel.getProduceListingsLiveData().observe(getViewLifecycleOwner(), listings -> {
            this.currentListings = listings != null ? listings : new ArrayList<>();
            renderCurrentTab();
        });

        viewModel.getCustomerOrdersLiveData().observe(getViewLifecycleOwner(), orders -> {
            this.currentOrders = orders != null ? orders : new ArrayList<>();
            // Update Tab title with count
            TabLayout.Tab ordersTab = tabLayout.getTabAt(1);
            if (ordersTab != null) {
                ordersTab.setText("📦 My Orders (" + currentOrders.size() + ")");
            }
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
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                viewModel.setSearchQuery(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> etSearch.setText(""));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                if (currentTabPosition == 0) {
                    scrollCategoryFilters.setVisibility(View.VISIBLE);
                    viewFilterDivider.setVisibility(View.VISIBLE);
                } else {
                    scrollCategoryFilters.setVisibility(View.GONE);
                    viewFilterDivider.setVisibility(View.GONE);
                }
                renderCurrentTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        chipGroupCategories.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_cat_all) {
                viewModel.setCategoryFilter("All");
            } else if (checkedId == R.id.chip_cat_veg) {
                viewModel.setCategoryFilter("Vegetables");
            } else if (checkedId == R.id.chip_cat_fruit) {
                viewModel.setCategoryFilter("Fruits");
            } else if (checkedId == R.id.chip_cat_cereal) {
                viewModel.setCategoryFilter("Cereals");
            } else if (checkedId == R.id.chip_cat_pulse) {
                viewModel.setCategoryFilter("Pulses");
            } else if (checkedId == R.id.chip_cat_spice) {
                viewModel.setCategoryFilter("Spices");
            }
        });
    }

    private void renderCurrentTab() {
        if (currentTabPosition == 0) {
            // Browse Produce
            recyclerView.setAdapter(produceAdapter);
            produceAdapter.submitList(currentListings);

            if (currentListings.isEmpty()) {
                layoutEmpty.setVisibility(View.VISIBLE);
                tvEmptyEmoji.setText("🌾");
                tvEmptyTitle.setText("No Produce Found");
                tvEmptySubtitle.setText("No harvest matching your search or filters is currently listed. Check back soon!");
            } else {
                layoutEmpty.setVisibility(View.GONE);
            }
        } else {
            // My Orders
            recyclerView.setAdapter(ordersAdapter);
            ordersAdapter.submitList(currentOrders);

            if (currentOrders.isEmpty()) {
                layoutEmpty.setVisibility(View.VISIBLE);
                tvEmptyEmoji.setText("📦");
                tvEmptyTitle.setText("No Orders Placed Yet");
                tvEmptySubtitle.setText("Browse verified farmer produce and place direct orders to support farmers and get fresh crops!");
            } else {
                layoutEmpty.setVisibility(View.GONE);
            }
        }
    }

    // --- OnProduceClickListener Callbacks ---
    @Override
    public void onBuy(FarmerProduceListing listing) {
        showCheckoutDialog(listing);
    }

    @Override
    public void onItemClick(FarmerProduceListing listing) {
        showCheckoutDialog(listing);
    }

    private void showCheckoutDialog(FarmerProduceListing listing) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_produce_checkout);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvEmoji = dialog.findViewById(R.id.tv_checkout_produce_emoji);
        TextView tvName = dialog.findViewById(R.id.tv_checkout_produce_name);
        TextView tvFarmerLoc = dialog.findViewById(R.id.tv_checkout_farmer_location);
        TextView tvUnitPrice = dialog.findViewById(R.id.tv_checkout_unit_price);
        TextView tvStock = dialog.findViewById(R.id.tv_checkout_available_stock);
        TextInputEditText etQty = dialog.findViewById(R.id.et_checkout_quantity);
        TextView tvUnitLabel = dialog.findViewById(R.id.tv_checkout_unit_label);
        MaterialButton btnMinus = dialog.findViewById(R.id.btn_qty_minus);
        MaterialButton btnPlus = dialog.findViewById(R.id.btn_qty_plus);
        MaterialButton chip5 = dialog.findViewById(R.id.chip_preset_5);
        MaterialButton chip10 = dialog.findViewById(R.id.chip_preset_10);
        MaterialButton chip25 = dialog.findViewById(R.id.chip_preset_25);
        MaterialButton chip50 = dialog.findViewById(R.id.chip_preset_50);
        TextView tvTotalCalc = dialog.findViewById(R.id.tv_checkout_total_calc);

        TextInputEditText etCustName = dialog.findViewById(R.id.et_checkout_customer_name);
        TextInputEditText etCustPhone = dialog.findViewById(R.id.et_checkout_customer_phone);
        TextInputEditText etCustAddress = dialog.findViewById(R.id.et_checkout_delivery_address);
        TextInputEditText etCustVillage = dialog.findViewById(R.id.et_checkout_village);
        TextInputEditText etCustTaluka = dialog.findViewById(R.id.et_checkout_taluka);
        RadioGroup rgPayment = dialog.findViewById(R.id.rg_checkout_payment);
        MaterialButton btnCancel = dialog.findViewById(R.id.btn_cancel_checkout);
        MaterialButton btnConfirm = dialog.findViewById(R.id.btn_confirm_checkout);

        // Prepopulate data
        tvEmoji.setText(listing.getImageEmoji() != null ? listing.getImageEmoji() : "🌾");
        tvName.setText(listing.getProduceName());
        tvFarmerLoc.setText("👨‍🌾 " + listing.getFarmerName() + " • " + listing.getFullLocation());
        tvUnitPrice.setText("₹" + listing.getPricePerKg() + " / " + listing.getPriceUnit());
        tvStock.setText(listing.getQuantityAvailable() + " " + listing.getUnit());
        tvUnitLabel.setText(listing.getUnit());

        etCustName.setText(viewModel.getCurrentCustomerName());
        etCustPhone.setText(viewModel.getCurrentCustomerPhone());
        etCustAddress.setText(viewModel.getCurrentDeliveryAddress());
        etCustVillage.setText(viewModel.getCurrentVillage());
        etCustTaluka.setText(viewModel.getCurrentTaluka());

        final double maxStock = listing.getQuantityAvailable();
        final double unitPrice = listing.getPricePerKg();

        // Helper to update total calculation
        Runnable updateTotal = () -> {
            try {
                String str = etQty.getText() != null ? etQty.getText().toString().trim() : "0";
                double qty = Double.parseDouble(str);
                double total = qty * unitPrice;
                tvTotalCalc.setText("₹" + String.format(java.util.Locale.US, "%.1f", total));
            } catch (Exception e) {
                tvTotalCalc.setText("₹0.0");
            }
        };

        double initialQty = Math.min(10.0, maxStock);
        etQty.setText(String.valueOf(initialQty));
        updateTotal.run();

        btnMinus.setOnClickListener(v -> {
            try {
                double curr = Double.parseDouble(etQty.getText().toString().trim());
                if (curr > 1) {
                    etQty.setText(String.valueOf(Math.max(1, curr - 1)));
                    updateTotal.run();
                }
            } catch (Exception ignored) {}
        });

        btnPlus.setOnClickListener(v -> {
            try {
                double curr = Double.parseDouble(etQty.getText().toString().trim());
                if (curr + 1 <= maxStock) {
                    etQty.setText(String.valueOf(curr + 1));
                    updateTotal.run();
                } else {
                    Toast.makeText(requireContext(), "Max available stock is " + maxStock + " " + listing.getUnit(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ignored) {}
        });

        chip5.setOnClickListener(v -> {
            double q = Math.min(5.0, maxStock);
            etQty.setText(String.valueOf(q));
            updateTotal.run();
        });

        chip10.setOnClickListener(v -> {
            double q = Math.min(10.0, maxStock);
            etQty.setText(String.valueOf(q));
            updateTotal.run();
        });

        chip25.setOnClickListener(v -> {
            double q = Math.min(25.0, maxStock);
            etQty.setText(String.valueOf(q));
            updateTotal.run();
        });

        chip50.setOnClickListener(v -> {
            double q = Math.min(50.0, maxStock);
            etQty.setText(String.valueOf(q));
            updateTotal.run();
        });

        etQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotal.run();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String qtyStr = etQty.getText() != null ? etQty.getText().toString().trim() : "0";
            double orderQty;
            try {
                orderQty = Double.parseDouble(qtyStr);
                if (orderQty <= 0) throw new NumberFormatException();
            } catch (Exception e) {
                etQty.setError("Enter a valid quantity");
                return;
            }

            if (orderQty > maxStock) {
                etQty.setError("Only " + maxStock + " " + listing.getUnit() + " available");
                return;
            }

            String cName = etCustName.getText() != null ? etCustName.getText().toString().trim() : "";
            String cPhone = etCustPhone.getText() != null ? etCustPhone.getText().toString().trim() : "";
            String cAddress = etCustAddress.getText() != null ? etCustAddress.getText().toString().trim() : "";
            String cVillage = etCustVillage.getText() != null ? etCustVillage.getText().toString().trim() : "";
            String cTaluka = etCustTaluka.getText() != null ? etCustTaluka.getText().toString().trim() : "";

            if (cName.isEmpty()) {
                etCustName.setError("Enter your name");
                return;
            }
            if (cPhone.isEmpty()) {
                etCustPhone.setError("Enter phone number");
                return;
            }
            if (cAddress.isEmpty()) {
                etCustAddress.setError("Enter delivery address");
                return;
            }

            int checkedPaymentId = rgPayment.getCheckedRadioButtonId();
            String paymentMethod = (checkedPaymentId == R.id.rb_pay_cod) ? "Cash on Delivery" : "Paid Online";

            viewModel.placeProduceOrder(
                    listing.getId(),
                    orderQty,
                    cName,
                    cPhone,
                    cAddress,
                    cVillage,
                    cTaluka,
                    "Pune",
                    paymentMethod
            );

            dialog.dismiss();

            // Switch to My Orders tab
            TabLayout.Tab ordersTab = tabLayout.getTabAt(1);
            if (ordersTab != null) {
                ordersTab.select();
            }
        });

        dialog.show();
    }

    // --- OnOrderClickListener Callbacks ---
    @Override
    public void onTrack(FarmerDirectOrder order) {
        showTrackOrderDialog(order);
    }

    @Override
    public void onCancel(FarmerDirectOrder order) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Order #" + order.getId())
                .setMessage("Are you sure you want to cancel this order for " + order.getProduceName() + "?")
                .setPositiveButton("Yes, Cancel", (d, w) -> viewModel.cancelCustomerOrder(order.getId()))
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onItemClick(FarmerDirectOrder order) {
        showTrackOrderDialog(order);
    }

    private void showTrackOrderDialog(FarmerDirectOrder order) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_track_produce_order);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvEmoji = dialog.findViewById(R.id.tv_track_emoji);
        TextView tvOrderId = dialog.findViewById(R.id.tv_track_order_id);
        TextView tvProduceName = dialog.findViewById(R.id.tv_track_produce_name);
        TextView tvStatusIcon = dialog.findViewById(R.id.tv_track_status_icon);
        TextView tvStatusTitle = dialog.findViewById(R.id.tv_track_status_title);
        TextView tvStatusDesc = dialog.findViewById(R.id.tv_track_status_desc);

        TextView ivStep1 = dialog.findViewById(R.id.iv_step_1);
        TextView tvStep1Title = dialog.findViewById(R.id.tv_step_1_title);
        View lineStep1 = dialog.findViewById(R.id.line_step_1);

        TextView ivStep2 = dialog.findViewById(R.id.iv_step_2);
        TextView tvStep2Title = dialog.findViewById(R.id.tv_step_2_title);
        View lineStep2 = dialog.findViewById(R.id.line_step_2);

        TextView ivStep3 = dialog.findViewById(R.id.iv_step_3);
        TextView tvStep3Title = dialog.findViewById(R.id.tv_step_3_title);
        View lineStep3 = dialog.findViewById(R.id.line_step_3);

        TextView ivStep4 = dialog.findViewById(R.id.iv_step_4);
        TextView tvStep4Title = dialog.findViewById(R.id.tv_step_4_title);
        View lineStep4 = dialog.findViewById(R.id.line_step_4);

        TextView ivStep5 = dialog.findViewById(R.id.iv_step_5);
        TextView tvStep5Title = dialog.findViewById(R.id.tv_step_5_title);
        View lineStep5 = dialog.findViewById(R.id.line_step_5);

        TextView ivStep6 = dialog.findViewById(R.id.iv_step_6);
        TextView tvStep6Title = dialog.findViewById(R.id.tv_step_6_title);

        MaterialButton btnClose = dialog.findViewById(R.id.btn_close_track_dialog);

        tvEmoji.setText(order.getImageEmoji() != null ? order.getImageEmoji() : "🌾");
        tvOrderId.setText("Order #" + order.getId());
        tvProduceName.setText(order.getProduceName() + " (" + order.getQuantity() + " " + order.getUnit() + ") • Total ₹" + order.getTotalPrice());

        String status = order.getStatus() != null ? order.getStatus() : "Order Placed";
        tvStatusTitle.setText("Current Status: " + status);

        // Step progression index (1 to 6)
        int currentStep = 1;
        if ("Accepted".equalsIgnoreCase(status) || "Confirmed".equalsIgnoreCase(status)) {
            currentStep = 2;
            tvStatusIcon.setText("👨‍🌾");
            tvStatusDesc.setText("Farmer has accepted your order and scheduled harvesting.");
        } else if ("Preparing".equalsIgnoreCase(status) || "Packed".equalsIgnoreCase(status)) {
            currentStep = 3;
            tvStatusIcon.setText("📦");
            tvStatusDesc.setText("Farmer is harvesting and packaging your fresh produce.");
        } else if ("Ready for Pickup".equalsIgnoreCase(status) || "Ready".equalsIgnoreCase(status)) {
            currentStep = 4;
            tvStatusIcon.setText("🚚");
            tvStatusDesc.setText("Produce is ready for vehicle loading and transport dispatch.");
        } else if ("Out for Delivery".equalsIgnoreCase(status)) {
            currentStep = 5;
            tvStatusIcon.setText("🛵");
            tvStatusDesc.setText("Delivery partner is in transit to your address.");
        } else if ("Delivered".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status)) {
            currentStep = 6;
            tvStatusIcon.setText("✅");
            tvStatusDesc.setText("Order delivered successfully. Enjoy farm fresh produce!");
        } else {
            // Order Placed
            currentStep = 1;
            tvStatusIcon.setText("📝");
            tvStatusDesc.setText("Order placed successfully. Waiting for farmer confirmation.");
        }

        // Apply visual active / inactive states
        updateStepView(ivStep1, tvStep1Title, lineStep1, currentStep >= 1, currentStep > 1);
        updateStepView(ivStep2, tvStep2Title, lineStep2, currentStep >= 2, currentStep > 2);
        updateStepView(ivStep3, tvStep3Title, lineStep3, currentStep >= 3, currentStep > 3);
        updateStepView(ivStep4, tvStep4Title, lineStep4, currentStep >= 4, currentStep > 4);
        updateStepView(ivStep5, tvStep5Title, lineStep5, currentStep >= 5, currentStep > 5);
        updateStepView(ivStep6, tvStep6Title, null, currentStep >= 6, false);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateStepView(TextView ivStep, TextView tvTitle, @Nullable View lineNext, boolean isReached, boolean isPast) {
        if (isReached) {
            ivStep.setBackgroundResource(R.drawable.bg_circle_step_active);
            ivStep.setTextColor(0xFFFFFFFF);
            ivStep.setText("✓");
            tvTitle.setTextColor(0xFF2E7D32);
        } else {
            ivStep.setBackgroundResource(R.drawable.bg_circle_step_inactive);
            ivStep.setTextColor(0xFF757575);
            tvTitle.setTextColor(0xFF757575);
        }

        if (lineNext != null) {
            lineNext.setBackgroundColor(isPast ? 0xFF2E7D32 : 0xFFCCCCCC);
        }
    }
}
