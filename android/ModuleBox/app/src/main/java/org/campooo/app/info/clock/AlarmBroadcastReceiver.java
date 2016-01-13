package org.campooo.app.info.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import org.campooo.app.Global;

/**
 * ckb on 16/1/12.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (intent != null) {

            final String action = intent.getAction();

            if (action != null) {
                final PendingResult result = goAsync();
                AlarmWakeLock.acquireCpuWakeLock(context);
                AsyncHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleIntent(context, intent);
                        result.finish();
                        AlarmWakeLock.releaseCpuLock();
                    }
                });
            }
        }
    }

    private void handleIntent(Context context, Intent intent) {
        SystemClock.sleep(5000);
        AlarmClockListener listener = (AlarmClockListener) Global.loadClass(context.getClassLoader(), intent.getAction());
        listener.onClockArrived(context, intent);
    }

}
