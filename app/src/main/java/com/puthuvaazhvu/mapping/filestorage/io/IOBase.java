package com.puthuvaazhvu.mapping.filestorage.io;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.createFile;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.isExternalStorageReadable;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.isExternalStorageWritable;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.isPathAValidFile;

/**
 * Created by muthuveerappans on 06/06/18.
 */

public class IOBase {
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
}
