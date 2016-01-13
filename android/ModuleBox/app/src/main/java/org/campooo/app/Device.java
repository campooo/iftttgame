package org.campooo.app;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.campooo.app.info.network.NetworkObserver;

/**
 * ckb on 16/1/11.
 */
public class Device {

    private static String mDeviceInfo;

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
            builder.append("manu=").append(android.os.Build.MANUFACTURER).append('&');
//            builder.append("wifi=").append(WifiDash.getWifiInfo()).append('&');
//            builder.append("storage=").append(getStorageInfo()).append('&');
//            builder.append("cell=").append(NetworkDash.getCellLevel()).append('&');

        }

        mDeviceInfo = builder.toString();

        return mDeviceInfo;
    }

}
