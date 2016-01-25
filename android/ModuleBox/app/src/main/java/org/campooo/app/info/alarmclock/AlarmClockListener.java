package org.campooo.app.info.alarmclock;

import android.content.Context;
import android.content.Intent;

/**
 * 不要写作内部类，有可能在反射的时候会没有权限
 * <p/>
 * ckb on 16/1/12.
 */
public interface AlarmClockListener {

    /**
     *  返回true 重置clock
     */
    public boolean onClockArrived(Context context, Intent intent);

}
