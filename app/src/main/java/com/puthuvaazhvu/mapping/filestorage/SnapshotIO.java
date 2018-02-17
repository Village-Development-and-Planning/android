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

    public SnapshotIO(String snapshotID) {
        this.dataInfoIO = new DataInfoIO();
        this.snapshotID = snapshotID;
        this.savePath = "";
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
        if (!file.exists())
            return Observable.error(new Throwable("File " + file.getAbsolutePath() + " is not present."));

        if (savePath == null || savePath.isEmpty())
            return Observable.error(new Throwable("Snapshot path is empty."));

        // serialize and save to file
        return StorageUtils.serialize(contents)
                .map(new Function<byte[], File>() {
                    @Override
                    public File apply(byte[] bytes) throws Exception {
                        File f = StorageUtils.saveContentsToFile(file, bytes).blockingFirst();

                        if (!file.exists())
                            throw new Exception("File " + file.getAbsolutePath() + " is not present.");

                        DataInfo dataInfo = dataInfoIO.read()
                                .onErrorReturnItem(new DataInfo()).blockingFirst();

                        SnapshotsInfo.Snapshot snapshot = new SnapshotsInfo.Snapshot();
                        snapshot.setPathToLastQuestion(savePath);
                        snapshot.setTimestamp(System.currentTimeMillis());
                        snapshot.setSnapshotID(snapshotID);

                        dataInfo.getSnapshotsInfo().addSnapshot(snapshot);

                        SnapshotsInfo.Survey survey =
                                dataInfo.getSnapshotsInfo()
                                        .getSurvey(snapshot.getSurveyID());

                        // delete old snapshots of the found survey
                        for (SnapshotsInfo.Snapshot s : survey.getSnapshots()) {
                            if (!s.getSnapshotID().equals(snapshotID)) {
                                // delete the file
                                boolean result = delete().blockingFirst();
                                Timber.i("Deleted snapshot file "
                                        + snapshot.getSnapshotID() + " status " + result);
                            }
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
                        if (!deletionStatus) return false;

                        // remove snapshot from datainfo.json
                        DataInfo dataInfo = dataInfoIO.read().blockingFirst();
                        SnapshotsInfo.Survey survey =
                                dataInfo.getSnapshotsInfo().getSurveyFromFileName(snapshotID);

                        if (survey != null) {
                            Iterator<SnapshotsInfo.Snapshot> snapshotIterator = survey.getSnapshots().iterator();
                            while (snapshotIterator.hasNext()) {
                                SnapshotsInfo.Snapshot snapshot = snapshotIterator.next();
                                if (snapshot.getSnapshotID().equals(snapshotID))
                                    snapshotIterator.remove();
                            }
                        }

                        // save the new changes to datainfo.json
                        dataInfoIO.save(dataInfo).blockingFirst();

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
