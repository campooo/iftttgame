package org.campooo.app.crash;

import org.campooo.api.module.Module;
import org.campooo.app.Global;

/**
 * ckb on 16/1/11.
 */
public class DVMCrashHandler implements Module<Global>, Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler otherCrashHandler;

    @Override
    public void initialize(Global box) {
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
        return false;
    }
}
