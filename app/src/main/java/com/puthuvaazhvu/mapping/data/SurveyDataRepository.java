package com.puthuvaazhvu.mapping.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.filestorage.SurveyIO;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.network.APIUtils;
import com.puthuvaazhvu.mapping.network.implementations.SingleSurveyAPI;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 01/02/18.
 */

public class SurveyDataRepository extends DataRepository<Survey> {
    private final SingleSurveyAPI singleSurveyAPI;

    private String snapshotID, surveyID;

    public SurveyDataRepository(
            SharedPreferences sharedPreferences,
            Context context,
            String surveyID
    ) {
        super(context);

        this.surveyID = surveyID;

        singleSurveyAPI = SingleSurveyAPI.getInstance(APIUtils.getAuth(sharedPreferences));
    }

    @Override
    public Observable<Survey> get(boolean forceNetwork) {
        if (forceNetwork) {
            return getSurveyFromAPI(surveyID);
        } else {
            return getSurveyFromFile()
                    .onErrorReturn(new Function<Throwable, Survey>() {
                        @Override
                        public Survey apply(Throwable throwable) throws Exception {
                            return getSurveyFromAPI(surveyID).blockingFirst();
                        }
                    });
        }
    }

    private Observable<Survey> getSurveyFromAPI(String surveyID) {
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
}
