package kale.lib.eventbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.CheckResult;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kale.lib.eventbus.utils.IntentFilterUtil;
import rx.EventObservable;
import rx.EventSubscriber;


/**
 * @author Jack Tony
 * @date 2015/9/12
 */
public class EventBus {

    private String TAG = EventBus.class.getCanonicalName();

    private static String mTag;
    
    private static EventBus instance;

    private SparseArray<BroadcastReceiver> mSubscriberSArr;

    private static LocalBroadcastManager mLocalBroadcastManager;

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public EventBus init(Context context) {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        return this;
    }

    private EventBus() {
        mSubscriberSArr = new SparseArray<>();
    }

    @CheckResult
    public static EventBus setTag(String tag) {
        mTag = tag;
        return instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    // post
    ///////////////////////////////////////////////////////////////////////////

    public static void post(String tag) {
        post(tag, new NullParam());
    }

    public static <T> EventObservable postWithObserver(String tag, Object... params) {
        EventObservable observable = new EventObservable();
        EventSubscriber<T> subscriber = new EventSubscriber<T>(observable);

        params = Arrays.copyOf(params, params.length + 1); // 扩容
        params[params.length - 1] = subscriber;
        post(tag, params);
        return observable;
    }

    public static void post(String tag, Object... params) {
        Intent intent = ParamsHandler.initIntentByParams(tag, params);
        if (mLocalBroadcastManager != null) {
            mLocalBroadcastManager.sendBroadcast(intent);
        } else {
            throw new NullPointerException("需要先调用EventBus的init()方法");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // register & unregister
    ///////////////////////////////////////////////////////////////////////////

    public void register(final Object subscriber) {
        if (subscriber == null) {
            Log.e(TAG, "Subscriber is null");
            return;
        }
        // 一个类就一个methodMap
        final Map<String, List<Method>> methodMap = new HashMap<>();
        IntentFilter filter = IntentFilterUtil.initFilter(subscriber, methodMap);
        // 如果当前类中没有注册监听，那么就不应该注册广播
        if (methodMap.size() != 0) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    List<Method> methods = methodMap.get(intent.getAction());
                    if (methods != null) {
                        MyMethod method = ParamsHandler.getParamsFromIntent(methods, intent);
                        if (method != null) {
                            Reflect.on(subscriber).call(method.name, method.params);
                        }
                    }
                }
            };
            mSubscriberSArr.put(subscriber.hashCode(), receiver);
            mLocalBroadcastManager.registerReceiver(receiver, filter);
        }
    }

    public void unregister(Object subscriber) {
        BroadcastReceiver receiver = mSubscriberSArr.get(subscriber.hashCode());
        if (receiver != null) {
            mSubscriberSArr.remove(subscriber.hashCode());
            mLocalBroadcastManager.unregisterReceiver(receiver);
        }
    }


    static class NullParam {}

    static class MyMethod {

        public String name;

        public Object[] params;

        public MyMethod(String name, Object[] params) {
            this.name = name;
            this.params = params;
        }
    }
}
