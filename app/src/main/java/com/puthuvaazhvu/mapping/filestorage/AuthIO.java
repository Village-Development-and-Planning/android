package com.puthuvaazhvu.mapping.filestorage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.root;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public class AuthIO extends StorageIO<JsonObject> {

    @Override
    public Observable<JsonObject> read(File file) {
        return StorageUtils.readFromFile(file)
                .map(new Function<byte[], JsonObject>() {
                    @Override
                    public JsonObject apply(byte[] bytes) throws Exception {
                        String s = new String(bytes);
                        JsonParser parser = new JsonParser();
                        JsonElement jsonElement = parser.parse(s);
                        return jsonElement.getAsJsonObject();
                    }
                });
    }

    @Override
    public Observable<File> save(File file, JsonObject contents) {
        return StorageUtils.saveContentsToFile(file, contents.toString());
    }

    @Override
    public String getAbsolutePath() {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.AUTH_FILE_NAME;
    }
}
