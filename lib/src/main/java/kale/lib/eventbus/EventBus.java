package kale.lib.eventbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.SparseArray;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import kale.lib.eventbus.rx.EventObservable;
import kale.lib.eventbus.rx.EventSubscriber;
import kale.lib.eventbus.utils.MethodsMapUtil;
import kale.lib.eventbus.utils.ParamsUtil;
import kale.lib.eventbus.utils.Reflect;


/**
 * @author Jack Tony
 * @date 2015/9/12
 */
public class EventBus {

    private static String TAG = EventBus.class.getSimpleName();

    private static String mTag;
    
    private static EventBus instance;

    private static SparseArray<BroadcastReceiver> mSubscriberSArr;

    private static LocalBroadcastManager mLocalBroadcastManager;

    /**
     * 仅仅在第一次初始化的时候进行调用
     */
    public static void install(Context context) {
        if (instance == null) {
            instance = new EventBus(context);
        }
    }

    /**
     * 私有的构造方法
     */
    private EventBus(Context context) {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        mSubscriberSArr = new SparseArray<>();
    }

    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // post

    /**
     * 在调用后接着调用post方法来发送信息
     */
    @CheckResult
    public static EventBus setTag(@NonNull String tag) {
        mTag = tag;
        return instance;
    }

    public void post() {
        post(new ParamsUtil.NullParam());
    }

    @CheckResult
    public <T> EventObservable postWithObserver(Object... params) {
        EventObservable observable = new EventObservable();
        EventSubscriber<T> subscriber = new EventSubscriber<>(observable);

        params = Arrays.copyOf(params, params.length + 1); // 扩容
        params[params.length - 1] = subscriber;
        post(params);
        return observable;
    }

    public void post(Object... params) {
        Intent intent = ParamsUtil.initIntentByParams(mTag, params);
        if (mLocalBroadcastManager != null) {
            mLocalBroadcastManager.sendBroadcast(intent);
        } else {
            throw new NullPointerException("需要先调用EventBus的init()方法");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // register & unregister
    ///////////////////////////////////////////////////////////////////////////

    public static void register(@NonNull final Object subscriber) {
        // 一个类对应一个methodMap，这里存放该类标志有@subscriber的方法对象
        final Map<String, List<Method>> methodMap = MethodsMapUtil.initMap(subscriber);
        final IntentFilter filter = new IntentFilter();
        for (Map.Entry<String, List<Method>> entry : methodMap.entrySet()) {
            filter.addAction(entry.getKey());
        }
        
        // 如果发现当前类中没有注册监听，那么就不应该注册广播
        if (methodMap.size() != 0) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    List<Method> methods = methodMap.get(intent.getAction());
                    if (methods != null) {
                        MethodBean bean = ParamsUtil.getMethodBeanFromIntent(methods, intent);
                        if (bean != null) {
                            Reflect.on(subscriber).call(bean.name, bean.params);
                        }
                    }
                }
            };
            mSubscriberSArr.put(subscriber.hashCode(), receiver);
            mLocalBroadcastManager.registerReceiver(receiver, filter);
        }
    }

    public static void unregister(Object subscriber) {
        BroadcastReceiver receiver = mSubscriberSArr.get(subscriber.hashCode());
        if (receiver != null) {
            mSubscriberSArr.remove(subscriber.hashCode());
            mLocalBroadcastManager.unregisterReceiver(receiver);
        }
    }


    public static class MethodBean {

        public String name;

        public Object[] params;

        public MethodBean(String name, Object[] params) {
            this.name = name;
            this.params = params;
        }
    }
}
