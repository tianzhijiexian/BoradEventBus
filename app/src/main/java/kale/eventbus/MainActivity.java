package kale.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

        ((TextView) findViewById(R.id.desc_tv)).setText("第一个界面");
        findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });

        EventBus.post("click");
    }

    @Subscriber(tag = "second")
    private void event_list(List<MainActivity> strings) {
        Log.d(TAG, "=====> tag = second: " + strings.get(0));
    }

    @Subscriber(tag = "click")
    private void event_click(CharSequence str) {
        Log.d(TAG, "=====> tag = click: " + str);
    }


    @Subscriber(tag = "click")
    private void event_one() {
        Log.d(TAG, "=====> tag = click: 无参数");
    }

    @Subscriber(tag = "click")
    private void event_click(String str, float i) {
        Log.d(TAG, "=====> tag = click: " + str + " " + i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getInstance().unregister(this);
    }
}
