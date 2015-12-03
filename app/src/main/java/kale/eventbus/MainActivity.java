package kale.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import kale.lib.eventbus.EventBus;
import kale.lib.eventbus.annotation.Subscriber;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 一定要先初始化
        EventBus.install(this);

        
        EventBus.register(this);
        

                ((TextView) findViewById(R.id.desc_tv)).setText("第一个界面");
        
        findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
        


        EventBus.setTag("rx").postWithObserver("123").subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(TAG, "call: 接收到回调 str = " + s);
            }
        });
    }

    @Subscriber(tag = "rx")
    private void event_rx(String str, Observer<String> subscriber) {
        // 这里产生事件
        Log.d(TAG, "触发事件： str = " + str); // 123
        Observable.just("from rx").subscribe(subscriber);
    }

    @Subscriber(tag = "rx")
    private void event_rx() {
        Log.d(TAG, "event_rx: 接收到rx");
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
        EventBus.unregister(this);
    }
}
