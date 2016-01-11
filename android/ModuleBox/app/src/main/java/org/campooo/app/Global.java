package org.campooo.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;

import org.campooo.api.module.ModuleBox;
import org.campooo.app.info.network.NetworkObserver;

import java.util.Date;

/**
 * ckb on 15/11/26.
 */
public final class Global extends ModuleBox {

    private static Context appContext = null;

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

    public Global(Context appContext) {
        super(appContext.getClassLoader());
        if (startDate != null) {
            throw new RuntimeException("a Global already exists");
        }
        setContext(appContext);
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
