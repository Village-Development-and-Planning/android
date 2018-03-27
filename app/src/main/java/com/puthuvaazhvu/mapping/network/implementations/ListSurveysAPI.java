package com.puthuvaazhvu.mapping.network.implementations;

import com.puthuvaazhvu.mapping.modals.SurveyAPIInfo;
import com.puthuvaazhvu.mapping.other.Error;
import com.puthuvaazhvu.mapping.network.ErrorUtils;
import com.puthuvaazhvu.mapping.network.adapters.NetworkAdapter;
import com.puthuvaazhvu.mapping.network.client_interfaces.ListSurveysClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class ListSurveysAPI {
    public static ListSurveysAPI surveysAPI;
    private final ListSurveysClient client;

    public interface ListSurveysAPICallbacks {
        void onSurveysLoaded(List<SurveyAPIInfo> surveyInfoList);

        void onErrorOccurred(Error error);
    }

    public ListSurveysAPI(String username, String password) {
        NetworkAdapter adapter = NetworkAdapter.getInstance();
        Retrofit retrofit = adapter.getDigestAuthenticationRetrofit(username, password);
        client = retrofit.create(ListSurveysClient.class);
    }

    public Observable<List<SurveyAPIInfo>> getSurveysList() {
        return Observable.create(new ObservableOnSubscribe<List<SurveyAPIInfo>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<SurveyAPIInfo>> emitter) throws Exception {
                getSurveysList(new ListSurveysAPI.ListSurveysAPICallbacks() {
                    @Override
                    public void onSurveysLoaded(List<SurveyAPIInfo> surveyInfoList) {
                        emitter.onNext(surveyInfoList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onErrorOccurred(Error error) {
                        emitter.onError(new Throwable(error.message()));
                    }
                });
            }
        });
    }

    public void getSurveysList(final ListSurveysAPICallbacks callbacks) {

        Call<List<SurveyAPIInfo>> call = client.getSurveyList();

        call.enqueue(new Callback<List<SurveyAPIInfo>>() {
            @Override
            public void onResponse(Call<List<SurveyAPIInfo>> call, Response<List<SurveyAPIInfo>> response) {
                if (response.isSuccessful()) {

                    List<SurveyAPIInfo> surveyInfoList = response.body();

                    List<SurveyAPIInfo> result = new ArrayList<>();

                    // filter for disabled surveys
                    for (SurveyAPIInfo surveyInfo : surveyInfoList) {
                        if (surveyInfo.isEnabled()) {
                            result.add(surveyInfo);
                        }
                    }

                    callbacks.onSurveysLoaded(result);
                } else {
                    callbacks.onErrorOccurred(ErrorUtils.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<List<SurveyAPIInfo>> call, Throwable t) {
                callbacks.onErrorOccurred(ErrorUtils.parseError(t));
            }
        });
    }
}
