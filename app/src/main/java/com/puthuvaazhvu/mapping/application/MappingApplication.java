package com.puthuvaazhvu.mapping.application;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.puthuvaazhvu.mapping.BuildConfig;
import com.puthuvaazhvu.mapping.application.modal.ApplicationData;
import com.puthuvaazhvu.mapping.other.Constants;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/16/17.
 */

public class MappingApplication extends Application {

    public static ApplicationData applicationData;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Timber.tag(Constants.LOG_TAG);
        }

        Fabric.with(this, new Crashlytics());

        applicationData = ApplicationData.getInstance();
    }
}
