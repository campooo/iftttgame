package org.campooo.app.info.clock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

    public static boolean startAlarm(AlarmClock clock) {
        try {
            AlarmManager alarmManager = (AlarmManager) Global.getSystemService(Context.ALARM_SERVICE);

            int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;

            long fireTime = SystemClock.elapsedRealtime() + clock.getInterval();

            Intent intent = new Intent(Global.getContext(), AlarmBroadcastReceiver.class);
            intent.setAction(clock.getName());
            /**
             * 这么做会触发android的bug，class not found
             *
             * intent.setExtrasClassLoader(AlarmClock.class.getClassLoader());
             * intent.putExtra(AlarmClock.INSTANCE, clock);
             */
            Bundle hackBundle = new Bundle();
            hackBundle.putParcelable(AlarmClock.INSTANCE, clock);
            if (clock.getListener() != null) {
                hackBundle.putString(AlarmClock.LISTENER_CLAZZ, clock.getListener().getClass().getName());
            }
            intent.putExtra("extraBundle", hackBundle);

            PendingIntent pendingIntent = PendingIntent
                    .getBroadcast(Global.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            clock.setPendingIntent(pendingIntent);

            alarmManager.set(alarmType, fireTime, pendingIntent);

            synchronized (AlarmClockCollector.class) {
                ALARM_CLOCK_MAP.put(clock.getName(), clock);
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
            ALARM_CLOCK_MAP.remove(clock.getName());
        }
    }

    public static void cancelWhenArrived(AlarmClock clock) {
        synchronized (AlarmClockCollector.class) {
            clock.setPendingIntent(null);

            ALARM_CLOCK_MAP.remove(clock.getName());
        }
    }

    public static AlarmClock getClock(String name) {
        return ALARM_CLOCK_MAP.get(name);
    }

}
