package com.puthuvaazhvu.mapping.filestorage;

import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SnapshotsInfo;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;
import java.util.Iterator;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import timber.log.Timber;

import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.root;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public class SnapshotIO extends StorageIO<Survey> {
    private final DataInfoIO dataInfoIO;
    private final String savePath;
    private final String snapshotID;

    public SnapshotIO(String savePath, String snapshotID) {
        dataInfoIO = new DataInfoIO();
        this.savePath = savePath;
        this.snapshotID = snapshotID;
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
        if (!file.exists())
            return Observable.error(new Throwable("File " + file.getAbsolutePath() + " is not present."));

        // serialize and save to file
        return StorageUtils.serialize(contents)
                .flatMap(new Function<byte[], ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(byte[] bytes) throws Exception {
                        return StorageUtils.saveContentsToFile(file, bytes);
                    }
                })
                // read the datainfo.json
                .flatMap(new Function<File, ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(File file) throws Exception {
                        if (!file.exists())
                            throw new Exception("File " + file.getAbsolutePath() + " is not present.");

                        return dataInfoIO.read()
                                .onErrorReturnItem(new DataInfo());
                    }
                })
                // update the datainfo.json file
                .map(new Function<DataInfo, File>() {
                    @Override
                    public File apply(DataInfo dataInfo) throws Exception {

                        SnapshotsInfo.Snapshot snapshot = new SnapshotsInfo.Snapshot();
                        snapshot.setPathToLastQuestion(savePath);
                        snapshot.setTimestamp(System.currentTimeMillis());
                        snapshot.setSnapshotFileName(filename());

                        dataInfo.getSnapshotsInfo().addSnapshot(snapshot);

                        SnapshotsInfo.Survey survey =
                                dataInfo.getSnapshotsInfo()
                                        .getSurvey(snapshot.getSurveyID());

                        // delete old snapshots of the found survey
                        for (SnapshotsInfo.Snapshot s : survey.getSnapshots()) {
                            if (!s.getSnapshotFileName().equals(filename())) {
                                // delete the file
                                boolean result = delete().blockingFirst();
                                Timber.i("Deleted snapshot file "
                                        + snapshot.getSnapshotFileName() + " status " + result);
                            }
                        }

                        return file;
                    }
                });
    }

    @Override
    public Observable<File> update(Survey contents) {
        throw new IllegalArgumentException("Update operation is not permitted.");
    }

    @Override
    public Observable<Boolean> delete() {
        return super.delete()
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean deletionStatus) throws Exception {
                        if (!deletionStatus) return false;

                        // remove snapshot from datainfo.json
                        DataInfo dataInfo = dataInfoIO.read().blockingFirst();
                        SnapshotsInfo.Survey survey =
                                dataInfo.getSnapshotsInfo().getSurveyFromFileName(filename());

                        if (survey != null) {
                            Iterator<SnapshotsInfo.Snapshot> snapshotIterator = survey.getSnapshots().iterator();
                            while (snapshotIterator.hasNext()) {
                                SnapshotsInfo.Snapshot snapshot = snapshotIterator.next();
                                if (snapshot.getSnapshotFileName().equals(filename()))
                                    snapshotIterator.remove();
                            }
                        }

                        // save the new changes to datainfo.json
                        dataInfoIO.update(dataInfo).blockingFirst();

                        return true;
                    }
                });
    }

    @Override
    public String getAbsolutePath() {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.SNAPSHOTS_DIR + "/" + filename();
    }

    private String filename() {
        return snapshotID + ".json";
    }
}
