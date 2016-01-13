package org.campooo.app.base;

import android.app.Application;
import android.os.SystemClock;
import android.util.Log;

import org.campooo.app.Global;

/**
 * ckb on 16/1/7.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Global.init(this);

    }
}
