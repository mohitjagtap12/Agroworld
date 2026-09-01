package com.example.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.model.ProductOrder;
import com.example.model.SellerProduct;
import com.example.model.SellerProfile;
import com.example.seller.SellerDataHub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for the Seller Store & Inventory Management module.
 */
public class SellerViewModel extends AndroidViewModel {

    private final SellerDataHub sellerDataHub;
    private final ExecutorService executorService;

    private final MutableLiveData<List<SellerProduct>> productsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ProductOrder>> ordersLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ProductOrder>> salesHistoryLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Map<String, Object>> metricsLiveData = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<SellerProfile> profileLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    private String currentSellerId = SellerDataHub.DEFAULT_SELLER_ID;

    public SellerViewModel(@NonNull Application application) {
        super(application);
        this.sellerDataHub = SellerDataHub.getInstance();
        this.executorService = Executors.newFixedThreadPool(2);
        refreshAllData();
    }

    public void setCurrentSellerId(String sellerId) {
        this.currentSellerId = sellerId;
        refreshAllData();
    }

    public String getCurrentSellerId() {
        return currentSellerId;
    }

    public void refreshAllData() {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            List<SellerProduct> products = sellerDataHub.getProductsForSeller(currentSellerId);
            List<ProductOrder> allOrders = sellerDataHub.getOrdersForSeller(currentSellerId);

            int totalProducts = products.size();
            int activeProducts = 0;
            for (SellerProduct p : products) {
                if (!p.isOutOfStock()) activeProducts++;
            }

            int pendingOrders = 0;
            int completedOrders = 0;
            double totalRevenue = 0.0;
            List<ProductOrder> salesHistory = new ArrayList<>();

            for (ProductOrder o : allOrders) {
                if (ProductOrder.STATUS_ORDER_PLACED.equalsIgnoreCase(o.getStatus()) ||
                        ProductOrder.STATUS_CONFIRMED.equalsIgnoreCase(o.getStatus()) ||
                        ProductOrder.STATUS_PACKED.equalsIgnoreCase(o.getStatus()) ||
                        ProductOrder.STATUS_OUT_FOR_DELIVERY.equalsIgnoreCase(o.getStatus())) {
                    pendingOrders++;
                } else if (ProductOrder.STATUS_DELIVERED.equalsIgnoreCase(o.getStatus()) ||
                        ProductOrder.STATUS_COMPLETED.equalsIgnoreCase(o.getStatus())) {
                    completedOrders++;
                    totalRevenue += o.getTotalAmount();
                    salesHistory.add(o);
                }
            }

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("totalProducts", totalProducts);
            metrics.put("activeProducts", activeProducts);
            metrics.put("pendingOrders", pendingOrders);
            metrics.put("completedOrders", completedOrders);
            metrics.put("totalRevenue", totalRevenue);

            productsLiveData.postValue(products);
            ordersLiveData.postValue(allOrders);
            salesHistoryLiveData.postValue(salesHistory);
            metricsLiveData.postValue(metrics);
            profileLiveData.postValue(sellerDataHub.getSellerProfile());
            isLoadingLiveData.postValue(false);
        });
    }

    public LiveData<List<SellerProduct>> getProductsLiveData() {
        return productsLiveData;
    }

    public LiveData<List<ProductOrder>> getOrdersLiveData() {
        return ordersLiveData;
    }

    public LiveData<List<ProductOrder>> getSalesHistoryLiveData() {
        return salesHistoryLiveData;
    }

    public LiveData<Map<String, Object>> getMetricsLiveData() {
        return metricsLiveData;
    }

    public LiveData<SellerProfile> getProfileLiveData() {
        return profileLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public void addProduct(SellerProduct product) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            product.setSellerId(currentSellerId);
            sellerDataHub.addProduct(product);
            messageLiveData.postValue("Product added to catalog successfully!");
            refreshAllData();
        });
    }

    public void updateProduct(SellerProduct product) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean success = sellerDataHub.updateProduct(product, currentSellerId);
            if (success) {
                messageLiveData.postValue("Product updated successfully!");
            } else {
                messageLiveData.postValue("Error: You can only edit your own products.");
            }
            refreshAllData();
        });
    }

    public void deleteProduct(String productId) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean success = sellerDataHub.deleteProduct(productId, currentSellerId);
            if (success) {
                messageLiveData.postValue("Product removed from catalog.");
            } else {
                messageLiveData.postValue("Error: Could not delete product.");
            }
            refreshAllData();
        });
    }

    public void updateStockAndPrice(String productId, int newStock, double newPrice) {
        executorService.execute(() -> {
            boolean success = sellerDataHub.updateStockAndPrice(productId, newStock, newPrice, currentSellerId);
            if (success) {
                messageLiveData.postValue("Stock & price updated successfully!");
                refreshAllData();
            } else {
                messageLiveData.postValue("Failed to update stock.");
            }
        });
    }

    public void acceptOrder(String orderId) {
        updateStatus(orderId, ProductOrder.STATUS_CONFIRMED, "Order confirmed! Proceed to packing.");
    }

    public void rejectOrder(String orderId) {
        updateStatus(orderId, ProductOrder.STATUS_REJECTED, "Order rejected and inventory stock restored.");
    }

    public void markPacked(String orderId) {
        updateStatus(orderId, ProductOrder.STATUS_PACKED, "Order marked as Packed.");
    }

    public void markReadyForDelivery(String orderId) {
        updateStatus(orderId, ProductOrder.STATUS_OUT_FOR_DELIVERY, "Handed over to AgroWorld Delivery Partner.");
    }

    public void markDelivered(String orderId) {
        updateStatus(orderId, ProductOrder.STATUS_DELIVERED, "Order delivered and marked completed.");
    }

    private void updateStatus(String orderId, String newStatus, String successMsg) {
        executorService.execute(() -> {
            boolean success = sellerDataHub.updateOrderStatus(orderId, newStatus, currentSellerId);
            if (success) {
                messageLiveData.postValue(successMsg);
                refreshAllData();
            } else {
                messageLiveData.postValue("Failed to update order status.");
            }
        });
    }
}
