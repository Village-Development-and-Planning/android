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

    public SurveyIO(String surveyID) {
        dataInfoIO = new DataInfoIO();
        this.surveyID = surveyID;
        this.surveyName = "";
    }

    @Override
    public Observable<Survey> read(File file) {
        return StorageUtils.readFromFile(file)
                .map(new Function<byte[], Survey>() {
                    @Override
                    public Survey apply(byte[] bytes) throws Exception {
                        return (Survey) StorageUtils.deserialize(bytes).blockingFirst();
                    }
                });
    }

    @Override
    public Observable<File> save(final File file, Survey contents) {
        return StorageUtils.serialize(contents)
                .map(new Function<byte[], File>() {
                    @Override
                    public File apply(byte[] bytes) throws Exception {
                        File f = StorageUtils.saveContentsToFile(file, bytes).blockingFirst();

                        if (!f.exists())
                            throw new Exception("File " + file.getAbsolutePath() + " is not present.");

                        // read and update the datainfo.json file
                        dataInfoIO.read()
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

                                        dataInfoIO.save(dataInfo).blockingFirst();

                                        return file;
                                    }
                                })
                                .blockingFirst();

                        return f;
                    }
                });
    }

    @Override
    public Observable<Boolean> delete() {
        return super.delete()
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean deletionStatus) throws Exception {
                        if (!deletionStatus)
                            throw new Exception("Failed to delete the file " + filename());

                        return dataInfoIO.read()
                                .map(new Function<DataInfo, Boolean>() {
                                    @Override
                                    public Boolean apply(DataInfo dataInfo) throws Exception {
                                        boolean result = dataInfo.getSurveysInfo().removeSurvey(surveyID);
                                        if (!result)
                                            throw new Exception("Error deleting file " + filename());

                                        dataInfoIO.save(dataInfo).blockingFirst();

                                        return true;
                                    }
                                }).blockingFirst();
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
