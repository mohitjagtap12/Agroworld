package com.example.network.api;

import com.example.model.FarmerCrop;
import com.example.network.dto.NetworkResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface FarmerApiService {

    @GET("api/farmer/crops")
    Call<NetworkResponse<List<FarmerCrop>>> getCrops();

    @POST("api/farmer/crops")
    Call<NetworkResponse<FarmerCrop>> addCrop(@Body FarmerCrop crop);

    @PUT("api/farmer/crops/{cropId}")
    Call<NetworkResponse<FarmerCrop>> updateCrop(@Path("cropId") String cropId, @Body FarmerCrop crop);

    @DELETE("api/farmer/crops/{cropId}")
    Call<NetworkResponse<String>> deleteCrop(@Path("cropId") String cropId);
}
