package com.puthuvaazhvu.mapping.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.filestorage.SurveyIO;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.network.APIUtils;
import com.puthuvaazhvu.mapping.network.implementations.SingleSurveyAPI;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 01/02/18.
 */

public class SurveyDataRepository extends DataRepository<Survey> {
    private final SingleSurveyAPI singleSurveyAPI;

    private String surveyID;

    public SurveyDataRepository(
            Context context,
            String username,
            String password,
            String surveyID
    ) {
        super(context);
        this.surveyID = surveyID;
        singleSurveyAPI = new SingleSurveyAPI(username, password);
    }

    public SurveyDataRepository(Context context, String surveyID) {
        super(context);
        singleSurveyAPI = new SingleSurveyAPI("", "");
        this.surveyID = surveyID;
    }

    private Observable<Survey> getSurveyFromAPI(String surveyID) {
        if (!Utils.isNetworkAvailable(context)) {
            return Observable.error(new Throwable("No internet available"));
        }

        return singleSurveyAPI.getSurvey(surveyID)
                .map(new Function<Survey, Survey>() {
                    @Override
                    public Survey apply(Survey survey) throws Exception {
                        SurveyIO surveyIO = new SurveyIO(survey.getId(), survey.getName());
                        File file = surveyIO.save(survey).blockingFirst();

                        if (!file.exists())
                            throw new Exception("Survey not saved at " + surveyIO.getAbsolutePath());

                        return survey;
                    }
                });
    }

    private Observable<Survey> getSurveyFromFile() {
        SurveyIO surveyIO = new SurveyIO(surveyID);
        return surveyIO.read();
    }

    @Override
    public Observable<Survey> getFromNetwork() {
        return getSurveyFromAPI(surveyID);
    }

    @Override
    public Observable<Survey> getFromFileSystem() {
        return getSurveyFromFile();
    }
}
