package com.puthuvaazhvu.mapping.utils.saving;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.FileUtils;
import com.puthuvaazhvu.mapping.utils.saving.modals.AnswersInfo;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class AnswerIOUtils extends IOUtilsBase {
    private static AnswerIOUtils answerIOUtils;

    public static AnswerIOUtils getInstance() {
        if (answerIOUtils == null) {
            answerIOUtils = new AnswerIOUtils();
        }
        return answerIOUtils;
    }

    private AnswerIOUtils() {
    }

    public Observable<AnswersInfo> saveAnswer(
            final Survey survey,
            final Question currentQuestion,
            final boolean isSurveyDone
    ) {
        return Observable.just(survey.getAsJson().toString())
                .flatMap(new Function<String, ObservableSource<File>>() {
                             @Override
                             public ObservableSource<File> apply(String s) throws Exception {
                                 String pathToFile = getRelativePathToAnswersDir() + File.separator
                                         + (survey.getId() + "_" + System.currentTimeMillis());
                                 return FileUtils.saveToFileFromPath(pathToFile, s);
                             }
                         }
                ).flatMap(new Function<File, ObservableSource<AnswersInfo>>() {
                    @Override
                    public ObservableSource<AnswersInfo> apply(File file) throws Exception {
                        return updateInfoForAnswers(
                                survey.getId(),
                                file.getName(),
                                TextUtils.join(",", QuestionUtils.getPathOfQuestion(currentQuestion)),
                                isSurveyDone
                        );
                    }
                });
    }

    private Observable<AnswersInfo> updateInfoForAnswers(
            final String surveyID,
            final String snapshotFileName,
            final String snapshotPath,
            final boolean isSurveyDone) {

        return Observable.just(getRelativePathToAnswersInfoFile())
                .flatMap(new Function<String, ObservableSource<AnswersInfo>>() {
                    @Override
                    public ObservableSource<AnswersInfo> apply(String path) throws Exception {

                        if (FileUtils.fileExists(path)) {
                            return readAnswerInfoFile();
                        } else {

                            // create a dummy json and return

                            return Observable.create(new ObservableOnSubscribe<AnswersInfo>() {
                                @Override
                                public void subscribe(ObservableEmitter<AnswersInfo> e) throws Exception {
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("version", Constants.Versions.ANSWERS_INFO_VERSION);
                                    JsonArray surveysArrayDummy = new JsonArray();
                                    jsonObject.add("surveys", surveysArrayDummy);

                                    AnswersInfo answersInfo = parseAnswerInfo(jsonObject);

                                    e.onNext(answersInfo);
                                    e.onComplete();
                                }
                            });
                        }
                    }
                })
                .flatMap(new Function<AnswersInfo, ObservableSource<AnswersInfo>>() {
                    @Override
                    public ObservableSource<AnswersInfo> apply(AnswersInfo answersInfo) throws Exception {
                        AnswersInfo.Snapshot snapshot = new AnswersInfo.Snapshot();
                        snapshot.setComplete(isSurveyDone);
                        snapshot.setSnapshotFileName(snapshotFileName);
                        snapshot.setPathToLastQuestion(snapshotPath);
                        snapshot.setTimestamp(System.currentTimeMillis());

                        if (answersInfo.isSurveyPresent(surveyID)) {
                            // add the snapshot to the existing survey
                            answersInfo.getSurvey(surveyID).getSnapshots().add(snapshot);
                        } else {
                            AnswersInfo.Survey survey = new AnswersInfo.Survey();
                            survey.setSurveyID(surveyID);

                            ArrayList<AnswersInfo.Snapshot> snapshots = new ArrayList<>();
                            snapshots.add(snapshot);

                            survey.setSnapshots(snapshots);

                            // create a new survey
                            answersInfo.getSurveys().add(survey);
                        }

                        return FileUtils.saveToFileFromPath(getRelativePathToAnswersInfoFile()
                                , answersInfo.toJsonString())
                                .flatMap(new Function<File, ObservableSource<AnswersInfo>>() {
                                    @Override
                                    public ObservableSource<AnswersInfo> apply(File file) throws Exception {
                                        return readAnswerInfoFile();
                                    }
                                });
                    }
                });
    }

    public Observable<AnswersInfo> readAnswerInfoFile() {
        return readFileAsJson(getRelativePathToAnswersInfoFile())
                .map(new Function<JsonObject, AnswersInfo>() {
                    @Override
                    public AnswersInfo apply(JsonObject jsonObject) throws Exception {
                        return parseAnswerInfo(jsonObject);
                    }
                });
    }

    public String getRelativePathToAnswersInfoFile() {
        return getRelativePathToAnswersDir() + File.separator + Constants.INFO_FILE_NAME;
    }

    public String getRelativePathToAnswersDir() {
        return File.separator + Constants.ANSWERS_DATA_DIR;
    }

    public AnswersInfo parseAnswerInfo(JsonObject jsonObject) {
        Gson gson = new Gson();
        return gson.fromJson(jsonObject, AnswersInfo.class);
    }
}
