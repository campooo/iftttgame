package org.campooo.app.info.clock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

import org.campooo.api.module.Module;
import org.campooo.app.Global;

import java.util.HashMap;
import java.util.Map;

/**
 * ckb on 16/1/11.
 */
public class AlarmClockCollector implements Module<Global> {

    private Global global;

    private static final Map<String, AlarmClock> ALARM_CLOCK_MAP = new HashMap<String, AlarmClock>();

    @Override
    public void initialize(Global box) {
        global = box;
    }

    @Override
    public void destroy() {

    }

    public static <T extends AlarmClockListener> boolean startAlarm(int interval, T listener) {
        try {
            AlarmManager alarmManager = (AlarmManager) Global.getSystemService(Context.ALARM_SERVICE);

            int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;

            long timeOrLengthofWait = SystemClock.elapsedRealtime() + interval;

            Intent intentToFire = new Intent(Global.getContext(), AlarmBroadcastReceiver.class);
            intentToFire.setAction(listener.getClass().getName());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(Global.getContext(), 0, intentToFire,
                    PendingIntent.FLAG_UPDATE_CURRENT);

//            clock.setPendingIntent(pendingIntent);

            alarmManager.set(alarmType, timeOrLengthofWait, pendingIntent);

            synchronized (AlarmClockCollector.class) {
                ALARM_CLOCK_MAP.put(listener.getClass().getSimpleName(), null);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean set(AlarmClock clock) {
        try {
            AlarmManager alarmManager = (AlarmManager) Global.getSystemService(Context.ALARM_SERVICE);

            int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;

            long timeOrLengthofWait = SystemClock.elapsedRealtime() + clock.getInterval();

            Intent intentToFire = new Intent(clock.getAction());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(Global.getContext(), 0, intentToFire,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            clock.setPendingIntent(pendingIntent);

            alarmManager.set(alarmType, timeOrLengthofWait, pendingIntent);

            synchronized (AlarmClockCollector.class) {
                ALARM_CLOCK_MAP.put(clock.getAction(), clock);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void cancel(AlarmClock clock) {
        AlarmManager alarmManager = (AlarmManager) Global.getSystemService(Context.ALARM_SERVICE);

        if (clock.getPendingIntent() != null) {
            alarmManager.cancel(clock.getPendingIntent());

            clock.setPendingIntent(null);
        }

        synchronized (AlarmClockCollector.class) {
            ALARM_CLOCK_MAP.remove(clock.getClockId());
        }
    }

    public static void cancelWhenArrived(AlarmClock clock) {
        synchronized (AlarmClockCollector.class) {
            clock.setPendingIntent(null);

            ALARM_CLOCK_MAP.remove(clock.getAction());
        }
    }

    public static AlarmClock getClock(String namePrefix) {
        return ALARM_CLOCK_MAP.get(namePrefix);
    }

}
