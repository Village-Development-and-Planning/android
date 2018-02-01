package com.puthuvaazhvu.mapping.application;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import com.crashlytics.android.Crashlytics;
import com.puthuvaazhvu.mapping.BuildConfig;
import com.puthuvaazhvu.mapping.application.modal.ApplicationData;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.FileUtils;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/16/17.
 */

public class MappingApplication extends Application {

    public static GlobalContext globalContext;

    @Override
    public void onCreate() {
        super.onCreate();

        // Setup handler for uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Timber.tag(Constants.LOG_TAG);
        }

        Fabric.with(this, new Crashlytics());

        globalContext = GlobalContext.getInstance();
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically
        saveLogToFile();
        System.exit(1); // kill off the crashed app
    }

    private void saveLogToFile() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;

        String path = Environment.getExternalStorageDirectory() + "/" + Constants.DATA_DIR + "/logs";
        File logDir = FileUtils.createDirectory(path);

        String fileName = "log_" + System.currentTimeMillis() + ".txt";
        File logFile = null;
        if (logDir != null) {
            logFile = FileUtils.createFile(logDir, fileName);
        }

        if (logFile == null) return;

        // Extract to file.
        InputStreamReader reader = null;
        FileWriter writer = null;
        try {

            // For Android 4.0 and earlier, you will get all app's log output, so filter it to
            // mostly limit it to your app's output.  In later versions, the filtering isn't needed.
            String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?
                    "logcat -d -v time MyApp:v dalvikvm:v System.err:v *:s" :
                    "logcat -d -v time";

            // get input stream
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new InputStreamReader(process.getInputStream());

            // write output stream
            writer = new FileWriter(logFile);
            writer.write("Android version: " + Build.VERSION.SDK_INT + "\n");
            writer.write("Device: " + model + "\n");
            writer.write("App version: " + (info == null ? "(null)" : info.versionCode) + "\n");

            char[] buffer = new char[10000];
            do {
                int n = reader.read(buffer, 0, buffer.length);
                if (n == -1)
                    break;
                writer.write(buffer, 0, n);
            } while (true);

            reader.close();
            writer.close();
        } catch (IOException e) {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e1) {
                }
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e1) {
                }
        }
    }

}
