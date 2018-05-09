package com.puthuvaazhvu.mapping.network.client_interfaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.SurveyAPIInfo;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by muthuveerappans on 09/05/18.
 */

public interface SurveysUploadClient {
    @Multipart
    @POST("/app/upload")
    Call<JsonElement> getSurveyList(@Part("name") RequestBody name, @Part MultipartBody.Part filePart);
}
