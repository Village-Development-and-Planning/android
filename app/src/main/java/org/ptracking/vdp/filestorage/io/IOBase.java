package org.ptracking.vdp.filestorage.io;

import android.os.Environment;

import org.ptracking.vdp.filestorage.StorageUtils;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by muthuveerappans on 06/06/18.
 */

class IOBase {
    boolean isFileReadable(String path) {
        return StorageUtils.isPathAValidFile(path) && StorageUtils.isExternalStorageReadable();
    }

    boolean isFileWritable(String path) {
        return StorageUtils.isPathAValidFile(path) && StorageUtils.isExternalStorageWritable();
    }

    File root() {
        return Environment.getExternalStorageDirectory();
    }

    File getFileFromPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return StorageUtils.createFile(path);
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
