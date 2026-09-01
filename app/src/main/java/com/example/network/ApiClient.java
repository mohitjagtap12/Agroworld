package com.example.network;

import android.content.Context;

import com.example.network.api.AuthApiService;
import com.example.network.api.DeliveryApiService;
import com.example.network.api.FarmerApiService;
import com.example.network.api.ProductApiService;
import com.example.network.api.WasteApiService;
import com.example.network.interceptor.AuthInterceptor;
import com.example.network.interceptor.MockBackendInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static ApiClient instance;
    private final Retrofit retrofit;

    private final AuthApiService authApiService;
    private final FarmerApiService farmerApiService;
    private final DeliveryApiService deliveryApiService;
    private final ProductApiService productApiService;
    private final WasteApiService wasteApiService;

    private ApiClient(Context context) {
        String baseUrl = SessionManager.getInstance(context).getBaseUrl();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(context))
                .addInterceptor(new MockBackendInterceptor(context))
                .build();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.authApiService = retrofit.create(AuthApiService.class);
        this.farmerApiService = retrofit.create(FarmerApiService.class);
        this.deliveryApiService = retrofit.create(DeliveryApiService.class);
        this.productApiService = retrofit.create(ProductApiService.class);
        this.wasteApiService = retrofit.create(WasteApiService.class);
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context.getApplicationContext());
        }
        return instance;
    }

    public AuthApiService getAuthApiService() { return authApiService; }
    public FarmerApiService getFarmerApiService() { return farmerApiService; }
    public DeliveryApiService getDeliveryApiService() { return deliveryApiService; }
    public ProductApiService getProductApiService() { return productApiService; }
    public WasteApiService getWasteApiService() { return wasteApiService; }
}
