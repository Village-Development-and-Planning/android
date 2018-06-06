package com.puthuvaazhvu.mapping.data;

import android.content.Context;

import com.puthuvaazhvu.mapping.modals.surveyorinfo.SurveyorInfo;
import com.puthuvaazhvu.mapping.network.implementations.SurveyorInfoAPI;
import com.puthuvaazhvu.mapping.utils.SharedPreferenceUtils;
import com.puthuvaazhvu.mapping.utils.Utils;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class AuthRepository extends Repository<SurveyorInfo> {
    private final SurveyorInfoAPI surveyorInfoAPI;
    private final SharedPreferenceUtils sharedPreferenceUtils;

    public AuthRepository(Context context, String surveyorCode, String password) {
        super(context, surveyorCode, password);

        surveyorInfoAPI = new SurveyorInfoAPI(surveyorCode, password);
        sharedPreferenceUtils = SharedPreferenceUtils.getInstance(context);
    }

    @Override
    public Observable<SurveyorInfo> get(boolean forceOffline) {
        if (forceOffline) {
            return Observable.just(getFromPrefs());
        }

        if (Utils.isNetworkAvailable(getContext()) || getFromPrefs() == null) {
            return getFromNetwork();
        } else {
            return Observable.just(getFromPrefs());
        }
    }

    private Observable<SurveyorInfo> getFromNetwork() {
        return surveyorInfoAPI.getAuthData()
                .doOnNext(new Consumer<SurveyorInfo>() {
                    @Override
                    public void accept(SurveyorInfo surveyorInfo) throws Exception {
                        sharedPreferenceUtils.putSurveyorInfo(surveyorInfo);
                    }
                });
    }

    private SurveyorInfo getFromPrefs() {
        return sharedPreferenceUtils.getSurveyorInfo();
    }
}
