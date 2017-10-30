package com.puthuvaazhvu.mapping.network.implementations;

import com.puthuvaazhvu.mapping.modals.SurveyInfo;
import com.puthuvaazhvu.mapping.network.APIError;
import com.puthuvaazhvu.mapping.network.ErrorUtils;
import com.puthuvaazhvu.mapping.network.adapters.NetworkAdapter;
import com.puthuvaazhvu.mapping.network.client_interfaces.ListSurveysClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class ListSurveysAPI extends BaseAPI {
    public static ListSurveysAPI surveysAPI;
    private final ListSurveysClient client;

    public interface ListSurveysAPICallbacks {
        void onSurveysLoaded(List<SurveyInfo> surveyInfoList);

        void onErrorOccurred(APIError error);
    }

    public static ListSurveysAPI getInstance(String authToken) {
        if (surveysAPI == null) {
            surveysAPI = new ListSurveysAPI(authToken);
        }
        return surveysAPI;
    }

    private ListSurveysAPI(String authToken) {
        super();
        client = getRetrofit(authToken).create(ListSurveysClient.class);
    }

    public void getSurveysList(final ListSurveysAPICallbacks callbacks) {

        Call<List<SurveyInfo>> call = client.getSurveyList();

        call.enqueue(new Callback<List<SurveyInfo>>() {
            @Override
            public void onResponse(Call<List<SurveyInfo>> call, Response<List<SurveyInfo>> response) {
                if (response.isSuccessful()) {
                    callbacks.onSurveysLoaded(response.body());
                } else {
                    callbacks.onErrorOccurred(ErrorUtils.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<List<SurveyInfo>> call, Throwable t) {
                callbacks.onErrorOccurred(ErrorUtils.parseError(t));
            }
        });
    }
}
