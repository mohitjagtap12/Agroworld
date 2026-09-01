package com.example.network.api;

import com.example.model.DeliveryJob;
import com.example.network.dto.NetworkResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface DeliveryApiService {

    @GET("api/delivery/available")
    Call<NetworkResponse<List<DeliveryJob>>> getAvailableJobs();

    @GET("api/delivery/my-jobs")
    Call<NetworkResponse<List<DeliveryJob>>> getMyJobs();

    @POST("api/delivery/jobs/{jobId}/accept")
    Call<NetworkResponse<DeliveryJob>> acceptJob(@Path("jobId") String jobId);

    @PATCH("api/delivery/jobs/{jobId}/status")
    Call<NetworkResponse<DeliveryJob>> updateJobStatus(
            @Path("jobId") String jobId,
            @Body Map<String, String> statusBody
    );
}
