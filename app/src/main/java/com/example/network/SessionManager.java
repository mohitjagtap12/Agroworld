package com.example.network;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages JWT Auth Token, User Profiles, and active sessions in persistent storage.
 */
public class SessionManager {

    private static final String PREF_NAME = "agroworld_session";
    private static final String KEY_JWT_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_VILLAGE = "user_village";
    private static final String KEY_USER_DISTRICT = "user_district";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_BASE_URL = "base_url";

    public static final String DEFAULT_BASE_URL = "https://api.agroworld.in/";

    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void saveAuthSession(String token, String userId, String name, String phone, String role, String village, String district) {
        prefs.edit()
                .putString(KEY_JWT_TOKEN, token)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_PHONE, phone)
                .putString(KEY_USER_ROLE, role)
                .putString(KEY_USER_VILLAGE, village)
                .putString(KEY_USER_DISTRICT, district)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public String getAuthToken() {
        return prefs.getString(KEY_JWT_TOKEN, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    public String getUserPhone() {
        return prefs.getString(KEY_USER_PHONE, "");
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, "");
    }

    public String getUserVillage() {
        return prefs.getString(KEY_USER_VILLAGE, "");
    }

    public String getUserTaluka() {
        return prefs.getString("user_taluka", "");
    }

    public String getUserDistrict() {
        return prefs.getString(KEY_USER_DISTRICT, "");
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getBaseUrl() {
        return prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL);
    }

    public void setBaseUrl(String url) {
        prefs.edit().putString(KEY_BASE_URL, url).apply();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
