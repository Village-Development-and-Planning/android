package com.puthuvaazhvu.mapping.utils.storage;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/19/17.
 */

public class SaveToFile {
    public interface SaveToFileCallbacks {
        void onFileSaved();

        void onErrorWhileSaving(String message);
    }

    public static SaveToFile saveToFile;

    private SaveToFileAsync saveToFileAsync;

    private SaveToFile() {
    }

    public static SaveToFile getInstance() {
        if (saveToFile == null) {
            saveToFile = new SaveToFile();
        }
        return saveToFile;
    }

    public void execute(String toSave, File file, SaveToFileCallbacks saveToFileCallbacks) {
        if (saveToFileAsync != null) {
            saveToFileAsync.cancel(true);
        }

        saveToFileAsync = new SaveToFileAsync(file, saveToFileCallbacks);
        saveToFileAsync.execute(toSave);
    }

    private static class SaveToFileAsync extends AsyncTask<String, Void, Exception> {
        private final File fileToSave;
        private final SaveToFileCallbacks callbacks;

        public SaveToFileAsync(File fileToSave, SaveToFileCallbacks callbacks) {
            this.fileToSave = fileToSave;
            this.callbacks = callbacks;
        }

        @Override
        protected Exception doInBackground(String... params) {
            String data = params[0];

            try {
                fileToSave.createNewFile();

                FileOutputStream fOut = new FileOutputStream(fileToSave);
                OutputStreamWriter outWriter = new OutputStreamWriter(fOut);

                outWriter.append(data);

                outWriter.close();

                fOut.flush();
                fOut.close();

            } catch (IOException e) {
                Timber.e("error while saving file : " + e.getMessage());
                return e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Exception e) {
            if (e != null) {
                callbacks.onErrorWhileSaving(e.getMessage());
            } else {
                callbacks.onFileSaved();
            }
        }
    }
}
