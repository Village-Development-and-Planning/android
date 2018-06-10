package com.puthuvaazhvu.mapping.filestorage.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.puthuvaazhvu.mapping.filestorage.StorageUtils;
import com.puthuvaazhvu.mapping.filestorage.modals.AnswerInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SnapshotsInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveyorData;
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
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public class AnswerIO extends IOBase {
    private final DataInfoIO dataInfoIO;
    private final SnapshotIO snapshotIO;

    private final Gson gson;

    public AnswerIO() {
        dataInfoIO = new DataInfoIO();
        snapshotIO = new SnapshotIO();

        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Survey.class, new SurveyGsonAdapter());

        gson = gsonBuilder.create();
    }

    public Observable<DataInfo> save(final Survey answer, final String surveyorCode) {
        final String answerId = answer.getId() + "_" + System.currentTimeMillis();

        return Observable.just(true)
                .observeOn(Schedulers.io())
                .map(new Function<Boolean, String>() {
                    @Override
                    public String apply(Boolean aBoolean) throws Exception {
                        return gson.toJson(answer, Survey.class);
                    }
                })
                // save answer to file system
                .flatMap(new Function<String, ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(String s) throws Exception {
                        final File f = getFileFromPath(getAbsolutePath(answerId));
                        if (f == null) {
                            throw new RuntimeException("Error saving the file. The file is null.");
                        }

                        return StorageUtils.saveContentsToFile(f, s).flatMap(new Function<File, ObservableSource<DataInfo>>() {
                            @Override
                            public ObservableSource<DataInfo> apply(File file) throws Exception {
                                if (!file.exists()) {
                                    throw new Exception(new ThrowableWithErrorCode(
                                            "File " + file.getAbsolutePath() + " not exists.",
                                            Constants.ErrorCodes.ERROR_WRITING_FILE
                                    ));
                                } else {
                                    return dataInfoIO.read();
                                }
                            }
                        });
                    }
                })
                // modify the datainfo file
                .flatMap(new Function<DataInfo, ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(DataInfo dataInfo) throws Exception {
                        return snapshotIO.clearOldSnapshots(surveyorCode, answer.getId())
                                .flatMap(new Function<DataInfo, ObservableSource<DataInfo>>() {
                                    @Override
                                    public ObservableSource<DataInfo> apply(DataInfo dataInfo) throws Exception {
                                        final SurveyorData surveyorData = dataInfo.getSurveyorData(surveyorCode);

                                        AnswerInfo.Answer answerInfo = new AnswerInfo.Answer();
                                        answerInfo.setSurveyID(answer.getId());
                                        answerInfo.setSurveyName(answer.getName());
                                        answerInfo.setTimeStamp(System.currentTimeMillis());
                                        answerInfo.setAnswerID(answerId);

                                        surveyorData.getAnswersInfo().getAnswers().add(answerInfo);

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

    public Observable<DataInfo> delete(final String answerId, final String surveyorCode) {
        return deleteFile(answerId)
                .observeOn(Schedulers.io())
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
                                        SurveyorData surveyorData = dataInfo.getSurveyorData(surveyorCode);
                                        if (surveyorData != null) {
                                            boolean result = surveyorData.getAnswersInfo().removeAnswer(answerId);
                                            Timber.i("Remove answer " + answerId + " from " + Constants.DATA_INFO_FILE + " status: " + result);

                                            return dataInfoIO.save(dataInfo).flatMap(new Function<File, ObservableSource<DataInfo>>() {
                                                @Override
                                                public ObservableSource<DataInfo> apply(File file) throws Exception {
                                                    return dataInfoIO.read();
                                                }
                                            });
                                        }

                                        return Observable.just(dataInfo);
                                    }
                                });
                    }
                });
    }

    public Observable<Boolean> deleteFile(String answerId) {
        if (!isFileReadable(getAbsolutePath(answerId))) {
            return Observable.error(new ThrowableWithErrorCode(
                            "File " + getAbsolutePath(answerId) + " cannot be read.",
                            Constants.ErrorCodes.ERROR_READING_FILE
                    )
            );
        }

        File file = new File(getAbsolutePath(answerId));

        return Observable.just(file.delete());
    }

    public File getFile(String answerId) {
        return new File(getAbsolutePath(answerId));
    }

    private String getAbsolutePath(String answerId) {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.ANSWER_DIR + "/" + filename(answerId);
    }

    private String filename(String answerId) {
        return answerId + ".json";
    }

}
