package com.puthuvaazhvu.mapping.filestorage;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SnapshotsInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveysInfo;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;
import java.util.Iterator;

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
                .map(new Function<byte[], Survey>() {
                    @Override
                    public Survey apply(byte[] bytes) throws Exception {
                        String s = new String(bytes);
                        JsonParser jsonParser = new JsonParser();
                        JsonElement element = jsonParser.parse(s);
                        return new Survey(element.getAsJsonObject());
                    }
                });
    }

    @Override
    public Observable<File> save(final File file, Survey survey) {
        return Observable.just(survey.getAsJson().toString())
                .map(new Function<String, File>() {
                    @Override
                    public File apply(String c) throws Exception {
                        File f = StorageUtils.saveContentsToFile(file, c).blockingFirst();

                        DataInfo dataInfo = dataInfoIO.read()
                                .onErrorReturnItem(new DataInfo())
                                .blockingFirst();

                        SurveysInfo.Survey survey = new SurveysInfo.Survey();
                        survey.setFilename(filename());
                        survey.setSurveyID(surveyID);
                        survey.setSurveyName(surveyName);
                        survey.setTimeStamp(System.currentTimeMillis());

                        dataInfo.getAnswersInfo().getSurveys().add(survey);

                        // remove all the snapshots of the particular surveyID

                        SnapshotsInfo.Survey s
                                = dataInfo.getSnapshotsInfo().getSurvey(surveyID);

                        if (s != null) {
                            Iterator<SnapshotsInfo.Snapshot> snapshotIterator
                                    = s.getSnapshots().iterator();

                            while (snapshotIterator.hasNext()) {
                                SnapshotsInfo.Snapshot snapshot = snapshotIterator.next();
                                SnapshotIO snapshotIO = new SnapshotIO(snapshot
                                        .getSnapshotID());
                                snapshotIO.delete().blockingFirst();
                                snapshotIterator.remove();
                            }

                            dataInfoIO.save(dataInfo).blockingFirst();
                        }

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

                        DataInfo dataInfo = dataInfoIO.read().blockingFirst();

                        boolean result = dataInfo.getAnswersInfo().removeSurvey(filename());

                        if (!result)
                            throw new Exception("Error deleting file " + filename());

                        dataInfoIO.save(dataInfo).blockingFirst();

                        return true;
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
