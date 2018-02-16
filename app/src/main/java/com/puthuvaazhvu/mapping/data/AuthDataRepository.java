package com.puthuvaazhvu.mapping.data;

import android.content.SharedPreferences;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.network.APIUtils;
import com.puthuvaazhvu.mapping.network.implementations.AuthAPI;
import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class AuthDataRepository {
    private static AuthDataRepository authDataRepository;
    private final AuthAPI authAPI;

    public static AuthDataRepository getInstance(SharedPreferences sharedPreferences) {
        if (authDataRepository == null) {
            authDataRepository = new AuthDataRepository(sharedPreferences);
        }
        return authDataRepository;
    }

    private AuthDataRepository(SharedPreferences sharedPreferences) {
        authAPI = AuthAPI.getInstance(APIUtils.getAuth(sharedPreferences));
    }

    public Observable<JsonObject> getAuthData(boolean forceFromNetwork) {
        if (forceFromNetwork) return getAuthDataFromNetworkAndSave();

        if (FileUtils.fileExists(getPath())) {
            return readFromFile();
        } else {
            return getAuthDataFromNetworkAndSave();
        }
    }

    private Observable<JsonObject> readFromFile() {
        return FileUtils.readFromPath(getPath())
                .map(new Function<String, JsonObject>() {
                    @Override
                    public JsonObject apply(String s) throws Exception {
                        try {
                            JsonParser jsonParser = new JsonParser();
                            JsonElement jsonElement = jsonParser.parse(s);
                            return jsonElement.getAsJsonObject();
                        } catch (JsonParseException e) {
                            Timber.e(e);
                            throw new Exception(e);
                        }
                    }
                });
    }

    private Observable<JsonObject> getAuthDataFromNetworkAndSave() {
        return authAPI.getAuthData()
                .flatMap(new Function<JsonObject, ObservableSource<JsonObject>>() {
                    @Override
                    public ObservableSource<JsonObject> apply(JsonObject jsonObject) throws Exception {
                        // save the auth data to a file
                        return FileUtils.saveToFileFromPath(getPath(), jsonObject.toString())
                                .flatMap(new Function<File, ObservableSource<JsonObject>>() {
                                    @Override
                                    public ObservableSource<JsonObject> apply(File file) throws Exception {
                                        return readFromFile();
                                    }
                                });
                    }
                });
    }

    private String getPath() {
        return File.separator + Constants.AUTH_FILE_NAME;
    }
}
