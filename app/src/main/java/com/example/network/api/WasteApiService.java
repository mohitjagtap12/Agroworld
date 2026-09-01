package com.example.network.api;

import com.example.model.AgriWasteItem;
import com.example.network.dto.NetworkResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface WasteApiService {

    @GET("api/waste/farmer")
    Call<NetworkResponse<List<AgriWasteItem>>> getFarmerListings();

    @GET("api/waste/marketplace")
    Call<NetworkResponse<List<AgriWasteItem>>> getMarketplaceListings();

    @POST("api/waste/farmer")
    Call<NetworkResponse<AgriWasteItem>> createListing(@Body AgriWasteItem item);

    @POST("api/waste/orders")
    Call<NetworkResponse<Map<String, Object>>> placeWasteOrder(@Body Map<String, Object> orderPayload);
}
