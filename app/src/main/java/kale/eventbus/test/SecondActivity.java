package kale.eventbus.test;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import kale.eventbus.BroadEventBus;
import kale.eventbus.R;


/**
 * @author Jack Tony
 * @date 2015/11/8
 */
public class SecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no_param_btn:
                BroadEventBus.setTag(EventTag.CUSTOM_EVENT).post();
                break;
            case R.id.string_btn:
                BroadEventBus.setTag(EventTag.CUSTOM_EVENT).post("kale");
                break;
            case R.id.more_param_btn:
                BroadEventBus.setTag(EventTag.CUSTOM_EVENT).post("kale", 31f);
                break;
            case R.id.list_btn:
                ArrayList<String> strings = new ArrayList<>();
                strings.add("Jack");
                BroadEventBus.setTag(EventTag.CUSTOM_EVENT).post(strings);
                break;

            case R.id.thread_btn:
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        BroadEventBus.setTag(EventTag.CUSTOM_EVENT).post("Thread: " + Thread.currentThread().getName());
                    }
                }.start();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadEventBus.unregister(this);
    }

}
