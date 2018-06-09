package com.puthuvaazhvu.mapping.filestorage;

import android.os.Environment;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.ThrowableWithErrorCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
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
        return Observable.just(obj)
                .observeOn(Schedulers.io())
                .map(new Function<Object, byte[]>() {
                    @Override
                    public byte[] apply(Object o) throws Exception {
                        Kryo kryo = new Kryo();

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        Output output = new Output(out);
                        kryo.writeClassAndObject(output, obj);
                        output.close();

                        return out.toByteArray();
                    }
                });
    }

    public static Observable<Object> deserialize(final byte[] data) {
        return Observable.just(data)
                .observeOn(Schedulers.io())
                .map(new Function<byte[], Object>() {
                    @Override
                    public Object apply(byte[] bytes) throws Exception {
                        Kryo kryo = new Kryo();

                        Input input = new Input(new ByteArrayInputStream(data));
                        Object o = kryo.readClassAndObject(input);
                        input.close();

                        return o;
                    }
                });
    }

    public static Observable<File> saveContentsToFile(final File file, final byte[] contents) {
        return Observable.just(true)
                .observeOn(Schedulers.io())
                .map(new Function<Boolean, File>() {
                    @Override
                    public File apply(Boolean aBoolean) throws Exception {
                        if (!file.exists()) {
                            String message = "The file " + file.getAbsolutePath() + " doesn't exits";
                            Timber.e(message);
                            throw new Exception(new ThrowableWithErrorCode(message, Constants.ErrorCodes.FILE_NOT_EXIST));
                        }

                        FileOutputStream fOut = new FileOutputStream(file);

                        fOut.write(contents);

                        fOut.flush();
                        fOut.close();

                        return file;
                    }
                });
    }

    public static Observable<File> saveContentsToFile(final File file, final String contents) {
        return Observable.just(true)
                .observeOn(Schedulers.io())
                .map(new Function<Boolean, File>() {
                    @Override
                    public File apply(Boolean aBoolean) throws Exception {
                        if (!file.exists()) {
                            String message = "The file " + file.getAbsolutePath() + " doesn't exits";
                            Timber.e(message);
                            throw new Exception(new ThrowableWithErrorCode(message, Constants.ErrorCodes.FILE_NOT_EXIST));
                        }

                        PrintWriter out = new PrintWriter(file);
                        out.println(contents);

                        out.close();

                        return file;
                    }
                });
    }

    public static Observable<byte[]> readFromFile(final File file) {
        return Observable.just(file)
                .observeOn(Schedulers.io())
                .map(new Function<File, byte[]>() {
                    @Override
                    public byte[] apply(File file) throws Exception {
                        if (!file.exists()) {
                            throw new Exception(
                                    new ThrowableWithErrorCode("File " + file.getAbsolutePath() + " is not present.",
                                            Constants.ErrorCodes.FILE_NOT_EXIST)
                            );
                        }

                        FileInputStream fin = new FileInputStream(file);

                        byte fileContent[] = new byte[(int) file.length()];

                        int length = fin.read(fileContent);

                        Timber.i("No of bytes read from " + file.getAbsolutePath() + " is " + length);

                        fin.close();

                        return fileContent;
                    }
                });
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

    public static boolean delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File child : f.listFiles())
                delete(child);
        }

        return f.delete();
    }

    public static boolean isPathAValidFile(String absolutePath) {
        File file = new File(absolutePath);
        return file.exists();
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
