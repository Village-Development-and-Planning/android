package com.puthuvaazhvu.mapping.utils.ObjectToFromDisk;

import android.os.AsyncTask;
import android.util.Log;

import com.puthuvaazhvu.mapping.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SaveToDiskAsync extends AsyncTask<QuestionOutput, Void, Void> {
    public interface SaveToDiskCallback {
        void onDone();
    }

    ExecutorService executor = Executors.newCachedThreadPool();
    SaveToDiskCallback saveToDiskCallback;

    public SaveToDiskAsync(SaveToDiskCallback saveToDiskCallback) {
        this.saveToDiskCallback = saveToDiskCallback;
    }

    @Override
    protected Void doInBackground(QuestionOutput... questionOutputs) {
        File file = null;
        ArrayList<Future<?>> futureArrayList = new ArrayList<>();
        for (int i = 0; i < questionOutputs.length; i++) {
            QuestionOutput questionOutput = questionOutputs[i];
            file = null;
            file = new File(questionOutput.filePath);
            Future<?> future = executor.submit(new SaveToDisk(file, questionOutputs[i]));
            futureArrayList.add(future);
        }
        for (int i = 0; i < futureArrayList.size(); i++) {
            Future<?> future = futureArrayList.get(i);
            try {
                future.get();
            } catch (InterruptedException e) {
                Log.e(Constants.LOG_TAG, "Error while saving object to disk :" + e.getMessage());
            } catch (ExecutionException e) {
                Log.e(Constants.LOG_TAG, "Error while saving object to disk :" + e.getMessage());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (saveToDiskCallback != null) {
            saveToDiskCallback.onDone();
        }
    }

    private class SaveToDisk implements Callable<Void> {
        File file;
        QuestionOutput questionOutput;

        public SaveToDisk(File file, QuestionOutput questionOutput) {
            this.file = file;
            this.questionOutput = questionOutput;
        }

        @Override
        public Void call() throws Exception {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(questionOutput);
            os.close();
            fos.close();
            return null;
        }
    }
}