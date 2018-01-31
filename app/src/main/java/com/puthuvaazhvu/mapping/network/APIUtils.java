package com.puthuvaazhvu.mapping.network;

import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class APIUtils {
    public static String BASE_URL = "https://ptracking.org/";

    public static String getAuth(SharedPreferences sharedPreferences) {
        return "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZGV2IiwiaWF0IjoxNTAzNzc2NjkxfQ.p6kwJVeJN4h_3QoAjRg998cQHaiy2m_6cH166OXq6NA";
    }
}
