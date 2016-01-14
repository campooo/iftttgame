package org.campooo.app;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.campooo.api.module.Module;
import org.campooo.app.info.network.NetworkObserver;

import java.util.UUID;

/**
 * ckb on 16/1/11.
 */
public final class Device implements Module<Global> {

    public static String DEVICE_INFO;
    public static String MAC_ADDRESS;

    public static String updateDeviceInfo() {
        WindowManager manager = (WindowManager) Global.getSystemService(Context.WINDOW_SERVICE);
        TelephonyManager mTelephonyMgr = (TelephonyManager) Global.getSystemService(Context.TELEPHONY_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displayMetrics);

        StringBuilder builder = new StringBuilder();
        {
            String device_id = null;

            try {
                device_id = mTelephonyMgr.getDeviceId();
            } catch (Exception e) {
                device_id = "N/A";
            }

            String apn = "";
            if (NetworkObserver.isWifi())
                apn = "wifi";
            else if (NetworkObserver.isMobile())
                apn = "mobile";
            else if (!NetworkObserver.isNetworkAvailable())
                apn = "N/A";

            builder.append("imei=").append(device_id).append('&');

            builder.append("model=").append(android.os.Build.MODEL).append('&');
            builder.append("os=").append(android.os.Build.VERSION.RELEASE).append('&');
            builder.append("apilevel=").append(android.os.Build.VERSION.SDK_INT).append('&');

            builder.append("network=").append(apn).append('&');
//            builder.append("sdcard=").append(Device.Storage.hasExternal() ? 1 : 0).append('&');
            builder.append("sddouble=").append("0").append('&');
            builder.append("display=").append(displayMetrics.widthPixels).append('*')
                    .append(displayMetrics.heightPixels).append('&');
            builder.append("manu=").append(android.os.Build.MANUFACTURER)/*.append('&')*/;
//            builder.append("wifi=").append(WifiDash.getWifiInfo()).append('&');
//            builder.append("storage=").append(getStorageInfo()).append('&');
//            builder.append("cell=").append(NetworkDash.getCellLevel()).append('&');

        }

        DEVICE_INFO = builder.toString();

        return DEVICE_INFO;
    }

    public static void updateMacAddress() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                MAC_ADDRESS = Console.execute("getprop ro.boot.wifimacaddr", 10000);
            } else {
                WifiManager wifi = (WifiManager) Global.getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifi.getConnectionInfo();
                MAC_ADDRESS = info.getMacAddress();
            }

        } catch (Exception e) {
            // no-op
        }
    }

    @Override
    public void initialize(Global box) {
        updateDeviceInfo();
        updateMacAddress();
    }

    @Override
    public void destroy() {

    }
}
