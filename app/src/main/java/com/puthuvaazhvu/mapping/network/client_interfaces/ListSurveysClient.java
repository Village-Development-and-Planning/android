package com.puthuvaazhvu.mapping.network.client_interfaces;

import com.puthuvaazhvu.mapping.modals.SurveyAPIInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public interface ListSurveysClient {
    @GET("/cms/surveys")
    Call<List<SurveyAPIInfo>> getSurveyList();
}
