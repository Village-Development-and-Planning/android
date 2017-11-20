package com.puthuvaazhvu.mapping.utils.info_file;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.Optional;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 10/31/17.
 */

public abstract class InfoFileBase {
    protected final GetFromFile getFromFile;
    protected final SaveToFile saveToFile;

    public InfoFileBase(GetFromFile getFromFile, SaveToFile saveToFile) {
        this.getFromFile = getFromFile;
        this.saveToFile = saveToFile;
    }

    public abstract File getInfoFile();

    public Single<JsonObject> getContentsOfFile() {
        return getFromFile.execute(getInfoFile()).map(new Function<String, JsonObject>() {
            @Override
            public JsonObject apply(@NonNull String contents) throws Exception {
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
        });
    }

    public Single<Optional> saveFile(JsonObject updatedRootJson) {
        File infoFile = getInfoFile();
        return saveToFile.execute(updatedRootJson.toString(), infoFile);
    }
}
