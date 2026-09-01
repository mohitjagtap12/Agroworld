package com.example.ui.farmer;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.example.adapter.CartAdapter;
import com.example.model.CartItem;
import com.example.viewmodel.CartViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

/**
 * Farmer Shopping Cart Fragment (Java + XML).
 * Handles cart viewing, item quantity adjustment, item removal, price calculation, and order checkout.
 */
public class CartFragment extends Fragment implements CartAdapter.OnCartItemInteractionListener {

    private CartViewModel cartViewModel;
    private CartAdapter adapter;

    private MaterialToolbar toolbarCart;
    private TextView tvClearCartBtn;
    private LinearLayout layoutEmptyCart;
    private MaterialButton btnEmptyCartShopNow;
    private LinearLayout layoutCartContent;
    private RecyclerView rvCartItems;
    private TextView tvCartSummarySubtotal;
    private TextView tvCartSummaryTotal;
    private MaterialButton btnProceedCheckout;

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupListeners();
        observeViewModel();
    }

    private void initViews(View view) {
        toolbarCart = view.findViewById(R.id.toolbarCart);
        tvClearCartBtn = view.findViewById(R.id.tvClearCartBtn);
        layoutEmptyCart = view.findViewById(R.id.layoutEmptyCart);
        btnEmptyCartShopNow = view.findViewById(R.id.btnEmptyCartShopNow);
        layoutCartContent = view.findViewById(R.id.layoutCartContent);
        rvCartItems = view.findViewById(R.id.rvCartItems);
        tvCartSummarySubtotal = view.findViewById(R.id.tvCartSummarySubtotal);
        tvCartSummaryTotal = view.findViewById(R.id.tvCartSummaryTotal);
        btnProceedCheckout = view.findViewById(R.id.btnProceedCheckout);
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCartItems.setAdapter(adapter);
    }

    private void setupListeners() {
        toolbarCart.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        tvClearCartBtn.setOnClickListener(v -> {
            cartViewModel.clearCart();
            Toast.makeText(requireContext(), "Cart cleared", Toast.LENGTH_SHORT).show();
        });

        btnEmptyCartShopNow.setOnClickListener(v -> {
            if (getActivity() instanceof FarmerContainerActivity) {
                ((FarmerContainerActivity) getActivity()).navigateToDestination(FarmerContainerActivity.DEST_FARMING_PRODUCTS);
            }
        });

        btnProceedCheckout.setOnClickListener(v -> showCheckoutDialog());
    }

    private void observeViewModel() {
        cartViewModel.getCartItemsLiveData().observe(getViewLifecycleOwner(), items -> {
            adapter.setCartItems(items);
            if (items == null || items.isEmpty()) {
                layoutEmptyCart.setVisibility(View.VISIBLE);
                layoutCartContent.setVisibility(View.GONE);
                tvClearCartBtn.setVisibility(View.GONE);
            } else {
                layoutEmptyCart.setVisibility(View.GONE);
                layoutCartContent.setVisibility(View.VISIBLE);
                tvClearCartBtn.setVisibility(View.VISIBLE);
            }
        });

        cartViewModel.getCartTotalLiveData().observe(getViewLifecycleOwner(), total -> {
            double amount = (total != null) ? total : 0.0;
            tvCartSummarySubtotal.setText(String.format("₹%.0f", amount));
            tvCartSummaryTotal.setText(String.format("₹%.0f", amount));
        });

        cartViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onQuantityChanged(String productId, int newQuantity) {
        cartViewModel.updateCartItemQuantity(productId, newQuantity);
    }

    @Override
    public void onRemoveItem(String productId) {
        cartViewModel.removeFromCart(productId);
    }

    private void showCheckoutDialog() {
        List<CartItem> items = cartViewModel.getCartItemsLiveData().getValue();
        if (items == null || items.isEmpty()) {
            Toast.makeText(requireContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

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

        double total = cartViewModel.getCartTotalLiveData().getValue() != null ? cartViewModel.getCartTotalLiveData().getValue() : 0.0;
        tvCheckoutItemsSummary.setText(String.format("%d Items • Total Payable: ₹%.0f", items.size(), total));

        StringBuilder detailsBuilder = new StringBuilder();
        for (CartItem item : items) {
            if (item.getProduct() != null) {
                detailsBuilder.append(String.format("• %s (%d %s) - ₹%.0f\n",
                        item.getProduct().getName(), item.getQuantity(), item.getProduct().getUnit(), item.getSubtotal()));
            }
        }
        tvCheckoutItemsDetails.setText(detailsBuilder.toString().trim());

        btnConfirmPlaceOrder.setOnClickListener(v -> {
            String address = etCheckoutAddress.getText() != null ? etCheckoutAddress.getText().toString().trim() : "";
            String village = etCheckoutVillage.getText() != null ? etCheckoutVillage.getText().toString().trim() : "";
            String taluka = etCheckoutTaluka.getText() != null ? etCheckoutTaluka.getText().toString().trim() : "";
            String district = etCheckoutDistrict.getText() != null ? etCheckoutDistrict.getText().toString().trim() : "";
            String notes = etCheckoutNotes.getText() != null ? etCheckoutNotes.getText().toString().trim() : "";

            if (address.isEmpty() || village.isEmpty() || taluka.isEmpty() || district.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in delivery address details", Toast.LENGTH_SHORT).show();
                return;
            }

            String paymentMethod = "Cash on Delivery";
            int checkedId = rgPaymentMethods.getCheckedRadioButtonId();
            if (checkedId == R.id.rbUpi) {
                paymentMethod = "UPI Kisan Pay";
            } else if (checkedId == R.id.rbKisanCredit) {
                paymentMethod = "Kisan Credit / Net Banking";
            }

            cartViewModel.placeCartOrder(address, village, taluka, district, paymentMethod, notes);
            checkoutDialog.dismiss();

            // Navigate to My Orders
            if (getActivity() instanceof FarmerContainerActivity) {
                ((FarmerContainerActivity) getActivity()).navigateToDestination(FarmerContainerActivity.DEST_FARMER_ORDERS);
            }
        });

        checkoutDialog.show();
    }
}
