package com.puthuvaazhvu.mapping.utils.ObjectToFromDisk;

import android.os.AsyncTask;
import android.util.Log;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Question.QuestionModal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by muthuveerappans on 9/1/17.
 */

public class ObjectToFromDiskAsync {
    public static ObjectToFromDiskAsync objectToFromDiskAsync;

    public static ObjectToFromDiskAsync getInstance() {
        if (objectToFromDiskAsync == null) {
            objectToFromDiskAsync = new ObjectToFromDiskAsync();
        }
        return objectToFromDiskAsync;
    }

    private ObjectToFromDiskAsync() {

    }

    public void saveObjectToDisk(HashMap<String, HashMap<String, QuestionModal>> result
            , String surveyID
            , String bastPath
            , SaveToDiskAsync.SaveToDiskCallback callback) {
        String questionID = result.keySet().iterator().next();
        HashMap<String, QuestionModal> r = result.get(questionID);
        ArrayList<QuestionOutput> questionOutputArrayList = new ArrayList<>();
        for (String optionID : r.keySet()) {
            QuestionModal o = r.get(optionID);
            String filePath = constructFileName(bastPath, surveyID, questionID, optionID);
            questionOutputArrayList.add(new QuestionOutput(surveyID, questionID, optionID, o, filePath));
        }
        SaveToDiskAsync saveToDiskAsync = new SaveToDiskAsync(callback);
        saveToDiskAsync.execute(questionOutputArrayList.toArray(new QuestionOutput[0]));
    }

    public void getObjectFromDisk(File[] fileArray, GetFromDiskAsync.GetFromDiskCallback callback) {
        GetFromDiskAsync fromDiskAsync = new GetFromDiskAsync(callback);
        fromDiskAsync.execute(fileArray);
    }

    public static String constructFileName(String basePath, String surveyID, String questionID, String optionID) {
        return basePath + File.separator + surveyID + "," + questionID + (optionID.isEmpty() ? "" : "," + optionID);
    }
}
