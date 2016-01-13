package org.campooo.app.info.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

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
        String action = intent.getAction();

        AlarmClock alarmClock = AlarmClockCollector.getClock(action);

        AlarmClockListener listener = null;

        if (alarmClock != null) {
            listener = alarmClock.getListener();
        } else {
            String listenerName = intent.getStringExtra(AlarmClock.LISTENER_CLAZZ);
            if (!TextUtils.isEmpty(listenerName)) {
                Object listenerClazz = Global.loadClass(context.getClassLoader(), listenerName);
                if (listenerClazz != null) {
                    try {
                        listener = AlarmClockListener.class.cast(listenerClazz);
                    } catch (ClassCastException cce) {
                        Log.e("AlarmBroadcastReceiver", action + " listener not found");
                    }
                }
            }
        }

        if (listener != null) {
            boolean proceed = listener.onClockArrived(context, intent);
            if (proceed) {
                AlarmClockCollector.startAlarm(alarmClock);
            } else {
                AlarmClockCollector.cancel(alarmClock);
            }
        }

    }

}
