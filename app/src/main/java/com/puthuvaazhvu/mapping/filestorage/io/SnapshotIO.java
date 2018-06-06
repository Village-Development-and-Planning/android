package com.puthuvaazhvu.mapping.filestorage.io;

import com.puthuvaazhvu.mapping.filestorage.StorageUtils;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SnapshotsInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveyorInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveysInfo;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.ThrowableWithErrorCode;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public class SnapshotIO extends IOBase {
    private final DataInfoIO dataInfoIO;

    public SnapshotIO() {
        this.dataInfoIO = new DataInfoIO();
    }

    public Observable<Survey> read(String snapshotId) {
        if (!isFileReadable(getAbsolutePath(snapshotId))) {
            return Observable.error(new ThrowableWithErrorCode(
                            "File " + getAbsolutePath(snapshotId) + " cannot be read.",
                            Constants.ErrorCodes.ERROR_READING_FILE
                    )
            );
        }

        return StorageUtils.readFromFile(new File(getAbsolutePath(snapshotId)))
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

    public Observable<DataInfo> save(final Survey contents, final String surveyorCode, final String snapshotPath) {
        return StorageUtils.serialize(contents)
                .flatMap(new Function<byte[], ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(byte[] bytes) throws Exception {
                        final String snapshotId = contents.getId() + "_" + System.currentTimeMillis();

                        final File f = getFileFromPath(getAbsolutePath(snapshotId));
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

                                        SnapshotsInfo.Survey survey =
                                                surveyorInfo.getSnapshotsInfo()
                                                        .getSurvey(contents.getId());

                                        ArrayList<Observable<DataInfo>> dataInfoObservables = new ArrayList<>();

                                        // delete old snapshots of the found survey
                                        if (survey != null) {
                                            for (SnapshotsInfo.Snapshot s : survey.getSnapshots()) {
                                                // delete the file
                                                SnapshotIO snapshotIO = new SnapshotIO();
                                                dataInfoObservables.add(snapshotIO.delete(s.getSnapshotID(), surveyorCode));
                                            }
                                        }

                                        return Observable.merge(dataInfoObservables)
                                                .flatMap(new Function<Object, ObservableSource<DataInfo>>() {
                                                    @Override
                                                    public ObservableSource<DataInfo> apply(Object o) throws Exception {
                                                        return dataInfoIO.read();
                                                    }
                                                }).flatMap(new Function<Object, ObservableSource<DataInfo>>() {
                                                    @Override
                                                    public ObservableSource<DataInfo> apply(Object o) throws Exception {
                                                        SnapshotsInfo.Snapshot snapshot = new SnapshotsInfo.Snapshot();
                                                        snapshot.setPathToLastQuestion(snapshotPath);
                                                        snapshot.setTimestamp(System.currentTimeMillis());
                                                        snapshot.setSnapshotID(snapshotId);

                                                        surveyorInfo.getSnapshotsInfo().addSnapshot(snapshot, contents.getId(), contents.getName());

                                                        return dataInfoIO.save(dataInfo)
                                                                .map(new Function<File, DataInfo>() {
                                                                    @Override
                                                                    public DataInfo apply(File file) throws Exception {
                                                                        return dataInfo;
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    public Observable<DataInfo> delete(final String snapshotId, final String surveyorCode) {
        if (!isFileReadable(getAbsolutePath(snapshotId))) {
            return Observable.error(new ThrowableWithErrorCode(
                            "File " + getAbsolutePath(snapshotId) + " cannot be read.",
                            Constants.ErrorCodes.ERROR_READING_FILE
                    )
            );
        }

        File file = new File(getAbsolutePath(snapshotId));

        return Observable.just(file.delete())
                .flatMap(new Function<Boolean, ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(Boolean aBoolean) throws Exception {
                        if (!aBoolean)
                            throw new Exception(
                                    new ThrowableWithErrorCode("Failed to delete the file " + filename(snapshotId),
                                            Constants.ErrorCodes.ERROR_DELETING_FILE)
                            );

                        return dataInfoIO.read()
                                .flatMap(new Function<DataInfo, ObservableSource<DataInfo>>() {
                                    @Override
                                    public ObservableSource<DataInfo> apply(final DataInfo dataInfo) throws Exception {
                                        SurveyorInfo surveyorInfo = dataInfo.getSurveyorInfo(surveyorCode);
                                        if (surveyorInfo != null) {
                                            SnapshotsInfo.Survey survey =
                                                    surveyorInfo.getSnapshotsInfo().getSurveyFromFileName(snapshotId);

                                            if (survey != null) {
                                                Iterator<SnapshotsInfo.Snapshot> snapshotIterator = survey.getSnapshots().iterator();
                                                while (snapshotIterator.hasNext()) {
                                                    SnapshotsInfo.Snapshot snapshot = snapshotIterator.next();
                                                    if (snapshot.getSnapshotID().equals(snapshotId))
                                                        snapshotIterator.remove();
                                                }
                                            }

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

    private String getAbsolutePath(String snapshotId) {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.SNAPSHOTS_DIR + "/" + filename(snapshotId);
    }

    private String filename(String snapshotId) {
        return snapshotId + ".bytes";
    }
}
