package kale.eventbus;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import kale.lib.eventbus.EventBus;
import kale.lib.eventbus.annotation.Subscriber;

/**
 * @author Jack Tony
 * @date 2015/11/8
 */
public class SecondActivity extends Activity {

    private static final String TAG = "SecondActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.register(this);

        ((TextView) findViewById(R.id.desc_tv)).setText("第二个界面");
        findViewById(R.id.start_btn).setVisibility(View.GONE);

        EventBus.setTag("click").post("str", 1.3f);
        EventBus.setTag("go").post(123456);

    }

    @Subscriber(tag = "go")
    private void event(int i) {
        Log.d(TAG, "=====> tag = go: " + i);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.unregister(this);
    }

}
