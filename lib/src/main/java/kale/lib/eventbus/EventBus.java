package kale.lib.eventbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kale.lib.eventbus.reflect.Reflect;


/**
 * @author Jack Tony
 * @date 2015/9/12
 */
public class EventBus {

    private String TAG = EventBus.class.getCanonicalName();

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
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
        return this;
    }

    private EventBus() {
        mSubscriberSArr = new SparseArray<>();
    }

    public static void post(String tag) {
        post(tag, new NullParam());
    }

    public static void post(String tag, Object... params) {
        Intent intent = ParamsHandler.initIntentByParams(tag, params);
        if (mLocalBroadcastManager != null) {
            mLocalBroadcastManager.sendBroadcast(intent);
        } else {
            throw new NullPointerException("需要先调用EventBus的init()方法");
        }
    }
    
    public void register(final Object subscriber) {
        if (subscriber == null) {
            Log.e(TAG, "Subscriber is null");
            return;
        }
        // 一个类就一个methodMap
        final Map<String, List<Method>> methodMap = new HashMap<>();
        IntentFilter filter = initFilter(subscriber, methodMap);
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

    private IntentFilter initFilter(Object subscriber, Map<String, List<Method>> methodMap) {
        IntentFilter filter = new IntentFilter();
        Class<?> clazz = subscriber.getClass();
        
        // 查找类中符合要求的注册方法,直到Object类
        while (clazz != null && !isSystemCls(clazz.getName())) {
            final Method[] allMethods = clazz.getDeclaredMethods();
            initMethodsMap(allMethods, filter, methodMap);
            clazz = clazz.getSuperclass();
        }
        return filter;
    }

    private void initMethodsMap(Method[] allMethods, IntentFilter filter, Map<String, List<Method>> methodMap) {
        for (Method method : allMethods) {
            Subscriber annotation = method.getAnnotation(Subscriber.class);
            if (annotation != null) {
                String key = annotation.tag();
                // 获取方法的tag
                if (!TextUtils.isEmpty(key)) {
                    filter.addAction(key);
                    if (methodMap.containsKey(key)) {
                        methodMap.get(key).add(method);
                    } else {
                        ArrayList<Method> methods = new ArrayList<>();
                        methods.add(method);
                        methodMap.put(key, methods);
                    }
                }
            }
        }
    }

    private boolean isSystemCls(String name) {
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.");
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
