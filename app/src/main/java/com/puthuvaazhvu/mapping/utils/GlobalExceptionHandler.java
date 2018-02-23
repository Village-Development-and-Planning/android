package com.puthuvaazhvu.mapping.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.puthuvaazhvu.mapping.filestorage.LogIO;

import java.io.InputStream;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 24/02/18.
 */

public class GlobalExceptionHandler {
    private static GlobalExceptionHandler globalExceptionHandler;
    private Context context;

    public static GlobalExceptionHandler getInstance(Context context) {
        if (globalExceptionHandler == null) {
            globalExceptionHandler = new GlobalExceptionHandler(context);
        }
        return globalExceptionHandler;
    }

    private GlobalExceptionHandler(Context context) {
        this.context = context;
        setUpGlobalExceptionHandler();
    }

    private void setUpGlobalExceptionHandler() {
        final Thread.UncaughtExceptionHandler oldHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(
                            Thread paramThread,
                            Throwable paramThrowable
                    ) {

                        saveLogs();

                        if (oldHandler != null)
                            oldHandler.uncaughtException(
                                    paramThread,
                                    paramThrowable
                            );
                        else
                            System.exit(2);
                    }
                });
    }

    private void saveLogs() {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

            String model = Build.MODEL;
            if (!model.startsWith(Build.MANUFACTURER))
                model = Build.MANUFACTURER + " " + model;

            String cmd = "logcat -d -v time";

            Process process = Runtime.getRuntime().exec(cmd);
            InputStream in = process.getInputStream();

            String log = "";
            log += "Android version: " + Build.VERSION.SDK_INT + "\n";
            log += "Device: " + model + "\n";
            log += "App version: " + (info == null ? "(null)" : info.versionCode) + "\n";
            log += Utils.readFromInputStream(in);

            LogIO logIO = new LogIO("log_" + System.currentTimeMillis());
            logIO.save(log).blockingFirst();
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
