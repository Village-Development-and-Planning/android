package com.puthuvaazhvu.mapping.filestorage;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveysInfo;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.root;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public class AnswerIO extends StorageIO<Survey> {
    private final DataInfoIO dataInfoIO;
    private final String surveyID, surveyName, answerID;

    public AnswerIO(String surveyID, String surveyName, String answerID) {
        dataInfoIO = new DataInfoIO();
        this.surveyID = surveyID;
        this.surveyName = surveyName;
        this.answerID = answerID;
    }

    @Override
    public Observable<Survey> read(File file) {
        return StorageUtils.readFromFile(file)
                .flatMap(new Function<byte[], ObservableSource<Survey>>() {
                    @Override
                    public ObservableSource<Survey> apply(byte[] bytes) throws Exception {
                        return StorageUtils.deserialize(bytes)
                                .map(new Function<Object, Survey>() {
                                    @Override
                                    public Survey apply(Object o) throws Exception {
                                        return (Survey) o;
                                    }
                                });
                    }
                });
    }

    @Override
    public Observable<File> save(final File file, Survey survey) {
        return Observable.just(survey.getAsJson().toString())
                .flatMap(new Function<String, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(String s) throws Exception {
                        return StorageUtils.saveContentsToFile(file, s);
                    }
                })
                .flatMap(new Function<File, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(final File file) throws Exception {
                        return dataInfoIO.read()
                                .onErrorReturnItem(new DataInfo())
                                .map(new Function<DataInfo, File>() {
                                    @Override
                                    public File apply(DataInfo dataInfo) throws Exception {
                                        SurveysInfo.Survey survey = new SurveysInfo.Survey();
                                        survey.setFilename(filename());
                                        survey.setSurveyID(surveyID);
                                        survey.setSurveyName(surveyName);
                                        survey.setTimeStamp(System.currentTimeMillis());

                                        dataInfo.getAnswersInfo().getSurveys().add(survey);

                                        return file;
                                    }
                                });
                    }
                });
    }

    @Override
    public Observable<Boolean> delete() {
        return super.delete()
                .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(Boolean deletionStatus) throws Exception {
                        if (!deletionStatus)
                            throw new Exception("Failed to delete the file " + filename());

                        return dataInfoIO.read()
                                .map(new Function<DataInfo, Boolean>() {
                                    @Override
                                    public Boolean apply(DataInfo dataInfo) throws Exception {
                                        return dataInfo.getAnswersInfo().removeSurvey(filename());
                                    }
                                });
                    }
                });
    }

    @Override
    public String getAbsolutePath() {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.ANSWER_DIR + "/" + filename();
    }

    private String filename() {
        return answerID + ".json";
    }
}
