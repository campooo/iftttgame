package org.campooo.app.info.clock;

import android.app.PendingIntent;

/**
 * ckb on 16/1/11.
 */
/* package */ class AlarmClock {

    public static final String ACTION_PREFIX = "org.campooo.alarm.";

    private long clockId;

    private String action;

    private long interval = -1;

    private PendingIntent pendingIntent;

    public AlarmClock(String action, int interval) {
        setAction(ACTION_PREFIX + action);
        setInterval(interval);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getClockId() {
        return clockId;
    }

    public void setClockId(long clockId) {
        this.clockId = clockId;
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
}
