package kale.eventbus;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import kale.lib.eventbus.EventBus;
import kale.lib.eventbus.Subscriber;

/**
 * @author Jack Tony
 * @date 2015/11/8
 */
public class SecondActivity extends Activity {

    private static final String TAG = "activity-2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getInstance().register(this);

        EventBus.post("click", "haha", 1.3f);

        EventBus.post("ddd", 123456);
    }

    @Subscriber(tag = "ddd")
    private void ddd(int i) {
        Log.d(TAG, "ddd: i = " + i);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getInstance().unregister(this);
    }
}
