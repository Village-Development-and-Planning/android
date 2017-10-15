package com.puthuvaazhvu.mapping.application;

import android.app.Application;

import com.puthuvaazhvu.mapping.BuildConfig;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/16/17.
 */

public class MappingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
