package org.campooo.modulebox;

import android.app.Activity;
import android.os.Bundle;

import org.campooo.R;
import org.campooo.app.Global;
import org.campooo.app.info.clock.AlarmClock;
import org.campooo.app.info.clock.AlarmClockCollector;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlarmClock clock = new AlarmClock("haha", 8000, new TestAlarm());
        AlarmClockCollector.startAlarm(clock);
    }

}
