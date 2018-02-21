package com.puthuvaazhvu.mapping.network.implementations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.deserialization.SurveyGsonAdapter;
import com.puthuvaazhvu.mapping.network.APIError;
import com.puthuvaazhvu.mapping.network.ErrorUtils;
import com.puthuvaazhvu.mapping.network.adapters.NetworkAdapter;
import com.puthuvaazhvu.mapping.network.client_interfaces.SingleSurveyClient;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SingleSurveyAPI {
    public static SingleSurveyAPI singleSurveyAPI;
    private final SingleSurveyClient client;
    private Gson gson = new Gson();

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

        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Survey.class, new SurveyGsonAdapter());

        gson = gsonBuilder.create();
    }

    public Observable<Survey> getSurvey(final String surveyID) {
        return Observable.create(new ObservableOnSubscribe<Survey>() {
            @Override
            public void subscribe(ObservableEmitter<Survey> emitter) throws Exception {
                Call<JsonElement> call = client.getSurvey(surveyID);
                Response<JsonElement> response = call.execute();

                if (response.isSuccessful()) {
                    JsonElement jsonElement = response.body();
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    emitter.onNext(gson.fromJson(jsonObject, Survey.class));
                    emitter.onComplete();
                } else {
                    APIError apiError = ErrorUtils.parseError(response);
                    emitter.onError(new Throwable(apiError.message()));
                }
            }
        });
    }
}
