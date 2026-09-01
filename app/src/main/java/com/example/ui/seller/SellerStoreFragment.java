package com.example.ui.seller;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.adapter.ProductCardAdapter;
import com.example.adapter.SellerOrderAdapter;
import com.example.model.ProductOrder;
import com.example.model.SellerProduct;
import com.example.model.SellerProfile;
import com.example.viewmodel.SellerViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Seller Store & Inventory Management Portal (Java + XML).
 * Handles store metrics, catalog listing, product CRUD, inventory stock adjustments, and farmer order fulfillment.
 */
public class SellerStoreFragment extends Fragment implements
        ProductCardAdapter.OnProductActionListener,
        SellerOrderAdapter.OnOrderActionListener {

    private SellerViewModel sellerViewModel;
    private ProductCardAdapter productAdapter;
    private SellerOrderAdapter orderAdapter;

    private MaterialToolbar toolbarSeller;
    private TextView tvSellerShopName;
    private TextView tvSellerOwnerAndPhone;
    private TextView tvSellerLocation;
    private TextView tvSellerVerifiedBadge;

    private TextView tvMetricTotalProducts;
    private TextView tvMetricActiveProducts;
    private TextView tvMetricPendingOrders;
    private TextView tvMetricTotalRevenue;

    private TabLayout tabLayoutSeller;
    private HorizontalScrollView scrollSellerFilters;
    private ChipGroup chipGroupSellerCategories;
    private LinearLayout layoutEmptySeller;
    private TextView tvEmptySellerTitle;
    private TextView tvEmptySellerSubtitle;
    private RecyclerView rvSellerProducts;
    private RecyclerView rvSellerOrders;
    private ExtendedFloatingActionButton fabAddProduct;

    private String currentCategoryFilter = "All";
    private List<SellerProduct> cachedProducts = new ArrayList<>();

    public static SellerStoreFragment newInstance() {
        return new SellerStoreFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sellerViewModel = new ViewModelProvider(this).get(SellerViewModel.class);

        initViews(view);
        setupRecyclerViews();
        setupListeners();
        observeViewModel();

        sellerViewModel.refreshAllData();
    }

    private void initViews(View view) {
        toolbarSeller = view.findViewById(R.id.toolbarSeller);
        tvSellerShopName = view.findViewById(R.id.tvSellerShopName);
        tvSellerOwnerAndPhone = view.findViewById(R.id.tvSellerOwnerAndPhone);
        tvSellerLocation = view.findViewById(R.id.tvSellerLocation);
        tvSellerVerifiedBadge = view.findViewById(R.id.tvSellerVerifiedBadge);

        tvMetricTotalProducts = view.findViewById(R.id.tvMetricTotalProducts);
        tvMetricActiveProducts = view.findViewById(R.id.tvMetricActiveProducts);
        tvMetricPendingOrders = view.findViewById(R.id.tvMetricPendingOrders);
        tvMetricTotalRevenue = view.findViewById(R.id.tvMetricTotalRevenue);

        tabLayoutSeller = view.findViewById(R.id.tabLayoutSeller);
        scrollSellerFilters = view.findViewById(R.id.scrollSellerFilters);
        chipGroupSellerCategories = view.findViewById(R.id.chipGroupSellerCategories);
        layoutEmptySeller = view.findViewById(R.id.layoutEmptySeller);
        tvEmptySellerTitle = view.findViewById(R.id.tvEmptySellerTitle);
        tvEmptySellerSubtitle = view.findViewById(R.id.tvEmptySellerSubtitle);
        rvSellerProducts = view.findViewById(R.id.rvSellerProducts);
        rvSellerOrders = view.findViewById(R.id.rvSellerOrders);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);
    }

    private void setupRecyclerViews() {
        productAdapter = new ProductCardAdapter(true, this);
        rvSellerProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSellerProducts.setAdapter(productAdapter);

        orderAdapter = new SellerOrderAdapter(this);
        rvSellerOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSellerOrders.setAdapter(orderAdapter);
    }

    private void setupListeners() {
        toolbarSeller.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        fabAddProduct.setOnClickListener(v -> showAddProductDialog(null));

        tabLayoutSeller.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // Products & Stock
                    rvSellerProducts.setVisibility(View.VISIBLE);
                    rvSellerOrders.setVisibility(View.GONE);
                    scrollSellerFilters.setVisibility(View.VISIBLE);
                    fabAddProduct.setVisibility(View.VISIBLE);
                    filterSellerProducts(currentCategoryFilter);
                } else {
                    // Orders
                    rvSellerProducts.setVisibility(View.GONE);
                    rvSellerOrders.setVisibility(View.VISIBLE);
                    scrollSellerFilters.setVisibility(View.GONE);
                    fabAddProduct.setVisibility(View.GONE);
                    checkOrdersEmpty();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        chipGroupSellerCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentCategoryFilter = "All";
            } else {
                int id = checkedIds.get(0);
                if (id == R.id.chipSellerCatAll) currentCategoryFilter = "All";
                else if (id == R.id.chipSellerCatSeeds) currentCategoryFilter = "Seeds";
                else if (id == R.id.chipSellerCatFertilizers) currentCategoryFilter = "Fertilizers";
                else if (id == R.id.chipSellerCatCropProtection) currentCategoryFilter = "Crop Protection";
                else if (id == R.id.chipSellerCatEquipment) currentCategoryFilter = "Farm Equipment";
                else if (id == R.id.chipSellerCatTools) currentCategoryFilter = "Tools";
            }
            filterSellerProducts(currentCategoryFilter);
        });
    }

    private void observeViewModel() {
        sellerViewModel.getProfileLiveData().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                tvSellerShopName.setText(profile.getShopName());
                tvSellerOwnerAndPhone.setText(profile.getOwnerName() + " • " + profile.getPhone());
                tvSellerLocation.setText("📍 " + profile.getVillage() + ", " + profile.getTaluka() + " (" + profile.getDistrict() + ") • GSTIN: " + profile.getGstin());
            }
        });

        sellerViewModel.getMetricsLiveData().observe(getViewLifecycleOwner(), (Map<String, Object> metrics) -> {
            if (metrics != null) {
                Object totalProd = metrics.get("totalProducts");
                Object activeProd = metrics.get("activeProducts");
                Object pendingOrd = metrics.get("pendingOrders");
                Object totalRev = metrics.get("totalRevenue");

                if (totalProd != null) tvMetricTotalProducts.setText(String.valueOf(totalProd));
                if (activeProd != null) tvMetricActiveProducts.setText(String.valueOf(activeProd));
                if (pendingOrd != null) tvMetricPendingOrders.setText(String.valueOf(pendingOrd));
                if (totalRev != null) tvMetricTotalRevenue.setText(String.format("₹%.0f", (Double) totalRev));
            }
        });

        sellerViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            this.cachedProducts = (products != null) ? products : new ArrayList<>();
            filterSellerProducts(currentCategoryFilter);
        });

        sellerViewModel.getOrdersLiveData().observe(getViewLifecycleOwner(), orders -> {
            orderAdapter.setOrders(orders);
            if (tabLayoutSeller.getSelectedTabPosition() == 1) {
                checkOrdersEmpty();
            }
        });

        sellerViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterSellerProducts(String category) {
        List<SellerProduct> filtered = new ArrayList<>();
        for (SellerProduct p : cachedProducts) {
            if ("All".equalsIgnoreCase(category) || (p.getCategory() != null && p.getCategory().equalsIgnoreCase(category))) {
                filtered.add(p);
            }
        }
        productAdapter.setProducts(filtered);

        if (tabLayoutSeller.getSelectedTabPosition() == 0) {
            if (filtered.isEmpty()) {
                layoutEmptySeller.setVisibility(View.VISIBLE);
                rvSellerProducts.setVisibility(View.GONE);
                tvEmptySellerTitle.setText("No products in " + category);
                tvEmptySellerSubtitle.setText("Tap + Add Product to list agricultural inputs in this category");
            } else {
                layoutEmptySeller.setVisibility(View.GONE);
                rvSellerProducts.setVisibility(View.VISIBLE);
            }
        }
    }

    private void checkOrdersEmpty() {
        if (orderAdapter.getItemCount() == 0) {
            layoutEmptySeller.setVisibility(View.VISIBLE);
            rvSellerOrders.setVisibility(View.GONE);
            tvEmptySellerTitle.setText("No orders yet");
            tvEmptySellerSubtitle.setText("Farmer purchase orders will appear here for fulfillment");
        } else {
            layoutEmptySeller.setVisibility(View.GONE);
            rvSellerOrders.setVisibility(View.VISIBLE);
        }
    }

    // =========================================================================
    // PRODUCT ACTIONS (SELLER)
    // =========================================================================

    @Override
    public void onViewDetails(SellerProduct product) {}

    @Override
    public void onAddToCart(SellerProduct product) {}

    @Override
    public void onSellerEdit(SellerProduct product) {
        showAddProductDialog(product);
    }

    @Override
    public void onSellerUpdateStock(SellerProduct product) {
        showUpdateStockDialog(product);
    }

    private void showAddProductDialog(@Nullable SellerProduct existingProduct) {
        Dialog dialog = new Dialog(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_Dialog);
        dialog.setContentView(R.layout.dialog_add_product);

        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextInputEditText etProductName = dialog.findViewById(R.id.etProductName);
        AutoCompleteTextView actvProductCategory = dialog.findViewById(R.id.actvProductCategory);
        TextInputEditText etProductBrand = dialog.findViewById(R.id.etProductBrand);
        TextInputEditText etProductPrice = dialog.findViewById(R.id.etProductPrice);
        AutoCompleteTextView actvProductUnit = dialog.findViewById(R.id.actvProductUnit);
        TextInputEditText etProductStock = dialog.findViewById(R.id.etProductStock);
        TextInputEditText etProductEmoji = dialog.findViewById(R.id.etProductEmoji);
        TextInputEditText etSuitableCrops = dialog.findViewById(R.id.etSuitableCrops);
        TextInputEditText etProductDescription = dialog.findViewById(R.id.etProductDescription);
        MaterialButton btnSubmitProduct = dialog.findViewById(R.id.btnSubmitProduct);

        String[] categories = new String[]{
                "Seeds", "Fertilizers", "Crop Protection", "Farm Equipment", "Tools", "Irrigation Equipment", "Other Agricultural Supplies"
        };
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        actvProductCategory.setAdapter(catAdapter);

        String[] units = new String[]{"kg", "bag", "bottle", "packet", "piece", "roll", "litre"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, units);
        actvProductUnit.setAdapter(unitAdapter);

        if (existingProduct != null) {
            tvDialogTitle.setText("✏️ Edit Product Details");
            btnSubmitProduct.setText("UPDATE PRODUCT");
            etProductName.setText(existingProduct.getName());
            actvProductCategory.setText(existingProduct.getCategory(), false);
            etProductBrand.setText(existingProduct.getBrand());
            etProductPrice.setText(String.valueOf(existingProduct.getPrice()));
            actvProductUnit.setText(existingProduct.getUnit(), false);
            etProductStock.setText(String.valueOf(existingProduct.getStock()));
            etProductEmoji.setText(existingProduct.getImageEmoji());
            etSuitableCrops.setText(existingProduct.getSuitableCrops());
            etProductDescription.setText(existingProduct.getDescription());
        }

        btnSubmitProduct.setOnClickListener(v -> {
            String name = etProductName.getText() != null ? etProductName.getText().toString().trim() : "";
            String category = actvProductCategory.getText().toString().trim();
            String brand = etProductBrand.getText() != null ? etProductBrand.getText().toString().trim() : "";
            String priceStr = etProductPrice.getText() != null ? etProductPrice.getText().toString().trim() : "";
            String unit = actvProductUnit.getText().toString().trim();
            String stockStr = etProductStock.getText() != null ? etProductStock.getText().toString().trim() : "";
            String emoji = etProductEmoji.getText() != null ? etProductEmoji.getText().toString().trim() : "🌱";
            String suitableCrops = etSuitableCrops.getText() != null ? etSuitableCrops.getText().toString().trim() : "All Crops";
            String description = etProductDescription.getText() != null ? etProductDescription.getText().toString().trim() : "";

            if (name.isEmpty() || brand.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all mandatory product fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            if (existingProduct != null) {
                existingProduct.setName(name);
                existingProduct.setCategory(category);
                existingProduct.setBrand(brand);
                existingProduct.setPrice(price);
                existingProduct.setUnit(unit);
                existingProduct.setStock(stock);
                existingProduct.setImageEmoji(emoji);
                existingProduct.setSuitableCrops(suitableCrops);
                existingProduct.setDescription(description);

                sellerViewModel.updateProduct(existingProduct);
            } else {
                SellerProduct newProd = new SellerProduct(
                        "", sellerViewModel.getCurrentSellerId(), "Kisan Agri Mart", "+91 98220 54321",
                        name, category, brand, description, price, unit, stock, 4.8, emoji, "Narayangaon, Junnar",
                        "Active", suitableCrops, "Follow manufacturer label instructions."
                );
                sellerViewModel.addProduct(newProd);
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private void showUpdateStockDialog(SellerProduct product) {
        Dialog dialog = new Dialog(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_Dialog);
        dialog.setContentView(R.layout.dialog_update_stock);

        TextView tvUpdateStockProductName = dialog.findViewById(R.id.tvUpdateStockProductName);
        TextInputEditText etUpdateStockQuantity = dialog.findViewById(R.id.etUpdateStockQuantity);
        TextInputEditText etUpdateStockPrice = dialog.findViewById(R.id.etUpdateStockPrice);
        MaterialButton btnCancelStock = dialog.findViewById(R.id.btnCancelStock);
        MaterialButton btnSaveStock = dialog.findViewById(R.id.btnSaveStock);

        tvUpdateStockProductName.setText(product.getName() + " (" + product.getUnit() + ")");
        etUpdateStockQuantity.setText(String.valueOf(product.getStock()));
        etUpdateStockPrice.setText(String.valueOf(product.getPrice()));

        btnCancelStock.setOnClickListener(v -> dialog.dismiss());

        btnSaveStock.setOnClickListener(v -> {
            String qtyStr = etUpdateStockQuantity.getText() != null ? etUpdateStockQuantity.getText().toString().trim() : "";
            String priceStr = etUpdateStockPrice.getText() != null ? etUpdateStockPrice.getText().toString().trim() : "";

            if (qtyStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter valid stock and price values", Toast.LENGTH_SHORT).show();
                return;
            }

            int newStock = Integer.parseInt(qtyStr);
            double newPrice = Double.parseDouble(priceStr);

            sellerViewModel.updateStockAndPrice(product.getId(), newStock, newPrice);
            dialog.dismiss();
        });

        dialog.show();
    }

    // =========================================================================
    // ORDER FULFILLMENT ACTIONS (SELLER)
    // =========================================================================

    @Override
    public void onAcceptOrder(ProductOrder order) {
        sellerViewModel.acceptOrder(order.getId());
    }

    @Override
    public void onRejectOrder(ProductOrder order) {
        sellerViewModel.rejectOrder(order.getId());
    }

    @Override
    public void onMarkPacked(ProductOrder order) {
        sellerViewModel.markPacked(order.getId());
    }

    @Override
    public void onReadyForDelivery(ProductOrder order) {
        sellerViewModel.markReadyForDelivery(order.getId());
    }
}
