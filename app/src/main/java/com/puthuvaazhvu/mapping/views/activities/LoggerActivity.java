package com.puthuvaazhvu.mapping.views.activities;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.puthuvaazhvu.mapping.BuildConfig;
import com.puthuvaazhvu.mapping.filestorage.LogIO;
import com.puthuvaazhvu.mapping.other.Config;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 22/02/18.
 */

@SuppressLint("Registered")
public class LoggerActivity extends AppCompatActivity {
    private final static int LOG_INTERVAL = 500;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Config.GLOBAL_EXCEPTION_HANDLER)
            setUpGlobalExceptionHandler();

        mHandler = new Handler();
        if (!BuildConfig.BUILD_TYPE.equals("release"))
            startRepeatingTask();
    }

    // Todo: refactor properly
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
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                log();
            } finally {
                mHandler.postDelayed(mStatusChecker, LOG_INTERVAL);
            }
        }
    };

    private void log() {
        String log = "";

        log += "HEAP MEMORY\n";
        log += "------------\n";
        log += logMemory();
        log += "\n";

        Timber.i(log);
    }

    private String logMemory() {
        final Runtime runtime = Runtime.getRuntime();
        final long usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB = runtime.maxMemory() / 1048576L;
        final long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;

        String log = "";
        log += "Used Memory (MB): " + usedMemInMB + "\n";
        log += "Maximum Heap size (MB): " + maxHeapSizeInMB + "\n";
        log += "Available Heap size (MB): " + availHeapSizeInMB + "\n";

        return log;
    }

    private void startRepeatingTask() {
        mStatusChecker.run();
    }

    private void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
}
