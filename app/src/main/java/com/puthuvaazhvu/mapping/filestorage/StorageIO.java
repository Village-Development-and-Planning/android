package com.puthuvaazhvu.mapping.filestorage;

import java.io.File;

import io.reactivex.Observable;

import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.createFile;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.isExternalStorageReadable;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.isExternalStorageWritable;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.isPathAValidFile;

/**
 * Created by muthuveerappans on 16/02/18.
 */

public abstract class StorageIO<T> {

    public Observable<Boolean> delete() {
        if (!isPathAValidFile(getAbsolutePath()))
            return Observable.error(new Throwable("File " + getAbsolutePath() + " cannot be read."));

        File file = new File(getAbsolutePath());
        return Observable.just(file.delete());
    }

    public Observable<T> read() {
        if (!isPathAValidFile(getAbsolutePath()) || !isExternalStorageReadable())
            return Observable.error(new Throwable("File " + getAbsolutePath() + " cannot be read."));

        return read(new File(getAbsolutePath()));
    }

    public Observable<File> save(T contents) {
        File file = createFile(getAbsolutePath());

        if (file == null || !isPathAValidFile(getAbsolutePath()) || !isExternalStorageWritable())
            return Observable.error(new Throwable("File " + getAbsolutePath() + " cannot be read."));

        return save(new File(getAbsolutePath()), contents);
    }

    public abstract Observable<T> read(File file);

    public abstract Observable<File> save(File file, T contents);

    public abstract String getAbsolutePath();
}
