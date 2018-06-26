package org.ptracking.vdp.network.implementations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.ptracking.vdp.modals.Survey;
import org.ptracking.vdp.modals.deserialization.SurveyGsonAdapter;
import org.ptracking.vdp.other.Constants;
import org.ptracking.vdp.other.Error;
import org.ptracking.vdp.network.ErrorUtils;
import org.ptracking.vdp.network.adapters.NetworkAdapter;
import org.ptracking.vdp.network.client_interfaces.SingleSurveyClient;
import org.ptracking.vdp.utils.ThrowableWithErrorCode;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SingleSurveyAPI {
    private final SingleSurveyClient client;
    private Gson gson = new Gson();

    public SingleSurveyAPI(String username, String password) {
        super();
        NetworkAdapter adapter = NetworkAdapter.getInstance();
        Retrofit retrofit = adapter.getDigestAuthenticationRetrofit(username, password);
        client = retrofit.create(SingleSurveyClient.class);

        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Survey.class, new SurveyGsonAdapter());

        gson = gsonBuilder.create();
    }

    public Observable<Survey> getSurvey(final String surveyId) {
        return Observable.just(surveyId)
                .observeOn(Schedulers.io())
                .map(new Function<String, Survey>() {
                    @Override
                    public Survey apply(String s) throws Exception {
                        Call<JsonElement> call = client.getSurvey(surveyId);
                        Response<JsonElement> response = call.execute();

                        if (response.isSuccessful()) {
                            JsonElement jsonElement = response.body();
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            return gson.fromJson(jsonObject, Survey.class);
                        } else {
                            Error error = ErrorUtils.parseError(response);
                            throw new Exception(new ThrowableWithErrorCode(error.message(),
                                    Constants.ErrorCodes.NETWORK_ERROR));
                        }
                    }
                });
    }
}
