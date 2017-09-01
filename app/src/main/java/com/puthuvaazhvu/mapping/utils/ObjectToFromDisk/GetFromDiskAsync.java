package com.puthuvaazhvu.mapping.utils.ObjectToFromDisk;

import android.os.AsyncTask;
import android.util.Log;

import com.puthuvaazhvu.mapping.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by muthuveerappans on 9/1/17.
 */

public class GetFromDiskAsync extends AsyncTask<File, Void, ArrayList<QuestionOutput>> {
    public interface GetFromDiskCallback {
        void onDone(ArrayList<QuestionOutput> questionOutputs);
    }

    ExecutorService executor = Executors.newCachedThreadPool();
    GetFromDiskCallback getFromDiskCallback;

    public GetFromDiskAsync(GetFromDiskCallback getFromDiskCallback) {
        this.getFromDiskCallback = getFromDiskCallback;
    }

    @Override
    protected ArrayList<QuestionOutput> doInBackground(File... files) {
        ArrayList<QuestionOutput> result = new ArrayList<>();
        ArrayList<Future<QuestionOutput>> futureArrayList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            Future<QuestionOutput> future = executor.submit(new GetFromDisk(files[i]));
            futureArrayList.add(future);
        }
        for (int i = 0; i < futureArrayList.size(); i++) {
            Future<QuestionOutput> future = futureArrayList.get(i);
            try {
                result.add(future.get());
            } catch (InterruptedException e) {
                Log.e(Constants.LOG_TAG, "Error while saving object to disk :" + e.getMessage());
            } catch (ExecutionException e) {
                Log.e(Constants.LOG_TAG, "Error while saving object to disk :" + e.getMessage());
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<QuestionOutput> result) {
        if (getFromDiskCallback != null) {
            getFromDiskCallback.onDone(result);
        }
    }

    private class GetFromDisk implements Callable<QuestionOutput> {
        File file;

        public GetFromDisk(File file) {
            this.file = file;
        }

        @Override
        public QuestionOutput call() throws Exception {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream is = new ObjectInputStream(fis);
            QuestionOutput questionOutput = (QuestionOutput) is.readObject();
            is.close();
            fis.close();
            return questionOutput;
        }
    }
}
