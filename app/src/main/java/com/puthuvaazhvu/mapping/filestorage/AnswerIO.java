package com.puthuvaazhvu.mapping.filestorage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.filestorage.modals.AnswerInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SnapshotsInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveysInfo;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.deserialization.SurveyGsonAdapter;
import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;
import java.util.Iterator;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.root;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public class AnswerIO extends StorageIO<Survey> {
    private final DataInfoIO dataInfoIO;

    final Gson gson;
    private final String answerID;

    public AnswerIO(String answerId) {
        dataInfoIO = new DataInfoIO();

        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Survey.class, new SurveyGsonAdapter());

        gson = gsonBuilder.create();

        this.answerID = answerId;
    }

    @Override
    public Observable<Survey> read(File file) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Observable<File> save(final File file, final Survey survey) {
        return Observable.just(gson.toJson(survey, Survey.class))
                .map(new Function<String, File>() {
                    @Override
                    public File apply(String c) throws Exception {
                        File f = StorageUtils.saveContentsToFile(file, c).blockingFirst();

                        DataInfo dataInfo = dataInfoIO.read()
                                .onErrorReturnItem(new DataInfo())
                                .blockingFirst();

                        AnswerInfo.Answer answer = new AnswerInfo.Answer();
                        answer.setSurveyID(survey.getId());
                        answer.setSurveyName(survey.getName());
                        answer.setTimeStamp(System.currentTimeMillis());
                        answer.setAnswerID(answerID);

                        dataInfo.getAnswersInfo().getAnswers().add(answer);

                        // remove all the snapshots of the surveyID

                        SnapshotsInfo.Survey s
                                = dataInfo.getSnapshotsInfo().getSurvey(survey.getId());

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

                        }

                        dataInfoIO.save(dataInfo).blockingFirst();

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

                        boolean result = dataInfo.getAnswersInfo().removeAnswer(answerID);

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
