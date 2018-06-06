package com.puthuvaazhvu.mapping.filestorage.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.puthuvaazhvu.mapping.filestorage.StorageUtils;
import com.puthuvaazhvu.mapping.filestorage.modals.AnswerInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SnapshotsInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveyorInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveysInfo;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.deserialization.SurveyGsonAdapter;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.ThrowableWithErrorCode;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public class AnswerIO extends IOBase {
    private final DataInfoIO dataInfoIO;
    private final Gson gson;

    public AnswerIO() {
        dataInfoIO = new DataInfoIO();

        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Survey.class, new SurveyGsonAdapter());

        gson = gsonBuilder.create();
    }

    public Observable<Survey> read() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Observable<DataInfo> save(final Survey answer, final String surveyorCode) {
        return Observable.just(gson.toJson(answer, Survey.class))
                .flatMap(new Function<String, ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(String s) throws Exception {
                        final String answerId = answer.getId() + "_" + System.currentTimeMillis();

                        final File f = getFileFromPath(getAbsolutePath(answerId));
                        if (f == null) {
                            throw new RuntimeException("Error saving the file. The file is null.");
                        }

                        final File answerFile = StorageUtils.saveContentsToFile(f, s).blockingFirst();
                        if (!answerFile.exists()) {
                            return Observable.error(new ThrowableWithErrorCode(
                                            "File " + answerFile.getAbsolutePath() + " not exists.",
                                            Constants.ErrorCodes.ERROR_WRITING_FILE
                                    )
                            );
                        }

                        return Observable.just(answerFile)
                                .flatMap(new Function<File, ObservableSource<DataInfo>>() {
                                    @Override
                                    public ObservableSource<DataInfo> apply(File f) throws Exception {
                                        if (dataInfoIO.isExists()) {
                                            return dataInfoIO.read();
                                        } else {
                                            throw new RuntimeException("The" + Constants.DATA_INFO_FILE + " is not present.");
                                        }
                                    }
                                }).flatMap(new Function<DataInfo, ObservableSource<DataInfo>>() {
                                    @Override
                                    public ObservableSource<DataInfo> apply(final DataInfo dataInfo) throws Exception {
                                        final SurveyorInfo surveyorInfo = dataInfo.getSurveyorInfo(surveyorCode);
                                        if (surveyorInfo == null) {
                                            throw new RuntimeException("The surveyor info is null for " + surveyorCode);
                                        }

                                        // remove all the snapshots of the surveyID
                                        ArrayList<Observable<DataInfo>> dataInfoObservables = new ArrayList<>();

                                        SnapshotsInfo.Survey s
                                                = surveyorInfo.getSnapshotsInfo().getSurvey(answer.getId());

                                        if (s != null) {
                                            Iterator<SnapshotsInfo.Snapshot> snapshotIterator
                                                    = s.getSnapshots().iterator();

                                            while (snapshotIterator.hasNext()) {
                                                SnapshotsInfo.Snapshot snapshot = snapshotIterator.next();
                                                SnapshotIO snapshotIO = new SnapshotIO();
                                                dataInfoObservables.add(snapshotIO.delete(snapshot.getSnapshotID(), surveyorCode));
                                                snapshotIterator.remove();
                                            }

                                        }

                                        return Observable.merge(dataInfoObservables)
                                                .flatMap(new Function<DataInfo, ObservableSource<DataInfo>>() {
                                                    @Override
                                                    public ObservableSource<DataInfo> apply(DataInfo dataInfo) throws Exception {
                                                        AnswerInfo.Answer answerInfo = new AnswerInfo.Answer();
                                                        answerInfo.setSurveyID(answer.getId());
                                                        answerInfo.setSurveyName(answer.getName());
                                                        answerInfo.setTimeStamp(System.currentTimeMillis());
                                                        answerInfo.setAnswerID(answerId);

                                                        surveyorInfo.getAnswersInfo().getAnswers().add(answerInfo);

                                                        return dataInfoIO.save(dataInfo)
                                                                .flatMap(new Function<File, ObservableSource<DataInfo>>() {
                                                                    @Override
                                                                    public ObservableSource<DataInfo> apply(File file) throws Exception {
                                                                        return dataInfoIO.read();
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    public Observable<DataInfo> delete(final String answerId, final String surveyorCode) {
        if (!isFileReadable(getAbsolutePath(answerId))) {
            return Observable.error(new ThrowableWithErrorCode(
                            "File " + getAbsolutePath(answerId) + " cannot be read.",
                            Constants.ErrorCodes.ERROR_READING_FILE
                    )
            );
        }

        File file = new File(getAbsolutePath(answerId));

        return Observable.just(file.delete())
                .flatMap(new Function<Boolean, ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(Boolean aBoolean) throws Exception {
                        if (!aBoolean)
                            throw new Exception(
                                    new ThrowableWithErrorCode("Failed to delete the file " + filename(answerId),
                                            Constants.ErrorCodes.ERROR_DELETING_FILE)
                            );

                        return dataInfoIO.read()
                                .flatMap(new Function<DataInfo, ObservableSource<DataInfo>>() {
                                    @Override
                                    public ObservableSource<DataInfo> apply(final DataInfo dataInfo) throws Exception {
                                        SurveyorInfo surveyorInfo = dataInfo.getSurveyorInfo(surveyorCode);
                                        if (surveyorInfo != null) {
                                            boolean result = surveyorInfo.getAnswersInfo().removeAnswer(answerId);
                                            Timber.i("Remove answer " + answerId + " from " + Constants.DATA_INFO_FILE + " status: " + result);

                                            return dataInfoIO.save(dataInfo).map(new Function<File, DataInfo>() {
                                                @Override
                                                public DataInfo apply(File file) throws Exception {
                                                    return dataInfo;
                                                }
                                            });
                                        }

                                        return Observable.just(dataInfo);
                                    }
                                });
                    }
                });
    }

    public File getFile(String answerId) {
        return StorageUtils.createFile(getAbsolutePath(answerId));
    }

    private String getAbsolutePath(String answerId) {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.ANSWER_DIR + "/" + filename(answerId);
    }

    private String filename(String answerId) {
        return answerId + ".json";
    }

}
