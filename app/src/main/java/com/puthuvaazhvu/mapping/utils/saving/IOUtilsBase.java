package com.puthuvaazhvu.mapping.utils.saving;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.utils.FileUtils;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class IOUtilsBase {

    public Observable<JsonObject> readFileAsJson(String path) {
        return FileUtils.readFromPath(path)
                .map(new Function<String, JsonObject>() {
                    @Override
                    public JsonObject apply(String s) throws Exception {
                        JsonParser jsonParser = new JsonParser();
                        JsonElement jsonElement = jsonParser.parse(s);

                        if (jsonElement != null) {
                            return jsonElement.getAsJsonObject();
                        }
                        return null;
                    }
                });
    }
}
