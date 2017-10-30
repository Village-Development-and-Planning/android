package com.puthuvaazhvu.mapping.network.client_interfaces;

import com.google.gson.JsonElement;
import com.puthuvaazhvu.mapping.modals.SurveyInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public interface ListSurveysClient {
    @GET("/cms/surveys")
    Call<List<SurveyInfo>> getSurveyList();
}
