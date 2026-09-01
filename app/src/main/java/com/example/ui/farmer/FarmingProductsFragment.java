package com.example.ui.farmer;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.example.adapter.ProductCardAdapter;
import com.example.model.SellerProduct;
import com.example.viewmodel.CartViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Farmer-side Farming Products Marketplace Fragment (Java + XML).
 * Provides category filtering, crop-based recommendations, search, product details, and cart integration.
 */
public class FarmingProductsFragment extends Fragment implements ProductCardAdapter.OnProductActionListener {

    private CartViewModel cartViewModel;
    private ProductCardAdapter adapter;

    private MaterialToolbar toolbarMarketplace;
    private FrameLayout btnOpenCart;
    private TextView tvCartBadge;
    private MaterialButton btnViewMyOrders;
    private TextInputEditText etSearchProducts;
    private ChipGroup chipGroupCrops;
    private ChipGroup chipGroupCategories;
    private TextView tvClearCropFilter;
    private TextView tvProductsCount;
    private LinearLayout layoutEmptyProducts;
    private RecyclerView rvMarketplaceProducts;
    private View layoutFloatingCartBar;
    private TextView tvFloatingCartCount;
    private TextView tvFloatingCartTotal;

    public static FarmingProductsFragment newInstance() {
        return new FarmingProductsFragment();
    }

    public static FarmingProductsFragment newInstance(String cropFilter) {
        FarmingProductsFragment fragment = new FarmingProductsFragment();
        Bundle args = new Bundle();
        args.putString("selected_crop", cropFilter);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_farming_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupListeners();
        observeViewModel();

        // Check if navigated with a specific crop recommendation
        if (getArguments() != null && getArguments().containsKey("selected_crop")) {
            String initialCrop = getArguments().getString("selected_crop");
            if (initialCrop != null && !initialCrop.isEmpty()) {
                selectCropChip(initialCrop);
            }
        }
    }

