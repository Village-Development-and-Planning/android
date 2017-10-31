package com.puthuvaazhvu.mapping.utils.info_file;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.JsonHelper;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswersInfoFileData;
import com.puthuvaazhvu.mapping.utils.info_file.modals.SavedSurveyInfoFileData;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Created by muthuveerappans on 10/31/17.
 */
/*
    Info JSON structure :

    {
        "answered_surveys": [
            {
                "_id": 1234,
            }
        ]
    }

 */

public class AnswersInfoFile extends InfoFileBase {

    public AnswersInfoFile(GetFromFile getFromFile, SaveToFile saveToFile) {
        super(getFromFile, saveToFile);
    }

    @Override
    public File getInfoFile() {
        return DataFileHelpers.getAnswersInfoFile(false);
    }

    public Callable<AnswersInfoFileData> getInfoJsonParsed() throws ExecutionException, InterruptedException {
        return new Callable<AnswersInfoFileData>() {
            @Override
            public AnswersInfoFileData call() throws Exception {
                JsonObject rootJson = pool.submit(getContentsOfFile()).get();
                return new AnswersInfoFileData(rootJson);
            }
        };
    }

    public Callable<Void> updateListOfSurveys(final ArrayList<String> answeredSurveyIds)
            throws IOException, ExecutionException, InterruptedException {

        JsonObject rootJson = pool.submit(getContentsOfFile()).get();
        JsonArray savedSurveyJsonArray = JsonHelper.getJsonArray(rootJson, "answered_surveys");

        if (savedSurveyJsonArray == null) {
            savedSurveyJsonArray = new JsonArray();
            rootJson.add("answered_surveys", savedSurveyJsonArray);
        }

        // add the survey id's
        for (String id : answeredSurveyIds) {
            JsonObject surveyJson = new JsonObject();
            surveyJson.addProperty("_id", id);
            savedSurveyJsonArray.add(surveyJson);
        }

        // save
        return saveFile(rootJson);
    }
}
