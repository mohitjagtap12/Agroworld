package com.example.network.api;

import com.example.network.dto.NetworkResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

public interface AuthApiService {

    @POST("api/auth/login")
    Call<NetworkResponse<Map<String, Object>>> login(@Body Map<String, String> credentials);

    @POST("api/auth/register")
    Call<NetworkResponse<Map<String, Object>>> register(@Body Map<String, String> userData);
}
