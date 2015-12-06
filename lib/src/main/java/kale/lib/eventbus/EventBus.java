package kale.lib.eventbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import kale.lib.eventbus.rx.EventObservable;
import kale.lib.eventbus.rx.EventObserver;
import kale.lib.eventbus.utils.MethodUtil;
import kale.lib.eventbus.utils.ParamsUtil;
import kale.lib.eventbus.utils.Reflect;
import kale.lib.eventbus.utils.SimpleMethod;


/**
 * @author Jack Tony
 * @date 2015/9/12
 */
public class EventBus {

    private static String TAG = EventBus.class.getSimpleName();

    private static String mTag;

    private static EventBus instance;

    private static LocalBroadcastManager mLocalBroadcastManager;

    private static SparseArray<BroadcastReceiver> mSubscriberSArr;

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
    // post
    ///////////////////////////////////////////////////////////////////////////

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
        EventObserver<T> observer = new EventObserver<>(observable);

        params = Arrays.copyOf(params, params.length + 1); // 扩容
        params[params.length - 1] = observer;
        post(params);
        return observable;
    }

    public void post(Object... params) {
        Intent intent = ParamsUtil.makeIntentByParams(mTag, params);
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
        final Map<String, List<SimpleMethod>> methodMap = MethodUtil.getSubscribedMethods(subscriber);
        final IntentFilter filter = new IntentFilter();
        for (Map.Entry<String, List<SimpleMethod>> entry : methodMap.entrySet()) {
            filter.addAction(entry.getKey());
        }

        if (methodMap.size() != 0) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // 设计原则：一个[tag : params]只对应一个方法
                    Object[] params = ParamsUtil.getParamsFromIntent(intent);
                    SimpleMethod method = MethodUtil.getMatchedMethod(methodMap, intent.getAction(), params);
                    if (method != null) {
                        Reflect.on(subscriber).call(method.name, method.params);
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

}
