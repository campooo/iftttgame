package org.campooo.app.info.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * ckb on 15/11/28.
 */
public class NetworkState {

    private static final NetworkState NONE = new NetworkState(false, "", NetworkType.NONE);

    private boolean connected;
    private String apnName;
    private NetworkType networkType;
    private NetworkInfo networkInfo;


    private NetworkState() {
    }

    private NetworkState(boolean connected, String apn, NetworkType networkType) {
        setConnected(connected);
        setApnName(apn);
        setNetworkType(networkType);
    }


    final static NetworkState fromNetworkInfo(NetworkInfo info) {
        if (info == null) {
            return NONE;
        }
        NetworkState state = new NetworkState();
        state.setConnected(info.isConnected());
        state.setApnName(info.getExtraInfo());

        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI: {
                state.setNetworkType(NetworkType.WIFI);
                break;
            }
            case ConnectivityManager.TYPE_MOBILE:
            case ConnectivityManager.TYPE_MOBILE_MMS:
            case ConnectivityManager.TYPE_MOBILE_SUPL:
            case ConnectivityManager.TYPE_MOBILE_DUN:
            case ConnectivityManager.TYPE_MOBILE_HIPRI: {
                state.setNetworkType(NetworkType.MOBILE);
                break;
            }
            case ConnectivityManager.TYPE_ETHERNET: {
                state.setNetworkType(NetworkType.ETHERNET);
                break;
            }
            default: {
                state.setNetworkType(NetworkType.OTHER);
                break;
            }
        }
        state.setNetworkInfo(info);

        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof NetworkState) {
            return (((NetworkState) o).isConnected() == this.isConnected())
                    && (((NetworkState) o).getNetworkType().equals(this.getNetworkType()))
                    && (((NetworkState) o).getApnName().equals(this.getApnName()));
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "NetworkState [connected=" + connected + ", apnName=" + apnName + ", type=" + networkType + "]";
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getApnName() {
        return apnName;
    }

    public void setApnName(String apnName) {
        this.apnName = apnName;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public void setNetworkInfo(NetworkInfo networkInfo) {
        this.networkInfo = networkInfo;
    }


}
