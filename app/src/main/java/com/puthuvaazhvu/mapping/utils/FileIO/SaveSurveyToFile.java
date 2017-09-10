package com.puthuvaazhvu.mapping.utils.FileIO;

import android.os.AsyncTask;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

/**
 * Created by muthuveerappans on 9/9/17.
 */

public class SaveSurveyToFile implements SaveOperation {
    JsonObject jsonObject;
    File file;
    SaveOperationCallback callback;
    SaveAsync saveAsync;

    public SaveSurveyToFile(JsonObject jsonObject, File file, SaveOperationCallback callback) {
        this.jsonObject = jsonObject;
        this.file = file;
        this.callback = callback;
    }

    @Override
    public void save() {
        cancel();
        saveAsync = new SaveAsync(callback, file);
        saveAsync.execute(jsonObject);
    }

    @Override
    public void cancel() {
        if (saveAsync != null) {
            saveAsync.cancel(true);
            saveAsync = null;
        }
    }

    @Override
    public File getSavedFile() {
        return file;
    }

    private static class SaveAsync extends AsyncTask<JsonObject, Void, Exception> {
        SaveOperationCallback saveOperationCallback;
        File file;

        public SaveAsync(SaveOperationCallback saveOperationCallback, File file) {
            this.saveOperationCallback = saveOperationCallback;
            this.file = file;
        }

        @Override
        protected Exception doInBackground(JsonObject... jsonObjects) {
            Exception error = null;
            try {
                JsonObject jsonObject = jsonObjects[0];
                String jsonString = jsonObject.toString();
                PrintWriter printWriter = new PrintWriter(file);
                printWriter.println(jsonString);
            } catch (IOException e) {
                error = e;
            }
            return error;
        }

        @Override
        protected void onPostExecute(Exception e) {
            if (e != null) {
                saveOperationCallback.error(e.getMessage());
            } else {
                saveOperationCallback.done();
            }
        }
    }
}
