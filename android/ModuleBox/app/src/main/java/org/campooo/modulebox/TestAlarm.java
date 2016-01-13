package org.campooo.modulebox;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.campooo.app.info.clock.AlarmClockListener;

/**
 * ckb on 16/1/13.
 */
public class TestAlarm implements AlarmClockListener {
    @Override
    public boolean onClockArrived(Context context, Intent intent) {
        Toast.makeText(context, "aaa", Toast.LENGTH_SHORT).show();
        return false;
    }
}
