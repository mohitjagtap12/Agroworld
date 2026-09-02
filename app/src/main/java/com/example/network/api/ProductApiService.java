package com.example.network.api;

import com.example.model.ProductOrder;
import com.example.model.SellerProduct;
import com.example.network.dto.NetworkResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface ProductApiService {

    @GET("api/products")
    Call<NetworkResponse<List<SellerProduct>>> getProducts(@Query("category") String category);

    @POST("api/products/orders")
    Call<NetworkResponse<ProductOrder>> placeOrder(@Body Map<String, Object> orderPayload);

    @GET("api/products/orders/farmer")
    Call<NetworkResponse<List<ProductOrder>>> getFarmerOrders();
}
