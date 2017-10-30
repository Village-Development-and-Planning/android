package com.puthuvaazhvu.mapping.network.implementations;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.SurveyInfo;
import com.puthuvaazhvu.mapping.network.APIError;
import com.puthuvaazhvu.mapping.network.ErrorUtils;
import com.puthuvaazhvu.mapping.network.client_interfaces.ListSurveysClient;
import com.puthuvaazhvu.mapping.network.client_interfaces.SingleSurveyClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SingleSurveyAPI extends BaseAPI {
    public static SingleSurveyAPI singleSurveyAPI;
    private final SingleSurveyClient client;

    public interface SingleSurveyAPICallbacks {
        void onSurveyLoaded(String surveyJson);

        void onErrorOccurred(APIError error);
    }

    public static SingleSurveyAPI getInstance(String authToken) {
        if (singleSurveyAPI == null) {
            singleSurveyAPI = new SingleSurveyAPI(authToken);
        }
        return singleSurveyAPI;
    }

    private SingleSurveyAPI(String authToken) {
        super();
        client = getRetrofit(authToken).create(SingleSurveyClient.class);
    }

    public void getSurvey(String surveyID, final SingleSurveyAPICallbacks singleSurveyAPICallbacks) {

        Call<JsonElement> call = client.getSurvey(surveyID);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    singleSurveyAPICallbacks.onSurveyLoaded(getSurveyFromResponse(response));
                } else {
                    singleSurveyAPICallbacks.onErrorOccurred(ErrorUtils.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                singleSurveyAPICallbacks.onErrorOccurred(ErrorUtils.parseError(t));
            }
        });
    }

    public String getSurveySynchronous(String surveyID) throws IOException {
        Call<JsonElement> call = client.getSurvey(surveyID);

        Response<JsonElement> response = call.execute();

        if (response.isSuccessful()) {
            return getSurveyFromResponse(response);
        } else {
            APIError apiError = ErrorUtils.parseError(response);
            Timber.e("Error while geting survey " + surveyID + " error: " + apiError.message());
            return null;
        }
    }

    public Callable<String> getSurvey(final String surveyID) throws IOException {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {

                Call<JsonElement> call = client.getSurvey(surveyID);

                Response<JsonElement> response = call.execute();

                if (response.isSuccessful()) {
                    return getSurveyFromResponse(response);
                } else {
                    APIError apiError = ErrorUtils.parseError(response);
                    return apiError.message();
                }
            }
        };
    }

    private String getSurveyFromResponse(Response<JsonElement> response) {
        JsonElement jsonElement = response.body();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return jsonObject.toString();
    }
}
