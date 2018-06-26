package org.ptracking.vdp.filestorage.io;

import org.ptracking.vdp.filestorage.StorageUtils;
import org.ptracking.vdp.other.Constants;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static org.ptracking.vdp.filestorage.StorageUtils.createFile;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public class LogIO extends IOBase {
    private String logID;

    public LogIO(String logID) {
        this.logID = logID;
    }

    public Observable<String> read() {
        return StorageUtils.readFromFile(new File(getAbsolutePath()))
                .map(new Function<byte[], String>() {
                    @Override
                    public String apply(byte[] bytes) throws Exception {
                        return new String(bytes);
                    }
                });
    }

    public Observable<File> save(String contents) {
        return StorageUtils.saveContentsToFile(createFile(getAbsolutePath()), contents);
    }

    public String getAbsolutePath() {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.LOG_DIR + "/" + logID + ".txt";
    }
}
