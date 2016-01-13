package org.campooo.app.crash;

import android.util.Log;

import org.campooo.api.module.Module;
import org.campooo.app.Global;

/**
 * ckb on 16/1/11.
 */
public class DVMCrashHandler implements Module<Global>, Thread.UncaughtExceptionHandler, Runnable {

    private Thread.UncaughtExceptionHandler otherCrashHandler;

    public Global global;

    private Thread shutdownThread;

    @Override
    public void initialize(Global box) {
        global = box;
        otherCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void destroy() {
        //这个模块的周期希望比App还长
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            if (!handleException(ex) && otherCrashHandler != null) {
                otherCrashHandler.uncaughtException(thread, ex);
            }
        } catch (Exception e) {
            //ignore
        }
    }

    private boolean handleException(Throwable ex) {
        shutdownThread = new Thread(this, "Shutdown Thread");
        shutdownThread.setDaemon(false);
        shutdownThread.start();
        return false;
    }

    @Override
    public void run() {
        Log.i("DVMCrashHandler","vm shut downing");
        global.destroyModules();
    }
}
