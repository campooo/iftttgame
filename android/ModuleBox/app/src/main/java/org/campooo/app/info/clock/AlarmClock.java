package org.campooo.app.info.clock;

import android.app.PendingIntent;
import android.os.Parcelable;

/**
 * ckb on 16/1/11.
 */
public class AlarmClock {

    private String name;

    private long interval = -1;

    private PendingIntent pendingIntent;

    private AlarmClockListener listener;

    public AlarmClock(String name, int interval, AlarmClockListener listener) {
        setName(name);
        setInterval(interval);
        setListener(listener);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    public AlarmClockListener getListener() {
        return listener;
    }

    public void setListener(AlarmClockListener listener) {
        this.listener = listener;
    }

    public static final String LISTENER_CLAZZ = "LISTENER";

}
