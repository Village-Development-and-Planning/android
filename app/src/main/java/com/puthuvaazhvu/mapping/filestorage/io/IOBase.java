package com.puthuvaazhvu.mapping.filestorage.io;

import android.os.Environment;

import com.puthuvaazhvu.mapping.filestorage.StorageUtils;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.createFile;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.isExternalStorageReadable;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.isExternalStorageWritable;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.isPathAValidFile;

/**
 * Created by muthuveerappans on 06/06/18.
 */

class IOBase {
    boolean isFileReadable(String path) {
        return isPathAValidFile(path) && isExternalStorageReadable();
    }

    boolean isFileWritable(String path) {
        return isPathAValidFile(path) && isExternalStorageWritable();
    }

    File root() {
        return Environment.getExternalStorageDirectory();
    }

    File getFileFromPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return createFile(path);
        }
        return file;
    }

    protected Observable<Boolean> deleteAll(final String absolutePath) {
        return Observable.just(true)
                .observeOn(Schedulers.io())
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean) throws Exception {
                        return StorageUtils.delete(
                                new File(absolutePath)
                        );
                    }
                });
    }
}
