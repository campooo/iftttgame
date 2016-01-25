package org.campooo.app.info.alarmclock;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * ckb on 16/1/12.
 */
public final class AsyncHandler {

    private static final HandlerThread sHandlerThread = new HandlerThread("AsyncHandler");
    private static final Handler sHandler;

    static {
        sHandlerThread.start();
        sHandler = new Handler(sHandlerThread.getLooper());
    }

    public static void post(Runnable r) {
        sHandler.post(r);
    }

    private AsyncHandler() {
    }

}
