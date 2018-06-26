package org.ptracking.vdp.filestorage.io;

import org.ptracking.vdp.filestorage.StorageUtils;
import org.ptracking.vdp.filestorage.modals.DataInfo;
import org.ptracking.vdp.filestorage.modals.SurveyorData;
import org.ptracking.vdp.filestorage.modals.SurveysInfo;
import org.ptracking.vdp.modals.Survey;
import org.ptracking.vdp.other.Constants;
import org.ptracking.vdp.utils.ThrowableWithErrorCode;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public class SurveyIO extends IOBase {
    private final DataInfoIO dataInfoIO;

    public SurveyIO() {
        dataInfoIO = new DataInfoIO();
    }

    public Observable<Survey> read(String surveyId) {
        if (!isFileReadable(getAbsolutePath(surveyId))) {
            return Observable.error(new ThrowableWithErrorCode(
                            "File " + getAbsolutePath(surveyId) + " cannot be read.",
                            Constants.ErrorCodes.ERROR_READING_FILE
                    )
            );
        }

        return StorageUtils.readFromFile(new File(getAbsolutePath(surveyId)))
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

    public Observable<DataInfo> save(final Survey contents, final String surveyorCode) {
        return StorageUtils.serialize(contents)
                .observeOn(Schedulers.io())
                .flatMap(new Function<byte[], ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(byte[] bytes) throws Exception {
                        final File f = getFileFromPath(getAbsolutePath(contents.getId()));
                        if (f == null) {
                            throw new RuntimeException("Error saving the file. The file is null.");
                        }

                        final File surveyFile = StorageUtils.saveContentsToFile(f, bytes).blockingFirst();
                        if (!surveyFile.exists()) {
                            return Observable.error(new ThrowableWithErrorCode(
                                            "File " + surveyFile.getAbsolutePath() + " not exists.",
                                            Constants.ErrorCodes.ERROR_WRITING_FILE
                                    )
                            );
                        }

                        return Observable.just(surveyFile)
                                .flatMap(new Function<File, ObservableSource<DataInfo>>() {
                                    @Override
                                    public ObservableSource<DataInfo> apply(File f) throws Exception {
                                        if (dataInfoIO.isExists()) {
                                            return dataInfoIO.read();
                                        } else {
                                            return Observable.just(new DataInfo());
                                        }
                                    }
                                }).flatMap(new Function<DataInfo, ObservableSource<DataInfo>>() {
                                    @Override
                                    public ObservableSource<DataInfo> apply(final DataInfo dataInfo) throws Exception {
                                        SurveyorData surveyorData = dataInfo.getSurveyorData(surveyorCode);
                                        if (surveyorData == null) {
                                            surveyorData = new SurveyorData();
                                            dataInfo.addSurveyorInfoToMap(surveyorCode, surveyorData);
                                        }

                                        SurveysInfo.Survey survey = new SurveysInfo.Survey();
                                        survey.setFilename(filename(contents.getId()));
                                        survey.setSurveyID(contents.getId());
                                        survey.setSurveyName(contents.getName());
                                        survey.setTimeStamp(System.currentTimeMillis());

                                        surveyorData.getSurveysInfo().addSurvey(survey);

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

    public Observable<DataInfo> delete(final String surveyId, final String surveyorCode) {
        if (!isFileReadable(getAbsolutePath(surveyId))) {
            return Observable.error(new ThrowableWithErrorCode(
                            "File " + getAbsolutePath(surveyId) + " cannot be read.",
                            Constants.ErrorCodes.ERROR_READING_FILE
                    )
            );
        }

        File file = new File(getAbsolutePath(surveyId));

        return Observable.just(file.delete())
                .observeOn(Schedulers.io())
                .flatMap(new Function<Boolean, ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(Boolean aBoolean) throws Exception {
                        if (!aBoolean)
                            throw new Exception(
                                    new ThrowableWithErrorCode("Failed to delete the file " + filename(surveyId),
                                            Constants.ErrorCodes.ERROR_DELETING_FILE)
                            );

                        return dataInfoIO.read()
                                .flatMap(new Function<DataInfo, ObservableSource<DataInfo>>() {
                                    @Override
                                    public ObservableSource<DataInfo> apply(final DataInfo dataInfo) throws Exception {
                                        SurveyorData surveyorData = dataInfo.getSurveyorData(surveyorCode);
                                        if (surveyorData != null) {
                                            boolean result = surveyorData.getSurveysInfo().removeSurvey(surveyId);
                                            Timber.i("Remove survey " + surveyId + " from " + Constants.DATA_INFO_FILE + " status: " + result);

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


    public Observable<Boolean> deleteAll() {
        return deleteAll(getDir());
    }

    private String getDir() {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.SURVEY_DIR;
    }

    private String getAbsolutePath(String surveyId) {
        return getDir() + "/" + filename(surveyId);
    }

    private String filename(String surveyId) {
        return surveyId + ".bytes";
    }
}
