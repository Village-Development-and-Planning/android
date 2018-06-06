package com.puthuvaazhvu.mapping.network.implementations;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.surveyorinfo.SurveyorInfo;
import com.puthuvaazhvu.mapping.network.adapters.NetworkAdapter;
import com.puthuvaazhvu.mapping.network.client_interfaces.SurveyorInfoClient;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class SurveyorInfoAPI {
    private final SurveyorInfoClient client;
    private final Gson gson;

    public SurveyorInfoAPI(String username, String password) {
        NetworkAdapter adapter = NetworkAdapter.getInstance();
        Retrofit retrofit = adapter.getDigestAuthenticationRetrofit(username, password);
        client = retrofit.create(SurveyorInfoClient.class);

        this.gson = new Gson();
    }

    public Observable<SurveyorInfo> getAuthData() {
        return Observable.create(new ObservableOnSubscribe<SurveyorInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<SurveyorInfo> e) throws Exception {

                Call<JsonElement> call = client.getAuthData();
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        JsonElement jsonElement = response.body();
                        if (jsonElement == null) {
                            e.onError(new Throwable("Response from the server is null."));
                            return;
                        }
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        SurveyorInfo surveyorInfo = gson.fromJson(jsonObject, SurveyorInfo.class);
                        e.onNext(surveyorInfo);
                        e.onComplete();
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        e.onError(t);
                    }
                });
            }
        });
    }
}
