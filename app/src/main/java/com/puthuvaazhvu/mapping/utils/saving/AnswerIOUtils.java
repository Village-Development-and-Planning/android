package com.puthuvaazhvu.mapping.utils.saving;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.other.Config;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.FileUtils;
import com.puthuvaazhvu.mapping.utils.saving.modals.AnswersInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import timber.log.Timber;

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

    public Observable<AnswersInfo> saveAnswerToFile(
            final Survey survey,
            final Question currentQuestion,
            final boolean isSurveyDone
    ) {
        return Observable.just(survey.getAsJson().toString())
                .flatMap(new Function<String, ObservableSource<File>>() {
                             @Override
                             public ObservableSource<File> apply(String s) throws Exception {

                                 String rootDir = getRelativePathToAnswersDir();

                                 if (isSurveyDone)
                                     rootDir = getRelativePathToCompletedAnswersDir();

                                 String pathToFile = rootDir + File.separator
                                         + (survey.getId() + "_" + System.currentTimeMillis());

                                 return FileUtils.saveToFileFromPath(pathToFile, s);
                             }
                         }
                ).flatMap(new Function<File, ObservableSource<AnswersInfo>>() {
                    @Override
                    public ObservableSource<AnswersInfo> apply(File file) throws Exception {
                        return addSnapShotToInfo(
                                survey.getId(),
                                file.getName(),
                                TextUtils.join(",", QuestionUtils.getPathOfQuestion(currentQuestion)),
                                isSurveyDone,
                                file.getAbsolutePath()
                        );
                    }
                });
    }

    public Observable<AnswersInfo> saveInfo(AnswersInfo answersInfo) {
        return FileUtils.saveToFileFromPath(getRelativePathToAnswersInfoFile()
                , answersInfo.toJsonString())
                .flatMap(new Function<File, ObservableSource<AnswersInfo>>() {
                    @Override
                    public ObservableSource<AnswersInfo> apply(File file) throws Exception {
                        return readAnswerInfoFile();
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

    public Observable<String> getAnswerFromFile(String snapShotFileName) {
        return FileUtils.readFromPath(answerIOUtils.getRelativePathToAnswersDir()
                + File.separator + snapShotFileName);
    }

    public boolean deleteInfoFile() {
        return FileUtils.deleteFile(getRelativePathToAnswersInfoFile());
    }

    public boolean deleteCompletedAnswer(String fileName) {
        return FileUtils.deleteFile(getRelativePathToCompletedAnswersDir() + File.separator + fileName);
    }

    public boolean deleteCompletedAnswer(File file) {
        return FileUtils.deleteFile(file);
    }

    private Observable<AnswersInfo> addSnapShotToInfo(
            final String surveyID,
            final String snapshotFileName,
            final String snapshotPath,
            final boolean isSurveyDone,
            final String pathToFile) {

        return readAnswerInfoFile()
                .onErrorReturnItem(createEmptyInfo())
                .map(new Function<AnswersInfo, AnswersInfo>() {
                    @Override
                    public AnswersInfo apply(AnswersInfo answersInfo) throws Exception {
                        AnswersInfo.Snapshot snapshot = new AnswersInfo.Snapshot();
                        snapshot.setComplete(isSurveyDone);
                        snapshot.setSnapshotFileName(snapshotFileName);
                        snapshot.setPathToLastQuestion(snapshotPath);
                        snapshot.setPathToFile(pathToFile);
                        snapshot.setTimestamp(System.currentTimeMillis());

                        if (answersInfo.isSurveyPresent(surveyID)) {
                            // add the snapshot to the existing survey
                            answersInfo.getSurvey(surveyID).getSnapshots().add(snapshot);
                        } else {
                            // create a new survey
                            AnswersInfo.Survey survey = new AnswersInfo.Survey();
                            survey.setSurveyID(surveyID);

                            ArrayList<AnswersInfo.Snapshot> snapshots = new ArrayList<>();
                            snapshots.add(snapshot);

                            survey.setSnapshots(snapshots);

                            answersInfo.getSurveys().add(survey);
                        }
                        return answersInfo;
                    }
                })
                // delete the previous snapshots of this answer
                .map(new Function<AnswersInfo, AnswersInfo>() {
                    @Override
                    public AnswersInfo apply(AnswersInfo answersInfo) throws Exception {
                        AnswersInfo.Survey survey =
                                answersInfo.getSurvey(surveyID);

                        if (survey == null) return answersInfo;

                        Iterator<AnswersInfo.Snapshot> iterator = survey.getSnapshots().iterator();

                        while (iterator.hasNext()) {
                            AnswersInfo.Snapshot snapshot = iterator.next();

                            if (!snapshot.getSnapshotFileName().equals(snapshotFileName)
                                    && !snapshot.isComplete()) {
                                // delete the file
                                boolean result = FileUtils.deleteFile(new File(snapshot.getPathToFile()));
                                Timber.i("Successfully deleted snapshot file " + snapshot.getSnapshotFileName());
                                if (result) {
                                    // remove the entry in info.json
                                    iterator.remove();
                                }
                            }
                        }

                        return answersInfo;
                    }
                })
                .flatMap(new Function<AnswersInfo, ObservableSource<AnswersInfo>>() {
                    @Override
                    public ObservableSource<AnswersInfo> apply(AnswersInfo answersInfo) throws Exception {
                        return saveInfo(answersInfo);
                    }
                });
    }

    private String getRelativePathToAnswersInfoFile() {
        return getRelativePathToAnswersDir() + File.separator + Constants.INFO_FILE_NAME;
    }

    private String getRelativePathToCompletedAnswersDir() {
        return getRelativePathToAnswersDir() + File.separator + Constants.COMPLETED_ANSWERS_DATA_DIR;
    }

    private String getRelativePathToAnswersDir() {
        return File.separator + Constants.ANSWERS_DATA_DIR;
    }

    private AnswersInfo createEmptyInfo() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("version", Config.Versions.ANSWERS_INFO_VERSION);
        JsonArray surveysArrayDummy = new JsonArray();
        jsonObject.add("surveys", surveysArrayDummy);

        return parseAnswerInfo(jsonObject);
    }

    private AnswersInfo parseAnswerInfo(JsonObject jsonObject) {
        Gson gson = new Gson();
        return gson.fromJson(jsonObject, AnswersInfo.class);
    }
}
