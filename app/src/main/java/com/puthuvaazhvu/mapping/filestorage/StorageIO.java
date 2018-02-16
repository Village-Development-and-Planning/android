package com.puthuvaazhvu.mapping.filestorage;

import android.os.Environment;

import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import timber.log.Timber;

import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.createFile;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.isExternalStorageReadable;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.isExternalStorageWritable;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.isPathAValidFile;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.readFromFile;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.saveContentsToFile;

/**
 * Created by muthuveerappans on 16/02/18.
 */

public abstract class StorageIO {

    public Observable<Boolean> delete(String filename) {
        if (!isPathAValidFile(getAbsolutePath(filename)))
            return Observable.error(new Throwable("File " + getAbsolutePath(filename) + " cannot be read."));

        File file = new File(getAbsolutePath(filename));
        return Observable.just(file.delete());
    }

    public Observable<byte[]> get(String filename) {
        if (!isPathAValidFile(getAbsolutePath(filename)) || !isExternalStorageReadable())
            return Observable.error(new Throwable("File " + getAbsolutePath(filename) + " cannot be read."));

        return readFromFile(new File(getAbsolutePath(filename)));
    }

    public Observable<File> update(String filename, byte[] contents) {
        if (!isExternalStorageReadable()) {
            return Observable.error(new Throwable("File " + getAbsolutePath(filename) + " cannot be read."));
        }
        if (!isPathAValidFile(getAbsolutePath(filename))) {
            return create(filename, contents);
        }

        return saveContentsToFile(new File(getAbsolutePath(filename)), contents);
    }

    public Observable<File> create(String filename, byte[] contents) {
        File file = createFile(getAbsolutePath(filename));

        if (file == null || !isPathAValidFile(getAbsolutePath(filename)) || !isExternalStorageWritable())
            return Observable.error(new Throwable("File " + getAbsolutePath(filename) + " cannot be read."));

        return saveContentsToFile(new File(getAbsolutePath(filename)), contents);
    }

    public abstract String getAbsolutePath(String filename);
}
