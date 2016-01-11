package org.campooo.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;

import org.campooo.api.module.ModuleBox;
import org.campooo.app.crash.DVMCrashHandler;
import org.campooo.app.info.network.NetworkObserver;

import java.util.Date;

/**
 * App的设备相关基础模块构建
 * <p/>
 * ckb on 15/11/26.
 */
public final class Global extends ModuleBox {

    private static Context appContext = null;

    private static Global instance = null;
    private Date startDate;

    public static Context getContext() {
        if (appContext == null) {
            throw new RuntimeException("app is not extend base appliction in androidmanifest.xml");
        }
        return appContext;
    }

    private final static void setContext(Context appContext) {
        Global.appContext = appContext;
    }

    public static void init(Context appContext) {
        setContext(appContext);
        if (instance != null) {
            throw new RuntimeException("a Global already exists");
        }
        instance = new Global();
    }

    public Global() {
        super(getContext().getClassLoader());
        initialize(this);
    }

    public void initialize(ModuleBox box) {
        startDate = new Date();
        loadModules();
        initModules();
    }

    /**
     * 注册所有模块
     */
    private void loadModules() {
        loadModule(NetworkObserver.class.getName());
        loadModule(DVMCrashHandler.class.getName());
    }

    public void destroy() {
        destroyModules();
    }

    public final static ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public final Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter) {
        return getContext().registerReceiver(receiver, filter);
    }

    public final void unregisterReceiver(BroadcastReceiver receiver) {
        getContext().unregisterReceiver(receiver);
    }


}
