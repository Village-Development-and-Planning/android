package com.puthuvaazhvu.mapping.network.implementations;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.SurveyInfo;
import com.puthuvaazhvu.mapping.network.APIError;
import com.puthuvaazhvu.mapping.network.ErrorUtils;
import com.puthuvaazhvu.mapping.network.adapters.NetworkAdapter;
import com.puthuvaazhvu.mapping.network.client_interfaces.ListSurveysClient;
import com.puthuvaazhvu.mapping.network.client_interfaces.SingleSurveyClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SingleSurveyAPI {
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
        NetworkAdapter adapter = NetworkAdapter.getInstance();
        Retrofit retrofit = adapter.getUnsafeRetrofit(authToken);
        client = retrofit.create(SingleSurveyClient.class);
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

    public Single<String> getSurvey(final String surveyID) {

        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                Call<JsonElement> call = client.getSurvey(surveyID);
                Response<JsonElement> response = call.execute();

                if (response.isSuccessful()) {
                    emitter.onSuccess(getSurveyFromResponse(response));
                } else {
                    APIError apiError = ErrorUtils.parseError(response);
                    emitter.onError(new Throwable(apiError.message()));
                }
            }
        });
    }

    private String getSurveyFromResponse(Response<JsonElement> response) {
        JsonElement jsonElement = response.body();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return jsonObject.toString();
    }
}
