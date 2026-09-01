package com.example.network.api;

import com.example.model.CropModel;
import com.example.network.dto.NetworkResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface FarmerApiService {

    @GET("api/farmer/crops")
    Call<NetworkResponse<List<CropModel>>> getCrops();

    @POST("api/farmer/crops")
    Call<NetworkResponse<CropModel>> addCrop(@Body CropModel crop);

    @PUT("api/farmer/crops/{cropId}")
    Call<NetworkResponse<CropModel>> updateCrop(@Path("cropId") String cropId, @Body CropModel crop);

    @DELETE("api/farmer/crops/{cropId}")
    Call<NetworkResponse<String>> deleteCrop(@Path("cropId") String cropId);
}
