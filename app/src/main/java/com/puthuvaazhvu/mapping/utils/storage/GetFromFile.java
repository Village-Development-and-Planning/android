package com.puthuvaazhvu.mapping.utils.storage;

import android.os.AsyncTask;

import com.puthuvaazhvu.mapping.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class GetFromFile {
    public interface GetFromFilesCallbacks {
        void onFileContentsLoaded(String contents);

        void onErrorWhileRetrieving(String message);
    }

    public static GetFromFile getFromFile;

    private Async async;

    private GetFromFile() {
    }

    public static GetFromFile getInstance() {
        if (getFromFile == null) {
            getFromFile = new GetFromFile();
        }

        return getFromFile;
    }

    public void execute(File file, GetFromFilesCallbacks getFromFilesCallbacks) {
        if (async != null) {
            async.cancel(true);
        }

        async = new Async(getFromFilesCallbacks);
        async.execute(file);
    }

    public Callable<String> execute(final File file) {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getFileContents(file);
            }
        };
    }

    public String executeSynchronous(File file) throws IOException {
        return getFileContents(file);
    }

    public static String getFileContents(final File file) throws IOException {
        FileInputStream fin = new FileInputStream(file);
        String contents = Utils.readFromInputStream(fin);
        //Make sure to close all streams.
        fin.close();
        return contents;
    }

    private static class Async extends AsyncTask<File, Void, String> {
        private final GetFromFilesCallbacks callbacks;

        public Async(GetFromFilesCallbacks callbacks) {
            this.callbacks = callbacks;
        }

        @Override
        protected String doInBackground(File... params) {
            try {
                return getFileContents(params[0]);
            } catch (IOException e) {
                Timber.e("Error reading file. " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            if (string == null) {
                callbacks.onErrorWhileRetrieving("Error reading contents from the file.");
            } else {
                callbacks.onFileContentsLoaded(string);
            }
        }
    }
}
