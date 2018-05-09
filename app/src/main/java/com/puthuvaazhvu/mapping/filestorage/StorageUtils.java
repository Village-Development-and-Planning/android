package com.puthuvaazhvu.mapping.filestorage;

import android.os.Environment;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.puthuvaazhvu.mapping.modals.Survey;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public final class StorageUtils {

    public static Observable<List<File>> getFilesFromDir(File dir) {
        if (dir.exists()) {
            return Observable.just(Arrays.asList(dir.listFiles()));
        } else {
            return Observable.error(new Throwable("Directory " + dir.getAbsolutePath() + " not exists."));
        }
    }

    public static Observable<byte[]> serialize(final Object obj) {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> e) throws Exception {
                Kryo kryo = new Kryo();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Output output = new Output(out);
                kryo.writeClassAndObject(output, obj);
                output.close();

                e.onNext(out.toByteArray());
                e.onComplete();
            }
        });
    }

    public static Observable<Object> deserialize(final byte[] data) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                Kryo kryo = new Kryo();

                Input input = new Input(new ByteArrayInputStream(data));
                Object o = kryo.readClassAndObject(input);
                input.close();

                e.onNext(o);
                e.onComplete();
            }
        });
    }

    public static Observable<File> saveContentsToFile(final File file, final byte[] contents) {
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

                fOut.write(contents);

                fOut.flush();
                fOut.close();

                e.onNext(file);
                e.onComplete();
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

                PrintWriter out = new PrintWriter(file);
                out.println(contents);

                out.close();

                e.onNext(file);
                e.onComplete();
            }
        });
    }

    public static Observable<byte[]> readFromFile(final File file) {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> emitter) throws Exception {
                if (!file.exists()) {
                    emitter.onError(new Throwable("File " + file.getAbsolutePath() + " is not present."));
                    return;
                }

                FileInputStream fin = new FileInputStream(file);

                byte fileContent[] = new byte[(int) file.length()];

                int length = fin.read(fileContent);

                Timber.i("No of bytes read from " + file.getAbsolutePath() + " is " + length);

                fin.close();

                emitter.onNext(fileContent);
                emitter.onComplete();
            }
        });
    }

    public static File createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (result) {
                Timber.i("Survey data dir created successfully. " + dir.getAbsolutePath());
            } else {
                Timber.i("Failed to save Survey data dir. " + dir.getAbsolutePath());
                return null;
            }
            return dir;
        }
        return dir;
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

    public static File createFile(String absolutePathToFile) {
        try {
            boolean result = false;

            String dirs = absolutePathToFile.substring(0, absolutePathToFile.lastIndexOf("/"));

            File outFile = new File(dirs);
            if (outFile.exists()) {
                result = true;
            } else {
                result = outFile.mkdirs();
            }

            if (result) {
                outFile = new File(absolutePathToFile);
                result = outFile.createNewFile();
            }

            if (!result) {
                Timber.e("Error creating the file " + absolutePathToFile);
                return null;
            }

            return outFile;
        } catch (IOException e) {
            Timber.e(e);
            return null;
        }
    }

    public static void deleteDir(File dir) throws IOException {
        if (dir.isDirectory())
            for (File child : dir.listFiles())
                deleteDir(child);

        dir.delete();
    }

    public static boolean isPathAValidFile(String absolutePath) {
        File file = new File(absolutePath);
        boolean result = file.exists();
        return result;
    }

    public static File root() {
        return Environment.getExternalStorageDirectory();
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
