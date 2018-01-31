package com.puthuvaazhvu.mapping.data;

import android.content.SharedPreferences;

import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;

import java.io.File;

import io.reactivex.Single;

/**
 * Created by muthuveerappans on 11/23/17.
 */

@Deprecated
public class DataRepository {
    protected final GetFromFile getFromFile;
    protected final SharedPreferences sharedPreferences;

    protected DataRepository(GetFromFile getFromFile, SharedPreferences sharedPreferences) {
        this.getFromFile = getFromFile;
        this.sharedPreferences = sharedPreferences;
    }

    public Single<String> getDataFromFile(File file) {
        return getFromFile.execute(file);
    }
}
