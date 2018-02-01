package com.puthuvaazhvu.mapping.network.implementations;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.network.adapters.NetworkAdapter;
import com.puthuvaazhvu.mapping.network.client_interfaces.AuthClient;

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

public class AuthAPI {
    public static AuthAPI authAPI;
    private final AuthClient client;

    public static AuthAPI getInstance(String authToken) {
        if (authAPI == null) {
            authAPI = new AuthAPI(authToken);
        }
        return authAPI;
    }

    private AuthAPI(String authToken) {
        super();
        NetworkAdapter adapter = NetworkAdapter.getInstance();
        Retrofit retrofit = adapter.getUnsafeRetrofit(authToken);
        client = retrofit.create(AuthClient.class);
    }

    public Observable<JsonObject> getAuthData() {
        return Observable.create(new ObservableOnSubscribe<JsonObject>() {
            @Override
            public void subscribe(final ObservableEmitter<JsonObject> e) throws Exception {

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
                        e.onNext(jsonObject);
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
