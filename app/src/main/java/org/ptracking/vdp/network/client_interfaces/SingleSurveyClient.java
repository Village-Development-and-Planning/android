package org.ptracking.vdp.network.client_interfaces;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public interface SingleSurveyClient {
    @GET("/app/surveys/{id}")
    Call<JsonElement> getSurvey(
            @Path("id") String surveyID
    );
}
