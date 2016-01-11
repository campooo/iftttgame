package org.campooo.quickfox.log;

import android.util.Log;

/**
 * 简单的封装
 *
 * @author campo
 */
public final class QLog implements Logger {

    private String TAG = "QuickFox ";

    public static final QLog getLogger(Class clazz) {
        return new QLog(clazz);
    }

    private QLog(Class clazz) {
        TAG += clazz.getSimpleName();
    }

    public void verbose(String msg) {
        Log.v(TAG, msg);
    }

    public void debug(String msg) {
        Log.d(TAG, msg);
    }

    public void info(String msg) {
        Log.i(TAG, msg);
    }

    public void wran(String msg) {
        Log.w(TAG, msg);
    }

    public void err(String msg) {
        err(msg, null);
    }

    public void err(Throwable tr) {
        err("", tr);
    }

    public void err(String msg, Throwable tr) {
        Log.e(TAG, msg, tr);
    }

}
