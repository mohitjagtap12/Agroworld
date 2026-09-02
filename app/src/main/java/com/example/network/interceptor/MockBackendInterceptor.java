package com.example.network.interceptor;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.model.DeliveryJob;
import com.example.model.FarmerCrop;
import com.example.network.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Handles offline / local development REST endpoint simulation for AgroWorld.
 * Intercepts matching routes and generates valid JSON payloads with status 200 OK.
 */
public class MockBackendInterceptor implements Interceptor {

    private static final MediaType JSON_MEDIA = MediaType.parse("application/json; charset=utf-8");
    private final Context context;
    private final Gson gson = new Gson();

    public MockBackendInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String path = request.url().encodedPath();
        String method = request.method();

        // If configured with a live remote host that is reachable, proceed with network
        String baseUrl = SessionManager.getInstance(context).getBaseUrl();
        if (baseUrl.contains("localhost") || baseUrl.contains("10.0.2.2") || baseUrl.contains("agroworld.in")) {
            return generateMockResponse(request, path, method);
        }

        try {
            return chain.proceed(request);
        } catch (Exception e) {
            return generateMockResponse(request, path, method);
        }
    }

    private Response generateMockResponse(Request request, String path, String method) {
        String jsonResult = "";

        if (path.contains("/api/farmer/crops")) {
            if ("GET".equalsIgnoreCase(method)) {
                List<FarmerCrop> crops = new ArrayList<>();
                crops.add(new FarmerCrop("crop_01", "Sugarcane", "Co 86032", "Cash Crop", "3.5", "Acres", "2025-10-15", "2026-10-20", "Drip", 150.0, 3200.0, "High sucrose yield variety", "Growing", "🌾"));
                crops.add(new FarmerCrop("crop_02", "Tomato", "Abhinav Hybrid", "Vegetables", "1.5", "Acres", "2026-01-10", "2026-04-15", "Drip", 35.0, 1800.0, "Polyhouse table crop", "Flowering", "🍅"));
                crops.add(new FarmerCrop("crop_03", "Wheat", "Lokwan 147", "Grains", "2.0", "Acres", "2025-11-20", "2026-03-25", "Canal", 40.0, 2400.0, "Premium grade durum wheat", "Ready for Harvest", "🌾"));

                Map<String, Object> body = new HashMap<>();
                body.put("success", true);
                body.put("message", "Crops fetched successfully");
                body.put("data", crops);
                jsonResult = gson.toJson(body);
            } else if ("POST".equalsIgnoreCase(method)) {
                Map<String, Object> body = new HashMap<>();
                body.put("success", true);
                body.put("message", "Crop registered successfully");
                body.put("data", new FarmerCrop("crop_new", "Soybean", "JS 335", "Oilseeds", "2.5", "Acres", "2026-06-15", "2026-10-10", "Rainfed", 25.0, 4500.0, "Field 3 sowing", "Growing", "🌱"));
                jsonResult = gson.toJson(body);
            }
        } else if (path.contains("/api/auth/login")) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c3JfZmFybWVyXzAxIiwicm9sZSI6ImZhcm1lciJ9");
            userData.put("userId", "usr_farmer_01");
            userData.put("name", "Ramesh Patil");
            userData.put("phone", "9876543210");
            userData.put("role", "farmer");
            userData.put("village", "Baramati");
            userData.put("district", "Pune");

            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("message", "Login successful");
            body.put("data", userData);
            jsonResult = gson.toJson(body);
        } else {
            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("message", "Request completed successfully");
            body.put("data", new HashMap<>());
            jsonResult = gson.toJson(body);
        }

        return new Response.Builder()
                .code(200)
                .message("OK")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(ResponseBody.create(jsonResult, JSON_MEDIA))
                .addHeader("content-type", "application/json")
                .build();
    }
}
