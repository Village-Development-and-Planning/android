package com.puthuvaazhvu.mapping.utils.saving;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.FileUtils;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class SaveAnswerUtils {

    public static Observable<Survey> saveAnswer(
            final Survey survey,
            final String snapshotPath,
            final boolean isSurveyDone
    ) {
        return Observable.just(survey.getAsJson().toString())
                .flatMap(new Function<String, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(String s) throws Exception {
                        String pathToFile = getRelativePathToAnswersDir() + File.separator
                                + (survey.getId() + "_" + System.currentTimeMillis());
                        return FileUtils.saveToFile(pathToFile, s);
                    }
                })
    }

    private static Observable<JsonObject> updateInfoForAnswers(
            String surveyID,
            String snapshotID,
            String snapshotPath,
            boolean isSurveyDone) {

        return Observable.create(new ObservableOnSubscribe<JsonObject>() {
            @Override
            public void subscribe(ObservableEmitter<JsonObject> e) throws Exception {
                File file = new File(getRelativePathToAnswersDir());
                if (file.exists()) {

                }
            }
        });
    }

    private static String getRelativePathToAnswersInfoFile() {
        return getRelativePathToAnswersDir() + File.separator + Constants.INFO_FILE_NAME;
    }

    private static String getRelativePathToAnswersDir() {
        return File.separator + Constants.ANSWERS_DATA_DIR;
    }
}
