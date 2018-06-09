package com.puthuvaazhvu.mapping.application;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/16/17.
 */

public class MappingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
    }

}
