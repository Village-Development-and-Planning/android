package com.puthuvaazhvu.mapping.network.client_interfaces;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public interface SurveyorInfoClient {
    @GET("/app/info")
    Call<JsonElement> getAuthData();
}
