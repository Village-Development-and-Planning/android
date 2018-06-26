package org.ptracking.vdp.network.client_interfaces;

import org.ptracking.vdp.modals.SurveyAPIInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public interface ListSurveysClient {
    @GET("/app/download")
    Call<List<SurveyAPIInfo>> getSurveyList();
}
