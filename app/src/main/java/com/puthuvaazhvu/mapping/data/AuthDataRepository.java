package com.puthuvaazhvu.mapping.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.filestorage.AuthIO;
import com.puthuvaazhvu.mapping.filestorage.StorageUtils;
import com.puthuvaazhvu.mapping.network.APIUtils;
import com.puthuvaazhvu.mapping.network.implementations.AuthAPI;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class AuthDataRepository extends DataRepository<JsonObject> {
    private final AuthAPI authAPI;
    private final AuthIO authIO;

    public AuthDataRepository(Context context, String username, String password) {
        super(context);
        authAPI = new AuthAPI(username, password);
        authIO = new AuthIO();
    }

    public AuthDataRepository(Context context) {
        super(context);
        authIO = new AuthIO();
        authAPI = new AuthAPI("", "");
    }

    public Observable<JsonObject> get(boolean forceNetwork) {
        if (forceNetwork || Utils.isNetworkAvailable(context)
                || !StorageUtils.isPathAValidFile(authIO.getAbsolutePath())) {
            return getFromNetwork();
        } else {
            return getFromFileSystem();
        }
    }

    @Override
    public Observable<JsonObject> getFromNetwork() {
        return authAPI.getAuthData()
                .map(new Function<JsonObject, JsonObject>() {
                    @Override
                    public JsonObject apply(JsonObject jsonObject) throws Exception {
                        File file = authIO.save(jsonObject).blockingFirst();
                        if (!file.exists())
                            throw new IllegalArgumentException("Auth not saved.");
                        return jsonObject;
                    }
                });
    }

    @Override
    public Observable<JsonObject> getFromFileSystem() {
        return authIO.read();
    }
}
