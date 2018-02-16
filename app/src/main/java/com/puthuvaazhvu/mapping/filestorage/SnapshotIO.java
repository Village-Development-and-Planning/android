package com.puthuvaazhvu.mapping.filestorage;

import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SnapshotsInfo;
import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;
import java.util.Iterator;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import timber.log.Timber;

import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.isPathAValidFile;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.root;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public class SnapshotIO extends StorageIO {
    private final StorageIO dataInfoIO;
    private final String savePath;

    public SnapshotIO(String savePath) {
        dataInfoIO = new DataInfoIO();
        this.savePath = savePath;
    }

    @Override
    public Observable<File> update(String filename, byte[] contents) {
        throw new IllegalArgumentException("Update operation is not permitted.");
    }

    @Override
    public Observable<File> create(final String filename, byte[] contents) {
        return
                // create the file first
                super.create(filename, contents)
                        // get the datainfo.json
                        .flatMap(new Function<File, ObservableSource<DataInfo>>() {
                            @Override
                            public ObservableSource<DataInfo> apply(File file) throws Exception {
                                return dataInfoIO.get(Constants.DATA_INFO_FILE)
                                        .onErrorReturnItem(StorageUtils.serialize(new DataInfo()).blockingFirst())
                                        .map(new Function<byte[], DataInfo>() {
                                            @Override
                                            public DataInfo apply(byte[] bytes) throws Exception {
                                                return (DataInfo) StorageUtils.deserialize(bytes).blockingFirst();
                                            }
                                        });
                            }
                        })
                        // update the datainfo.json file
                        .map(new Function<DataInfo, File>() {
                            @Override
                            public File apply(DataInfo dataInfo) throws Exception {

                                SnapshotsInfo.Snapshot snapshot = new SnapshotsInfo.Snapshot();
                                snapshot.setPathToLastQuestion(savePath);
                                snapshot.setTimestamp(System.currentTimeMillis());
                                snapshot.setSnapshotFileName(filename);

                                dataInfo.getSnapshotsInfo().addSnapshot(snapshot);

                                SnapshotsInfo.Survey survey =
                                        dataInfo.getSnapshotsInfo()
                                                .getSurvey(snapshot.getSurveyID());

                                // delete old snapshots of the found survey
                                for (SnapshotsInfo.Snapshot s : survey.getSnapshots()) {
                                    if (!s.getSnapshotFileName().equals(filename)) {
                                        // delete the file
                                        boolean result = delete(s.getSnapshotFileName()).blockingFirst();
                                        Timber.i("Deleted snapshot file "
                                                + snapshot.getSnapshotFileName() + " status " + result);
                                    }
                                }

                                return new File(getAbsolutePath(filename));
                            }
                        });
    }

    @Override
    public Observable<Boolean> delete(final String filename) {
        return super.delete(filename)
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean deletionStatus) throws Exception {
                        if (!deletionStatus) return false;

                        // remove snapshot from datainfo.json
                        DataInfo dataInfo = (DataInfo) StorageUtils.deserialize(
                                dataInfoIO.get(Constants.DATA_INFO_FILE).blockingFirst()).blockingFirst();
                        SnapshotsInfo.Survey survey =
                                dataInfo.getSnapshotsInfo().getSurveyFromFileName(filename);

                        if (survey != null) {
                            Iterator<SnapshotsInfo.Snapshot> snapshotIterator = survey.getSnapshots().iterator();
                            while (snapshotIterator.hasNext()) {
                                SnapshotsInfo.Snapshot snapshot = snapshotIterator.next();
                                if (snapshot.getSnapshotFileName().equals(filename))
                                    snapshotIterator.remove();
                            }
                        }

                        // save the new changes to datainfo.json
                        dataInfoIO.update(Constants.DATA_INFO_FILE,
                                StorageUtils.serialize(dataInfo).blockingFirst()).blockingFirst();

                        return true;
                    }
                });
    }

    @Override
    public String getAbsolutePath(String filename) {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.SNAPSHOTS_DIR + "/" + filename;
    }
}
