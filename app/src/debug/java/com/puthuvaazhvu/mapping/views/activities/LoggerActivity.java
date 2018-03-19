package com.puthuvaazhvu.mapping.views.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.puthuvaazhvu.mapping.BuildConfig;
import com.puthuvaazhvu.mapping.other.Config;
import com.puthuvaazhvu.mapping.utils.GlobalExceptionHandler;

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

        GlobalExceptionHandler.getInstance(this);

        mHandler = new Handler();
        startRepeatingTask();
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
                logHeap(_logHeap());
            } finally {
                mHandler.postDelayed(mStatusChecker, LOG_INTERVAL);
            }
        }
    };

    protected void logHeap(String log) {
        Timber.i(log);
    }

    private String _logHeap() {
        String log = "";

        log += "HEAP MEMORY\n";
        log += "------------\n";
        log += logMemory();
        log += "\n";

        return log;
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