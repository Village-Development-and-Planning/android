package com.puthuvaazhvu.mapping.other;

import android.os.Handler;

/**
 * Created by muthuveerappans on 11/25/17.
 */

public class RepeatingTask {
    private final Handler handler;
    private final Runnable task;
    private final long millis;

    public RepeatingTask(Handler handler, final Runnable task, final long millis) {
        this.handler = handler;
        this.task = task;
        this.millis = millis;
    }

    public void start() {
        if (runningTask != null) {
            runningTask.run();
        }
    }

    public void stop() {
        if (runningTask != null)
            handler.removeCallbacks(runningTask);
    }

    private Runnable runningTask = new Runnable() {
        @Override
        public void run() {
            try {
                task.run();
            } finally {
                handler.postDelayed(runningTask, millis);
            }
        }
    };
}
