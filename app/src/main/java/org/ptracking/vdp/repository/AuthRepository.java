package org.ptracking.vdp.repository;

import android.content.Context;

import org.ptracking.vdp.modals.surveyorinfo.SurveyorInfoFromAPI;
import org.ptracking.vdp.network.implementations.SurveyorInfoAPI;
import org.ptracking.vdp.utils.SharedPreferenceUtils;
import org.ptracking.vdp.utils.Utils;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class AuthRepository extends Repository<SurveyorInfoFromAPI> {
    private final SurveyorInfoAPI surveyorInfoAPI;
    private final SharedPreferenceUtils sharedPreferenceUtils;

    public AuthRepository(Context context, String surveyorCode, String password) {
        super(context, surveyorCode, password);

        surveyorInfoAPI = new SurveyorInfoAPI(surveyorCode, password);
        sharedPreferenceUtils = SharedPreferenceUtils.getInstance(context);
    }

    @Override
    public Observable<SurveyorInfoFromAPI> get(boolean forceOffline) {
        if (forceOffline) {
            return Observable.just(getFromPrefs());
        }

        if (Utils.isNetworkAvailable(getContext()) || getFromPrefs() == null) {
            return getFromNetwork();
        } else {
            return Observable.just(getFromPrefs());
        }
    }

    private Observable<SurveyorInfoFromAPI> getFromNetwork() {
        return surveyorInfoAPI.getAuthData()
                .doOnNext(new Consumer<SurveyorInfoFromAPI>() {
                    @Override
                    public void accept(SurveyorInfoFromAPI surveyorInfoFromAPI) throws Exception {
                        sharedPreferenceUtils.putSurveyorInfo(surveyorInfoFromAPI);
                    }
                });
    }

    private SurveyorInfoFromAPI getFromPrefs() {
        return sharedPreferenceUtils.getSurveyorInfo();
    }
}
