package com.example.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.model.CartItem;
import com.example.model.ProductOrder;
import com.example.model.SellerProduct;
import com.example.repository.FarmerRepository;
import com.example.seller.SellerDataHub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for Farmer-side Farming-Product shopping, cart management, crop recommendations, and order checkout.
 */
public class CartViewModel extends AndroidViewModel {

    private final SellerDataHub sellerDataHub;
    private final FarmerRepository farmerRepository;
    private final ExecutorService executorService;

    private final MutableLiveData<List<SellerProduct>> productsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<SellerProduct>> filteredProductsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<CartItem>> cartItemsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> cartTotalLiveData = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> cartCountLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<List<ProductOrder>> farmerOrdersLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    private String currentCategory = "All";
    private String currentSearchQuery = "";
    private String currentCropFilter = "All";

    public CartViewModel(@NonNull Application application) {
        super(application);
        this.sellerDataHub = SellerDataHub.getInstance();
        this.farmerRepository = FarmerRepository.getInstance();
        this.executorService = Executors.newFixedThreadPool(2);

        refreshCatalog();
        refreshCart();
        refreshOrders();
    }

    public void refreshCatalog() {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            List<SellerProduct> all = sellerDataHub.getAllProducts();
            productsLiveData.postValue(all);
            applyFiltersInternal(all);
            isLoadingLiveData.postValue(false);
        });
    }

    public void refreshCart() {
        executorService.execute(() -> {
            List<CartItem> items = sellerDataHub.getCartItems();
            double total = sellerDataHub.getCartTotal();
            int count = sellerDataHub.getCartItemCount();

            cartItemsLiveData.postValue(items);
            cartTotalLiveData.postValue(total);
            cartCountLiveData.postValue(count);
        });
    }

    public void refreshOrders() {
        executorService.execute(() -> {
            String farmerId = farmerRepository.getFarmerId();
            List<ProductOrder> orders = sellerDataHub.getOrdersForFarmer(farmerId);
            farmerOrdersLiveData.postValue(orders);
        });
    }

    public LiveData<List<SellerProduct>> getProductsLiveData() {
        return productsLiveData;
    }

    public LiveData<List<SellerProduct>> getFilteredProductsLiveData() {
        return filteredProductsLiveData;
    }

    public LiveData<List<CartItem>> getCartItemsLiveData() {
        return cartItemsLiveData;
    }

    public LiveData<Double> getCartTotalLiveData() {
        return cartTotalLiveData;
    }

    public LiveData<Integer> getCartCountLiveData() {
        return cartCountLiveData;
    }

    public LiveData<List<ProductOrder>> getFarmerOrdersLiveData() {
        return farmerOrdersLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public void filterByCategory(String category) {
        this.currentCategory = category != null ? category : "All";
        applyFilters();
    }

    public void searchProducts(String query) {
        this.currentSearchQuery = query != null ? query.trim().toLowerCase() : "";
        applyFilters();
    }

    public void filterByCrop(String cropName) {
        this.currentCropFilter = cropName != null ? cropName.trim() : "All";
        applyFilters();
    }

    private void applyFilters() {
        List<SellerProduct> all = productsLiveData.getValue();
        if (all == null) all = sellerDataHub.getAllProducts();
        applyFiltersInternal(all);
    }

    private void applyFiltersInternal(List<SellerProduct> source) {
        List<SellerProduct> filtered = new ArrayList<>();

        for (SellerProduct p : source) {
            boolean matchesCategory = "All".equalsIgnoreCase(currentCategory) ||
                    (p.getCategory() != null && p.getCategory().equalsIgnoreCase(currentCategory));

            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    (p.getName() != null && p.getName().toLowerCase().contains(currentSearchQuery)) ||
                    (p.getCategory() != null && p.getCategory().toLowerCase().contains(currentSearchQuery)) ||
                    (p.getBrand() != null && p.getBrand().toLowerCase().contains(currentSearchQuery)) ||
                    (p.getDescription() != null && p.getDescription().toLowerCase().contains(currentSearchQuery));

            boolean matchesCrop = "All".equalsIgnoreCase(currentCropFilter) ||
                    (p.getSuitableCrops() != null &&
                            (p.getSuitableCrops().toLowerCase().contains(currentCropFilter.toLowerCase()) ||
                                    p.getSuitableCrops().toLowerCase().contains("all crops"))) ||
                    (p.getName() != null && p.getName().toLowerCase().contains(currentCropFilter.toLowerCase()));

            if (matchesCategory && matchesSearch && matchesCrop) {
                filtered.add(p);
            }
        }
        filteredProductsLiveData.postValue(filtered);
    }

    public void addToCart(SellerProduct product, int quantity) {
        executorService.execute(() -> {
            if (product.isOutOfStock()) {
                messageLiveData.postValue("Sorry, " + product.getName() + " is currently Out of Stock.");
                return;
            }
            boolean success = sellerDataHub.addToCart(product, quantity);
            if (success) {
                messageLiveData.postValue("Added " + quantity + " " + product.getUnit() + " of " + product.getName() + " to cart!");
                refreshCart();
            } else {
                messageLiveData.postValue("Cannot add to cart. Stock limit reached.");
            }
        });
    }

    public void updateCartItemQuantity(String productId, int quantity) {
        executorService.execute(() -> {
            sellerDataHub.updateCartItemQuantity(productId, quantity);
            refreshCart();
        });
    }

    public void removeFromCart(String productId) {
        executorService.execute(() -> {
            sellerDataHub.removeFromCart(productId);
            messageLiveData.postValue("Item removed from cart.");
            refreshCart();
        });
    }

    public void clearCart() {
        executorService.execute(() -> {
            sellerDataHub.clearCart();
            refreshCart();
        });
    }

    public void placeCartOrder(String address, String village, String taluka, String district, String paymentMethod, String notes) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            String farmerId = farmerRepository.getFarmerId();
            String farmerName = farmerRepository.getFarmerName();
            String farmerPhone = farmerRepository.getFarmerPhone();

            List<ProductOrder> orders = sellerDataHub.placeCartOrder(
                    farmerId, farmerName, farmerPhone, address, village, taluka, district, paymentMethod, notes
            );

            if (!orders.isEmpty()) {
                messageLiveData.postValue("Order placed successfully! Total " + orders.size() + " items ordered.");
            } else {
                messageLiveData.postValue("Your cart is empty or items are out of stock.");
            }

            refreshCatalog();
            refreshCart();
            refreshOrders();
            isLoadingLiveData.postValue(false);
        });
    }

    public void placeDirectBuyNow(SellerProduct product, int quantity, String address, String village, String taluka, String district, String paymentMethod, String notes) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            String farmerId = farmerRepository.getFarmerId();
            String farmerName = farmerRepository.getFarmerName();
            String farmerPhone = farmerRepository.getFarmerPhone();

            ProductOrder order = sellerDataHub.placeDirectOrder(
                    product, quantity, farmerId, farmerName, farmerPhone, address, village, taluka, district, paymentMethod, notes
            );

            if (order != null) {
                messageLiveData.postValue("Order #" + order.getOrderNumber() + " placed successfully!");
            } else {
                messageLiveData.postValue("Failed to place order. Product may be out of stock.");
            }

            refreshCatalog();
            refreshCart();
            refreshOrders();
            isLoadingLiveData.postValue(false);
        });
    }

    public void filterOrdersByStatus(String statusFilter) {
        executorService.execute(() -> {
            String farmerId = farmerRepository.getFarmerId();
            List<ProductOrder> all = sellerDataHub.getOrdersForFarmer(farmerId);
            if (statusFilter == null || "All".equalsIgnoreCase(statusFilter)) {
                farmerOrdersLiveData.postValue(all);
                return;
            }

            List<ProductOrder> filtered = new ArrayList<>();
            for (ProductOrder o : all) {
                if ("Active".equalsIgnoreCase(statusFilter)) {
                    if (ProductOrder.STATUS_CONFIRMED.equalsIgnoreCase(o.getStatus()) ||
                            ProductOrder.STATUS_PACKED.equalsIgnoreCase(o.getStatus()) ||
                            ProductOrder.STATUS_OUT_FOR_DELIVERY.equalsIgnoreCase(o.getStatus())) {
                        filtered.add(o);
                    }
                } else if ("Pending".equalsIgnoreCase(statusFilter)) {
                    if (ProductOrder.STATUS_ORDER_PLACED.equalsIgnoreCase(o.getStatus())) {
                        filtered.add(o);
                    }
                } else if ("Completed".equalsIgnoreCase(statusFilter)) {
                    if (ProductOrder.STATUS_DELIVERED.equalsIgnoreCase(o.getStatus()) ||
                            ProductOrder.STATUS_COMPLETED.equalsIgnoreCase(o.getStatus())) {
                        filtered.add(o);
                    }
                } else if ("Cancelled".equalsIgnoreCase(statusFilter) || "Rejected".equalsIgnoreCase(statusFilter)) {
                    if (ProductOrder.STATUS_REJECTED.equalsIgnoreCase(o.getStatus())) {
                        filtered.add(o);
                    }
                }
            }
            farmerOrdersLiveData.postValue(filtered);
        });
    }
}
