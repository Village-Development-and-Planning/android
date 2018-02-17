package com.puthuvaazhvu.mapping.filestorage;

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

public class SurveyIO extends StorageIO<Survey> {
    private final DataInfoIO dataInfoIO;
    private final String surveyID, surveyName;

    public SurveyIO(String surveyID, String surveyName) {
        dataInfoIO = new DataInfoIO();
        this.surveyID = surveyID;
        this.surveyName = surveyName;
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
    public Observable<File> save(final File file, Survey contents) {
        return StorageUtils.serialize(contents)
                .flatMap(new Function<byte[], ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(byte[] bytes) throws Exception {
                        return StorageUtils.saveContentsToFile(file, bytes);
                    }
                })
                .flatMap(new Function<File, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(final File file) throws Exception {
                        if (!file.exists())
                            throw new Exception("File " + file.getAbsolutePath() + " is not present.");

                        // read and update the datainfo.json file
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

                                        dataInfo.getSurveysInfo().getSurveys().add(survey);

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
                                        return dataInfo.getSurveysInfo().removeSurvey(filename());
                                    }
                                });
                    }
                });
    }

    @Override
    public String getAbsolutePath() {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.SURVEY_DIR + "/" + filename();
    }

    private String filename() {
        return surveyID + ".json";
    }
}