    private void initViews(View view) {
        toolbarMarketplace = view.findViewById(R.id.toolbarMarketplace);
        btnOpenCart = view.findViewById(R.id.btnOpenCart);
        tvCartBadge = view.findViewById(R.id.tvCartBadge);
        btnViewMyOrders = view.findViewById(R.id.btnViewMyOrders);
        etSearchProducts = view.findViewById(R.id.etSearchProducts);
        chipGroupCrops = view.findViewById(R.id.chipGroupCrops);
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories);
        tvClearCropFilter = view.findViewById(R.id.tvClearCropFilter);
        tvProductsCount = view.findViewById(R.id.tvProductsCount);
        layoutEmptyProducts = view.findViewById(R.id.layoutEmptyProducts);
        rvMarketplaceProducts = view.findViewById(R.id.rvMarketplaceProducts);
        layoutFloatingCartBar = view.findViewById(R.id.layoutFloatingCartBar);
        tvFloatingCartCount = view.findViewById(R.id.tvFloatingCartCount);
        tvFloatingCartTotal = view.findViewById(R.id.tvFloatingCartTotal);
    }

    private void setupRecyclerView() {
        adapter = new ProductCardAdapter(false, this);
        rvMarketplaceProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMarketplaceProducts.setAdapter(adapter);
    }

    private void setupListeners() {
        toolbarMarketplace.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnOpenCart.setOnClickListener(v -> navigateToCart());
        layoutFloatingCartBar.setOnClickListener(v -> navigateToCart());

        btnViewMyOrders.setOnClickListener(v -> {
            if (getActivity() instanceof FarmerContainerActivity) {
                ((FarmerContainerActivity) getActivity()).navigateToDestination(FarmerContainerActivity.DEST_FARMER_ORDERS);
            }
        });

        etSearchProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cartViewModel.searchProducts(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Crop Recommendation Chips
        chipGroupCrops.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                cartViewModel.filterByCrop("All");
                tvClearCropFilter.setVisibility(View.GONE);
                return;
            }
            int id = checkedIds.get(0);
            if (id == R.id.chipCropAll) {
                cartViewModel.filterByCrop("All");
                tvClearCropFilter.setVisibility(View.GONE);
            } else if (id == R.id.chipCropTomato) {
                cartViewModel.filterByCrop("Tomato");
                tvClearCropFilter.setVisibility(View.VISIBLE);
            } else if (id == R.id.chipCropOnion) {
                cartViewModel.filterByCrop("Onion");
                tvClearCropFilter.setVisibility(View.VISIBLE);
            } else if (id == R.id.chipCropWheat) {
                cartViewModel.filterByCrop("Wheat");
                tvClearCropFilter.setVisibility(View.VISIBLE);
            } else if (id == R.id.chipCropRice) {
                cartViewModel.filterByCrop("Rice");
                tvClearCropFilter.setVisibility(View.VISIBLE);
            } else if (id == R.id.chipCropCotton) {
                cartViewModel.filterByCrop("Cotton");
                tvClearCropFilter.setVisibility(View.VISIBLE);
            }
        });

        tvClearCropFilter.setOnClickListener(v -> {
            chipGroupCrops.check(R.id.chipCropAll);
            cartViewModel.filterByCrop("All");
            tvClearCropFilter.setVisibility(View.GONE);
        });

        // Category Chips
        chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                cartViewModel.filterByCategory("All");
                return;
            }
            int id = checkedIds.get(0);
            if (id == R.id.chipCatAll) {
                cartViewModel.filterByCategory("All");
            } else if (id == R.id.chipCatSeeds) {
                cartViewModel.filterByCategory("Seeds");
            } else if (id == R.id.chipCatFertilizers) {
                cartViewModel.filterByCategory("Fertilizers");
            } else if (id == R.id.chipCatCropProtection) {
                cartViewModel.filterByCategory("Crop Protection");
            } else if (id == R.id.chipCatFarmEquipment) {
                cartViewModel.filterByCategory("Farm Equipment");
            } else if (id == R.id.chipCatTools) {
                cartViewModel.filterByCategory("Tools");
            } else if (id == R.id.chipCatIrrigation) {
                cartViewModel.filterByCategory("Irrigation Equipment");
            } else if (id == R.id.chipCatSupplies) {
                cartViewModel.filterByCategory("Other Agricultural Supplies");
            }
        });
    }

    private void selectCropChip(String cropName) {
        if (cropName == null) return;
        String lower = cropName.toLowerCase();
        if (lower.contains("tomato")) {
            chipGroupCrops.check(R.id.chipCropTomato);
        } else if (lower.contains("onion")) {
            chipGroupCrops.check(R.id.chipCropOnion);
        } else if (lower.contains("wheat")) {
            chipGroupCrops.check(R.id.chipCropWheat);
        } else if (lower.contains("rice")) {
            chipGroupCrops.check(R.id.chipCropRice);
        } else if (lower.contains("cotton")) {
            chipGroupCrops.check(R.id.chipCropCotton);
        }
    }

    private void observeViewModel() {
        cartViewModel.getFilteredProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            adapter.setProducts(products);
            if (products == null || products.isEmpty()) {
                layoutEmptyProducts.setVisibility(View.VISIBLE);
                rvMarketplaceProducts.setVisibility(View.GONE);
                tvProductsCount.setText("No products found matching filters");
            } else {
                layoutEmptyProducts.setVisibility(View.GONE);
                rvMarketplaceProducts.setVisibility(View.VISIBLE);
                tvProductsCount.setText("Showing " + products.size() + " verified farming products");
            }
        });

        cartViewModel.getCartCountLiveData().observe(getViewLifecycleOwner(), count -> {
            if (count != null && count > 0) {
                tvCartBadge.setVisibility(View.VISIBLE);
                tvCartBadge.setText(String.valueOf(count));
                layoutFloatingCartBar.setVisibility(View.VISIBLE);
                tvFloatingCartCount.setText(count + (count == 1 ? " Item in Cart" : " Items in Cart"));
            } else {
                tvCartBadge.setVisibility(View.GONE);
                layoutFloatingCartBar.setVisibility(View.GONE);
            }
        });

        cartViewModel.getCartTotalLiveData().observe(getViewLifecycleOwner(), total -> {
            if (total != null && total > 0) {
                tvFloatingCartTotal.setText(String.format("₹%.0f", total));
            }
        });

        cartViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToCart() {
        if (getActivity() instanceof FarmerContainerActivity) {
            ((FarmerContainerActivity) getActivity()).navigateToDestination(FarmerContainerActivity.DEST_FARMING_CART);
        }
    }

    @Override
    public void onViewDetails(SellerProduct product) {
        showProductDetailsDialog(product);
    }

    @Override
    public void onAddToCart(SellerProduct product) {
        if (product.isOutOfStock()) {
            Toast.makeText(requireContext(), "Product is currently Out of Stock", Toast.LENGTH_SHORT).show();
            return;
        }
        cartViewModel.addToCart(product, 1);
    }

    @Override
    public void onSellerEdit(SellerProduct product) {}

    @Override
    public void onSellerUpdateStock(SellerProduct product) {}

    private void showProductDetailsDialog(SellerProduct product) {
        Dialog dialog = new Dialog(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_Dialog);
        dialog.setContentView(R.layout.dialog_product_details);

        TextView tvDetailEmoji = dialog.findViewById(R.id.tvDetailEmoji);
        TextView tvDetailCategory = dialog.findViewById(R.id.tvDetailCategory);
        TextView tvDetailName = dialog.findViewById(R.id.tvDetailName);
        TextView tvDetailPrice = dialog.findViewById(R.id.tvDetailPrice);
        TextView tvDetailStock = dialog.findViewById(R.id.tvDetailStock);
        TextView tvDetailDescription = dialog.findViewById(R.id.tvDetailDescription);
        TextView tvDetailSuitableCrops = dialog.findViewById(R.id.tvDetailSuitableCrops);
        TextView tvDetailAdvisory = dialog.findViewById(R.id.tvDetailAdvisory);
        TextView tvDetailSellerName = dialog.findViewById(R.id.tvDetailSellerName);
        TextView tvDetailSellerLocation = dialog.findViewById(R.id.tvDetailSellerLocation);

        MaterialButton btnDetailMinus = dialog.findViewById(R.id.btnDetailMinus);
        TextView tvDetailSelectedQty = dialog.findViewById(R.id.tvDetailSelectedQty);
        MaterialButton btnDetailPlus = dialog.findViewById(R.id.btnDetailPlus);

        MaterialButton btnDetailAddToCart = dialog.findViewById(R.id.btnDetailAddToCart);
        MaterialButton btnDetailBuyNow = dialog.findViewById(R.id.btnDetailBuyNow);

        tvDetailEmoji.setText(product.getImageEmoji() != null ? product.getImageEmoji() : "🌱");
        tvDetailCategory.setText(product.getCategory() + " • " + product.getBrand());
        tvDetailName.setText(product.getName());
        tvDetailPrice.setText(String.format("₹%.0f / %s", product.getPrice(), product.getUnit()));
        tvDetailStock.setText(product.getStock() + " " + product.getUnit() + (product.isOutOfStock() ? " (Out of Stock)" : " Available"));
        tvDetailDescription.setText(product.getDescription());
        tvDetailSuitableCrops.setText("🌱 Suitable Crops: " + product.getSuitableCrops());
        tvDetailAdvisory.setText("Advisory: " + product.getUsageAdvisory());

        tvDetailSellerName.setText("🏪 " + product.getSellerName());
        tvDetailSellerLocation.setText("📍 " + product.getLocation() + " • ⭐ " + product.getRating() + " Rating");

        final int[] selectedQty = {1};
        final int maxStock = product.getStock();

        tvDetailSelectedQty.setText(selectedQty[0] + " " + product.getUnit());

        btnDetailMinus.setOnClickListener(v -> {
            if (selectedQty[0] > 1) {
                selectedQty[0]--;
                tvDetailSelectedQty.setText(selectedQty[0] + " " + product.getUnit());
            }
        });

        btnDetailPlus.setOnClickListener(v -> {
            if (selectedQty[0] < maxStock) {
                selectedQty[0]++;
                tvDetailSelectedQty.setText(selectedQty[0] + " " + product.getUnit());
            } else {
                Toast.makeText(requireContext(), "Reached maximum available stock (" + maxStock + ")", Toast.LENGTH_SHORT).show();
            }
        });

        if (product.isOutOfStock()) {
            btnDetailAddToCart.setEnabled(false);
            btnDetailBuyNow.setEnabled(false);
            btnDetailBuyNow.setText("OUT OF STOCK");
        }

        btnDetailAddToCart.setOnClickListener(v -> {
            cartViewModel.addToCart(product, selectedQty[0]);
            dialog.dismiss();
        });

        btnDetailBuyNow.setOnClickListener(v -> {
            dialog.dismiss();
            showDirectCheckoutDialog(product, selectedQty[0]);
        });

        dialog.show();
    }

    private void showDirectCheckoutDialog(SellerProduct product, int quantity) {
        Dialog checkoutDialog = new Dialog(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_Dialog);
        checkoutDialog.setContentView(R.layout.dialog_checkout);

        TextView tvCheckoutItemsSummary = checkoutDialog.findViewById(R.id.tvCheckoutItemsSummary);
        TextView tvCheckoutItemsDetails = checkoutDialog.findViewById(R.id.tvCheckoutItemsDetails);
        TextInputEditText etCheckoutAddress = checkoutDialog.findViewById(R.id.etCheckoutAddress);
        TextInputEditText etCheckoutVillage = checkoutDialog.findViewById(R.id.etCheckoutVillage);
        TextInputEditText etCheckoutTaluka = checkoutDialog.findViewById(R.id.etCheckoutTaluka);
        TextInputEditText etCheckoutDistrict = checkoutDialog.findViewById(R.id.etCheckoutDistrict);
        TextInputEditText etCheckoutNotes = checkoutDialog.findViewById(R.id.etCheckoutNotes);
        RadioGroup rgPaymentMethods = checkoutDialog.findViewById(R.id.rgPaymentMethods);
        MaterialButton btnConfirmPlaceOrder = checkoutDialog.findViewById(R.id.btnConfirmPlaceOrder);

        double total = product.getPrice() * quantity;
        tvCheckoutItemsSummary.setText(String.format("1 Item • Total Payable: ₹%.0f", total));
        tvCheckoutItemsDetails.setText(String.format("• %s (%d %s) - ₹%.0f", product.getName(), quantity, product.getUnit(), total));

        btnConfirmPlaceOrder.setOnClickListener(v -> {
            String address = etCheckoutAddress.getText() != null ? etCheckoutAddress.getText().toString().trim() : "";
            String village = etCheckoutVillage.getText() != null ? etCheckoutVillage.getText().toString().trim() : "";
            String taluka = etCheckoutTaluka.getText() != null ? etCheckoutTaluka.getText().toString().trim() : "";
            String district = etCheckoutDistrict.getText() != null ? etCheckoutDistrict.getText().toString().trim() : "";
            String notes = etCheckoutNotes.getText() != null ? etCheckoutNotes.getText().toString().trim() : "";

            if (address.isEmpty() || village.isEmpty() || taluka.isEmpty() || district.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in full delivery address details", Toast.LENGTH_SHORT).show();
                return;
            }

            String paymentMethod = "Cash on Delivery";
            int checkedId = rgPaymentMethods.getCheckedRadioButtonId();
            if (checkedId == R.id.rbUpi) {
                paymentMethod = "UPI Kisan Pay";
            } else if (checkedId == R.id.rbKisanCredit) {
                paymentMethod = "Kisan Credit / Net Banking";
            }

            cartViewModel.placeDirectBuyNow(product, quantity, address, village, taluka, district, paymentMethod, notes);
            checkoutDialog.dismiss();

            // Navigate to My Orders
            if (getActivity() instanceof FarmerContainerActivity) {
                ((FarmerContainerActivity) getActivity()).navigateToDestination(FarmerContainerActivity.DEST_FARMER_ORDERS);
            }
        });

        checkoutDialog.show();
    }
}
