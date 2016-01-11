package org.campooo.app.info.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.campooo.api.module.Module;
import org.campooo.app.Global;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * ckb on 15/11/28.
 */
public class NetworkObserver extends BroadcastReceiver implements Module<Global> {

    private static Global sGlobal = null;

    private static NetworkState currState;
    private static NetworkState lastState;

    private static final List<WeakReference<NetworkStateListener>> listeners = new ArrayList<>();


    public final static void addListener(NetworkStateListener listener) {
        synchronized (listeners) {
            listeners.add(new WeakReference<NetworkStateListener>(listener));
        }
    }

    public final static void removeListener(NetworkStateListener listener) {
        synchronized (listeners) {
            WeakReference<NetworkStateListener> reference = null;

            for (WeakReference<NetworkStateListener> weakReference : listeners) {
                NetworkStateListener aListener = weakReference.get();
                if (aListener.equals(listener)) {
                    reference = weakReference;
                    break;
                }
            }
            listeners.remove(reference);

        }
    }

    final static boolean updateNetworkState() {
        synchronized (NetworkObserver.class) {
            NetworkInfo networkInfo = null;
            try {
                ConnectivityManager connectivityManager = Global.getConnectivityManager();
                networkInfo = connectivityManager.getActiveNetworkInfo();
            } catch (Exception e) {
                networkInfo = null;
            }

            boolean changed = setCurrState(NetworkState.fromNetworkInfo(networkInfo));

            if (changed) {
                notifyNetworkStateChange();
            }
            return changed;
        }
    }

    private final static boolean setCurrState(NetworkState newState) {
        synchronized (NetworkObserver.class) {
            lastState = currState;
            currState = newState;
            return !currState.equals(lastState);
        }
    }

    private static void notifyNetworkStateChange() {
        //FIXME
        synchronized (listeners) {
            for (WeakReference<NetworkStateListener> weakReference : listeners) {
                NetworkStateListener listener = weakReference.get();
                if (listener != null) {
                    listener.onNetworkStateChanged(getCurrState(), getLastState());
                }
            }
        }
    }


    public static NetworkState getCurrState() {
        return currState;
    }

    protected static NetworkState getLastState() {
        return lastState;
    }

    public static boolean isNetworkAvailable() {
        updateNetworkState();
        return getCurrState().isConnected();
    }

    public static boolean isWifi() {
        updateNetworkState();
        return getCurrState().getNetworkType() == NetworkType.WIFI;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkObserver.updateNetworkState();
        }
    }

    @Override
    public void initialize(Global global) {
        sGlobal = global;
        sGlobal.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)); //TODO 对权限的统一申请
    }

    @Override
    public void destroy() {
        sGlobal.unregisterReceiver(this);
    }
}
