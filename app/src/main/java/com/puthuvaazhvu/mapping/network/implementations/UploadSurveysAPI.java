package com.puthuvaazhvu.mapping.network.implementations;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Upload;
import com.puthuvaazhvu.mapping.network.adapters.NetworkAdapter;
import com.puthuvaazhvu.mapping.network.client_interfaces.SurveysUploadClient;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by muthuveerappans on 09/05/18.
 */

public class UploadSurveysAPI {
    private final SurveysUploadClient client;
    private final Gson gson;

    public UploadSurveysAPI(String username, String password) {
        NetworkAdapter adapter = NetworkAdapter.getInstance();
        Retrofit retrofit = adapter.getDigestAuthenticationRetrofit(username, password);
        client = retrofit.create(SurveysUploadClient.class);
        gson = new Gson();
    }

    public Observable<Upload> uploadFile(final String name, final File file) {
        return Observable.create(new ObservableOnSubscribe<Upload>() {
            @Override
            public void subscribe(final ObservableEmitter<Upload> emitter) throws Exception {
                RequestBody nameField = RequestBody.create(MediaType.parse("text/plain"), name);
                MultipartBody.Part fileBody = MultipartBody.Part.createFormData("data-file", file.getName(), RequestBody.create(MediaType.parse("application/json"), file));

                Call<JsonElement> call = client.getSurveyList(nameField, fileBody);
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        JsonElement jsonElement = response.body();
                        if (jsonElement == null) {
                            emitter.onError(new Throwable("Response from the server is null."));
                            return;
                        }

                        JsonObject json = jsonElement.getAsJsonObject();
                        JsonObject entityJson = json.get("entity").getAsJsonObject();
                        Upload upload = gson.fromJson(entityJson, Upload.class);

                        emitter.onNext(upload);
                        emitter.onComplete();
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        emitter.onError(t);
                    }
                });

            }
        });
    }

}
