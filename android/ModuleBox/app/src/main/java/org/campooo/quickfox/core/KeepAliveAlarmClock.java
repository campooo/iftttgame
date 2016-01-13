package org.campooo.quickfox.core;

import android.content.Context;
import android.content.Intent;

import org.campooo.app.info.clock.AlarmClock;
import org.campooo.app.info.clock.AlarmClockCollector;
import org.campooo.app.info.clock.AlarmClockListener;
import org.campooo.quickfox.PushService;

/**
 * 保活器
 * <p/>
 * ckb on 16/1/13.
 */
public class KeepAliveAlarmClock implements AlarmClockListener {

    private static AlarmClock keepAliveClock;

    static {
        if (keepAliveClock == null) {
            keepAliveClock = new AlarmClock("keepAliveClock", QuickFoxConfiguration.getKeepAliveInterval(), new KeepAliveAlarmClock());
        }

    }

    private static StanzaWriter writer;

    @Override

    public boolean onClockArrived(Context context, Intent intent) {
        if (writer == null) {
            //重启
            PushService.startPushService(context);
            return true;
        }
        writer.sendBreatheStanza();

        return true;
    }

    static void setWriterAndStart(StanzaWriter writer) {
        KeepAliveAlarmClock.writer = writer;
        AlarmClockCollector.cancel(keepAliveClock);
        AlarmClockCollector.startAlarm(keepAliveClock);
    }
}
