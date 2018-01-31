package com.puthuvaazhvu.mapping.utils;

import android.os.Environment;

import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class FileUtils {

    public static boolean fileExists(final String pathFromRoot) {
        File rootDirectory = getRootDirectory();
        if (rootDirectory == null) {
            Timber.e("Error creating root directory");
            return false;
        }

        String fullPathToFile = rootDirectory.getAbsolutePath() + pathFromRoot;

        File file = new File(fullPathToFile);

        return file.exists();
    }

    public static Observable<String> readFromPath(final String pathFromRoot) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                File rootDirectory = getRootDirectory();
                if (rootDirectory == null) {
                    Timber.e("Error creating root directory");
                    e.onError(new Throwable("Error creating root directory"));
                    return;
                }

                String fullPathToFile = rootDirectory.getAbsolutePath() + pathFromRoot;

                File file = new File(fullPathToFile);

                if (!file.exists()) {
                    Timber.e("File not present " + fullPathToFile);
                    e.onError(new Throwable("File not present " + fullPathToFile));
                    return;
                }

                e.onNext(file);
                e.onComplete();
            }
        }).flatMap(new Function<File, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(File file) throws Exception {
                return readFromFile(file);
            }
        });
    }

    public static Observable<String> readFromFile(final File file) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (!file.exists()) {
                    e.onError(new Throwable("File " + file.getAbsolutePath() + " is not present."));
                    return;
                }

                FileInputStream fin = new FileInputStream(file);
                String contents = Utils.readFromInputStream(fin);

                //Make sure to close all streams.
                fin.close();

                e.onNext(contents);
                e.onComplete();
            }
        });
    }

    public static Observable<File> saveToFile(final String pathFromRoot, final String data) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                String[] fileElements = pathFromRoot.split(File.separator);

                StringBuilder dirPathBuilder = new StringBuilder();

                File rootDirectory = getRootDirectory();
                if (rootDirectory == null) {
                    Timber.e("Error creating root directory");
                    e.onError(new Throwable("Error creating root directory"));
                    return;
                }

                dirPathBuilder.append(rootDirectory.getAbsolutePath());

                for (int i = 0; i < fileElements.length - 1; i++) {
                    dirPathBuilder.append(File.separator);
                    dirPathBuilder.append(fileElements[i]);
                }

                String dirPath = dirPathBuilder.toString();

                Timber.i("Dir formed to create is " + dirPath);

                String fileName = fileElements[fileElements.length - 1];

                // create dir if needed
                File dirToFile = createDirectory(dirPath);
                if (dirToFile == null) {
                    String msg = "Error creating dir for specified path " + pathFromRoot;
                    Timber.e(msg);
                    e.onError(new Throwable(msg));
                    return;
                }

                // create the file
                File file = createFile(dirToFile, fileName);
                if (file == null) {
                    String msg = "Error creating file for " + fileName;
                    Timber.e(msg);
                    e.onError(new Throwable(msg));
                    return;
                }

                e.onNext(file);
                e.onComplete();
            }
        }).flatMap(new Function<File, ObservableSource<File>>() {
            @Override
            public ObservableSource<File> apply(File file) throws Exception {
                return saveContentsToFile(file, data);
            }
        });

    }

    public static Observable<File> saveContentsToFile(final File file, final String contents) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                if (!file.exists()) {
                    String message = "The file " + file.getAbsolutePath() + " doesn't exits";
                    Timber.e(message);
                    e.onError(new Throwable(message));
                    return;
                }

                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter outWriter = new OutputStreamWriter(fOut);

                outWriter.append(contents);

                outWriter.close();

                fOut.flush();
                fOut.close();

                e.onNext(file);
                e.onComplete();
            }
        });
    }

    public static File createFile(File dir, String fileName) {
        try {
            if (dir != null) {
                File file = new File(dir, fileName);
                file.createNewFile();
                return file;
            } else {
                Timber.e("The dir provided is null.");
            }
        } catch (IOException e) {
            Timber.e("Error creating " + fileName + " " + e.getMessage());
        }
        return null;
    }

    public static File createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (result) {
                Timber.i("Survey data dir created successfully. " + dir.getAbsolutePath());
            } else {
                Timber.i("Failed to create Survey data dir. " + dir.getAbsolutePath());
                return null;
            }
            return dir;
        }
        return dir;
    }

    public static File getRootDirectory() {
        boolean positive = false;
        File dataDir = null;

        if (isExternalStorageReadable() && isExternalStorageWritable()) {

            File root = Environment.getExternalStorageDirectory();
            positive = root.exists();
            if (!positive) {
                positive = root.mkdir();
            }

            dataDir = new File(root, Constants.DATA_DIR);
            if (positive && !dataDir.exists()) {
                positive = dataDir.mkdir();

                if (positive) {
                    Timber.i("Created data directory at : " + dataDir.getAbsolutePath());
                }
            }

            if (!positive)
                Timber.e("Error creating data directory at : " + dataDir.getAbsolutePath());

        }

        return positive ? dataDir : null;
    }

    /* Checks if external storage is available for read and write */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
