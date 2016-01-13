package org.campooo.app.info.clock;

import android.content.Context;
import android.content.Intent;

/**
 * ckb on 16/1/12.
 */
public interface AlarmClockListener {

    public boolean onClockArrived(Context context , Intent intent);

}
