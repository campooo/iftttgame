package org.campooo.app.info.clock;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * ckb on 16/1/11.
 */
public class AlarmClock implements Parcelable {

    public static final String LISTENER_CLAZZ = "LISTENER";

    public static final String INSTANCE = "INSTANCE";

    private String name;

    private long interval = -1;

    private PendingIntent pendingIntent;

    private AlarmClockListener listener;

    public AlarmClock(String name, int interval, AlarmClockListener listener) {
        setName(name);
        setInterval(interval);
        setListener(listener);
    }

    protected AlarmClock(Parcel in) {
        name = in.readString();
        interval = in.readLong();
    }

    public static final Creator<AlarmClock> CREATOR = new Creator<AlarmClock>() {
        @Override
        public AlarmClock createFromParcel(Parcel in) {
            return new AlarmClock(in);
        }

        @Override
        public AlarmClock[] newArray(int size) {
            return new AlarmClock[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(interval);
    }
}
