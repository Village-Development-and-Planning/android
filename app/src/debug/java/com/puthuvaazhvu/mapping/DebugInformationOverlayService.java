package com.puthuvaazhvu.mapping;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by muthuveerappans on 19/03/18.
 */

public class DebugInformationOverlayService extends Service {
    private final static int LOG_INTERVAL = 500;
    View rootView;
    private Handler mHandler;
    TextView heapLogTxt;

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                heapLogTxt.setText(logMemory());
            } finally {
                mHandler.postDelayed(mStatusChecker, LOG_INTERVAL);
            }
        }
    };

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();

        rootView = LayoutInflater.from(this).inflate(R.layout.debug_information, null);

        heapLogTxt = rootView.findViewById(R.id.heap_dump_txt);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0 | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.START | Gravity.TOP;
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        if (wm != null)
            wm.addView(rootView, params);

        startRepeatingTask();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopRepeatingTask();

        if (rootView != null) {
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);

            if (wm != null)
                wm.removeView(rootView);
        }
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
