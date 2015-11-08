package kale.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import kale.lib.eventbus.EventBus;
import kale.lib.eventbus.Subscriber;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 一定要先初始化
        EventBus.getInstance().init(this);


        EventBus.getInstance().register(this);

        startActivity(new Intent(this, SecondActivity.class));
    }

    @Subscriber(tag = "haha")
    private void event_list(List<MainActivity> strings) {
        Log.d(TAG, "event_list: ------ " + strings.get(0));
    }

    @Subscriber(tag = "click")
    private void event_click(CharSequence str) {
        Log.d(TAG, "click--------" + str);
    }


    @Subscriber(tag = "click")
    private void event_one() {
        Log.d(TAG, "event_one: one----");
    }

    @Subscriber(tag = "click")
    private void event_click(String str, float i) {
        Log.d(TAG, "click--------" + str + i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getInstance().unregister(this);
    }
}
