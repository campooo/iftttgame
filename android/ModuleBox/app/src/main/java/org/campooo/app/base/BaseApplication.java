package org.campooo.app.base;

import android.app.Application;

import org.campooo.app.Global;

/**
 * ckb on 16/1/7.
 */
public class BaseApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        new Global(this);

    }
}
