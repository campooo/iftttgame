package org.campooo.modulebox;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import org.campooo.R;
import org.campooo.app.Console;
import org.campooo.app.Device;
import org.campooo.app.Global;
import org.campooo.app.accessibility.MyAccessibilityService;
import org.campooo.quickfox.PushService;

public class MainActivity extends Activity {

    TextView mTextView;

    Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.helloworld);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pushService != null) {
                    pushService.sendRawText(Device.DEVICE_INFO);
                    MyAccessibilityService.AAA = 1;
                }
            }
        });
        bindService(new Intent(MainActivity.this, PushService.class), conn, Service.BIND_AUTO_CREATE);


    }

    PushService pushService = null;

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PushService.PushBinder binder = (PushService.PushBinder) service;
            pushService = binder.getService();
//            Intent it = new Intent(MainActivity.this,MyAccessibilityService.class);
//            startService(it);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


}
