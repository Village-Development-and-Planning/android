package com.puthuvaazhvu.mapping.filestorage.io;

import com.puthuvaazhvu.mapping.filestorage.StorageUtils;
import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.createFile;

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

    private String getAbsolutePath() {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.LOG_DIR + "/" + logID + ".txt";
    }
}
