package com.puthuvaazhvu.mapping.utils.info_file;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.JsonHelper;
import com.puthuvaazhvu.mapping.utils.info_file.modals.SavedSurveyInfoFileData;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Created by muthuveerappans on 10/30/17.
 */

/*
    Info JSON structure :

    {
        "saved_surveys": [
            {
                "_id": 1234,
            }
        ]
    }

 */

public class SurveyInfoFile extends InfoFileBase {
    public SurveyInfoFile(GetFromFile getFromFile, SaveToFile saveToFile) {
        super(getFromFile, saveToFile);
    }

    @Override
    public File getInfoFile() {
        return DataFileHelpers.getSurveyInfoFile(false);
    }

    public Callable<SavedSurveyInfoFileData> getInfoJsonParsed() throws ExecutionException, InterruptedException {
        return new Callable<SavedSurveyInfoFileData>() {
            @Override
            public SavedSurveyInfoFileData call() throws Exception {
                JsonObject rootJson = pool.submit(getContentsOfFile()).get();
                return new SavedSurveyInfoFileData(rootJson);
            }
        };
    }

    public Callable<Void> updateListOfSurveys(final ArrayList<String> savedSurveyIds)
            throws IOException, ExecutionException, InterruptedException {

        JsonObject rootJson = pool.submit(getContentsOfFile()).get();
        JsonArray savedSurveyJsonArray = JsonHelper.getJsonArray(rootJson, "saved_surveys");

        if (savedSurveyJsonArray == null) {
            savedSurveyJsonArray = new JsonArray();
            rootJson.add("saved_surveys", savedSurveyJsonArray);
        }

        // add the survey id's
        for (String id : savedSurveyIds) {
            JsonObject surveyJson = new JsonObject();
            surveyJson.addProperty("_id", id);
            savedSurveyJsonArray.add(surveyJson);
        }

        // save
        return saveFile(rootJson);
    }
}
