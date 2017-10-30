package com.puthuvaazhvu.mapping.utils.info_file;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.JsonHelper;
import com.puthuvaazhvu.mapping.utils.info_file.modals.InfoFile;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

import static com.puthuvaazhvu.mapping.other.Constants.SURVEY_INFO_FILE_NAME;

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

public class UpdateInfoFile {
    private final GetFromFile getFromFile;
    private final File infoFile;
    private final SaveToFile saveToFile;

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    public UpdateInfoFile(GetFromFile getFromFile, SaveToFile saveToFile) {
        this.getFromFile = getFromFile;
        this.saveToFile = saveToFile;

        infoFile = DataFileHelpers.getSurveyInfoFile(false);

    }

    public Callable<InfoFile> getInfoJsonParsed() throws ExecutionException, InterruptedException {
        return new Callable<InfoFile>() {
            @Override
            public InfoFile call() throws Exception {
                String contents = pool.submit(getFromFile.execute(infoFile)).get();

                if (contents.isEmpty()) {
                    return null;
                } else {
                    JsonParser jsonParser = new JsonParser();
                    return new InfoFile(jsonParser.parse(contents).getAsJsonObject());
                }
            }
        };
    }

    public Callable<Void> updateAnsweredSurveys(final ArrayList<String> savedSurveyIds)
            throws IOException, ExecutionException, InterruptedException {

        String contents = pool.submit(getFromFile.execute(infoFile)).get();

        JsonObject rootJson = null;
        JsonArray savedSurveyJsonArray = null;

        if (!contents.isEmpty()) {
            JsonParser jsonParser = new JsonParser();
            rootJson = jsonParser.parse(contents).getAsJsonObject();

            savedSurveyJsonArray = JsonHelper.getJsonArray(rootJson, "saved_surveys");
        } else {
            // if empty create a new json object
            rootJson = new JsonObject();
        }

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
        return saveToFile.execute(rootJson.toString(), infoFile);
    }
}
