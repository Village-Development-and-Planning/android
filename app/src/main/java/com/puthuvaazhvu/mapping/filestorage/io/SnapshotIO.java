package com.puthuvaazhvu.mapping.filestorage.io;

import com.puthuvaazhvu.mapping.filestorage.StorageUtils;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SnapshotsInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveyorData;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.ThrowableWithErrorCode;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
                .observeOn(Schedulers.io())
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
                .observeOn(Schedulers.io())
                .flatMap(new Function<byte[], ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(final byte[] bytes) throws Exception {
                        return clearOldSnapshots(surveyorCode, contents.getId())
                                .flatMap(new Function<DataInfo, ObservableSource<DataInfo>>() {
                                    @Override
                                    public ObservableSource<DataInfo> apply(final DataInfo dataInfo) throws Exception {
                                        final String snapshotId = contents.getId() + "_" + System.currentTimeMillis();

                                        final File f = getFileFromPath(getAbsolutePath(snapshotId));
                                        if (f == null) {
                                            throw new RuntimeException("Error saving the file. The file is null.");
                                        }

                                        // save the new snapshot
                                        return StorageUtils.saveContentsToFile(f, bytes)
                                                .flatMap(new Function<File, ObservableSource<DataInfo>>() {
                                                    @Override
                                                    public ObservableSource<DataInfo> apply(File file) throws Exception {
                                                        if (!file.exists()) {
                                                            return Observable.error(new ThrowableWithErrorCode(
                                                                            "File " + file.getAbsolutePath() + " not exists.",
                                                                            Constants.ErrorCodes.ERROR_WRITING_FILE
                                                                    )
                                                            );
                                                        }

                                                        final SurveyorData surveyorData = dataInfo.getSurveyorData(surveyorCode);

                                                        // update in datainfo
                                                        SnapshotsInfo.Snapshot snapshot = new SnapshotsInfo.Snapshot();
                                                        snapshot.setPathToLastQuestion(snapshotPath);
                                                        snapshot.setTimestamp(System.currentTimeMillis());
                                                        snapshot.setSnapshotID(snapshotId);

                                                        surveyorData.getSnapshotsInfo().addSnapshot(snapshot, contents.getId(), contents.getName());

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
                .observeOn(Schedulers.io())
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
                                        SurveyorData surveyorData = dataInfo.getSurveyorData(surveyorCode);
                                        if (surveyorData != null) {
                                            SnapshotsInfo.Survey survey =
                                                    surveyorData.getSnapshotsInfo().getSurveyFromFileName(snapshotId);

                                            if (survey != null) {
                                                Iterator<SnapshotsInfo.Snapshot> snapshotIterator = survey.getSnapshots().iterator();
                                                while (snapshotIterator.hasNext()) {
                                                    SnapshotsInfo.Snapshot snapshot = snapshotIterator.next();
                                                    if (snapshot.getSnapshotID().equals(snapshotId))
                                                        snapshotIterator.remove();
                                                }
                                            }

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

    public Observable<DataInfo> clearOldSnapshots(final String surveyorCode, final String surveyId) {
        return Observable.just(true)
                .flatMap(new Function<Boolean, ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(Boolean b) throws Exception {
                        if (dataInfoIO.isExists()) {
                            return dataInfoIO.read();
                        } else {
                            throw new RuntimeException("The" + Constants.DATA_INFO_FILE + " is not present.");
                        }
                    }
                }).flatMap(new Function<DataInfo, ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(DataInfo dataInfo) throws Exception {
                        SurveyorData surveyorData = dataInfo.getSurveyorData(surveyorCode);
                        if (surveyorData == null) {
                            throw new RuntimeException("The surveyor info is null for " + surveyorCode);
                        }

                        SnapshotsInfo.Survey survey =
                                surveyorData.getSnapshotsInfo()
                                        .getSurvey(surveyId);

                        ArrayList<Observable<DataInfo>> dataInfoObservables = new ArrayList<>();

                        // delete old snapshots of the found survey
                        if (survey != null) {
                            for (SnapshotsInfo.Snapshot s : survey.getSnapshots()) {
                                // delete the file
                                SnapshotIO snapshotIO = new SnapshotIO();
                                dataInfoObservables.add(snapshotIO.delete(s.getSnapshotID(), surveyorCode));
                            }
                        }

                        return
                                (
                                        dataInfoObservables.isEmpty() ?
                                                Observable.just(true) : Observable.merge(dataInfoObservables)
                                ).flatMap(new Function<Object, ObservableSource<DataInfo>>() {
                                    @Override
                                    public ObservableSource<DataInfo> apply(Object o) throws Exception {
                                        return dataInfoIO.read();
                                    }
                                });
                    }
                });
    }

    public Observable<Boolean> deleteAll() {
        return deleteAll(getDir());
    }

    private String getDir() {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.SNAPSHOTS_DIR;
    }

    private String getAbsolutePath(String snapshotId) {
        return getDir() + "/" + filename(snapshotId);
    }

    private String filename(String snapshotId) {
        return snapshotId + ".bytes";
    }
}
