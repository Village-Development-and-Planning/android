package com.puthuvaazhvu.mapping.network;

import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class APIUtils {
    public static String BASE_URL = "https://ptracking.org/";

    @Deprecated
    public static String getAuth(SharedPreferences sharedPreferences) {
        return "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6IjAwMDAwMCIsIm5hbWUiOiJBcHAgU3VydmV5b3IiLCJyb2xlcyI6WyJTVVJWRVlPUiJdLCJpYXQiOjE1MTk4MTgzMzB9.ueYCvM-qOqg5Dhpsh3iC3XkeJv995Bur9PEfmbf4h1g";
    }
}
