package com.puthuvaazhvu.mapping.filestorage;

import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.root;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public class LogIO extends StorageIO<String> {
    private String logID;

    public LogIO(String logID) {
        this.logID = logID;
    }

    @Override
    public Observable<String> read(File file) {
        return StorageUtils.readFromFile(file)
                .map(new Function<byte[], String>() {
                    @Override
                    public String apply(byte[] bytes) throws Exception {
                        return new String(bytes);
                    }
                });
    }

    @Override
    public Observable<File> save(File file, String contents) {
        return StorageUtils.saveContentsToFile(file, contents);
    }

    @Override
    public String getAbsolutePath() {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.LOG_DIR + "/" + logID + ".txt";
    }
}
