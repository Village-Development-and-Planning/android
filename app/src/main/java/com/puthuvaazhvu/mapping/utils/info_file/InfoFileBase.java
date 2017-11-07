package com.puthuvaazhvu.mapping.utils.info_file;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by muthuveerappans on 10/31/17.
 */

public abstract class InfoFileBase {
    protected final GetFromFile getFromFile;
    protected final SaveToFile saveToFile;

    protected final ExecutorService pool = Executors.newSingleThreadExecutor();

    public InfoFileBase(GetFromFile getFromFile, SaveToFile saveToFile) {
        this.getFromFile = getFromFile;
        this.saveToFile = saveToFile;
    }

    public abstract File getInfoFile();

    public Callable<JsonObject> getContentsOfFile() {
        return new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                return getContentsOfFileInternal();
            }
        };
    }

    public Callable<Void> saveFile(JsonObject updatedRootJson) {
        File infoFile = getInfoFile();
        return saveToFile.execute(updatedRootJson.toString(), infoFile);
    }

    /**
     * Should be run in the background thread
     *
     * @return - contents of the file in JSON format
     * @throws IOException
     */
    private JsonObject getContentsOfFileInternal() throws IOException {
        File infoFile = getInfoFile();
        String contents = getFromFile.executeSynchronous(infoFile);

        JsonObject root;

        if (contents.isEmpty()) {

            // if empty create a new json object
            root = new JsonObject();

        } else {
            JsonParser jsonParser = new JsonParser();
            root = jsonParser.parse(contents).getAsJsonObject();
        }

        return root;
    }
}
