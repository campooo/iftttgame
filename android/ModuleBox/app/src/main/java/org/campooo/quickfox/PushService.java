package org.campooo.quickfox;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.campooo.app.Global;
import org.campooo.quickfox.core.Connection;
import org.campooo.quickfox.core.ConnectionListener;
import org.campooo.quickfox.core.PushConnection;

/**
 * ckb on 16/1/13.
 */
public class PushService extends Service {

    private static final String START_ACTION = "START_PUSH";
    private static final String STOP_ACTION = "STOP_PUSH";

    private PushConnection pushConn = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public final static void startPushService(Context context) {

        Intent intent = new Intent(START_ACTION);
        intent.setClass(Global.getContext(), PushService.class);
        context.startService(intent);

    }

    public final static void stopPushService(Context context) {

        Intent intent = new Intent(STOP_ACTION);
        intent.setClass(Global.getContext(), PushService.class);
        context.startService(intent);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getAction()) {
            case START_ACTION: {
                if (pushConn == null) {
                    AsyncHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            createPushConnection();
                        }
                    });
                }
                break;
            }
            case STOP_ACTION: {
                stopSelf();
                break;
            }
        }

        return Service.START_NOT_STICKY;
    }

    private void createPushConnection() {

        pushConn = new PushConnection();

        try {
            pushConn.connect();
            pushConn.addConnectionListener(connListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private ConnectionListener connListener = new ConnectionListener() {
        @Override
        public void connectionCreated(Connection conn) {

        }

        @Override
        public void connected(Connection conn) {

        }

        @Override
        public void authenticated(Connection conn) {

        }

        @Override
        public void connectionClosed() {

        }

        @Override
        public void connectionClosedOnError(Exception e) {

        }

        @Override
        public void reconnectingIn(int timeOut) {

        }

        @Override
        public void reconnectSuccessful() {

        }

        @Override
        public void reconnectionFailed(Exception e) {

        }
    };
}
