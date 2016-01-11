package org.campooo.app.info.network;

/**
 * ckb on 15/11/28.
 */
public interface NetworkStateListener {

    void onNetworkStateChanged(NetworkState currState, NetworkState lastState);

}
