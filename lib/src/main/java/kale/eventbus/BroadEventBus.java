package kale.eventbus;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.CheckResult;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;


/**
 * @author Jack Tony
 * @date 2015/9/12
 */
public class BroadEventBus {

    public static boolean DEBUG = false;

    private static String TAG = BroadEventBus.class.getCanonicalName();

    private static String mTag;

    private static BroadEventBus mInstance;

    private static SparseArray<BroadcastReceiver> mSubscriberSArr;

    private static LocalBroadcastManager mLocalBroadcastManager;

    private BroadEventBus(Context context) {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        mSubscriberSArr = new SparseArray<>();
    }

    /**
     * 仅仅在第一次初始化的时候进行调用
     */
    public static void install(Context context, boolean isDebug) {
        DEBUG = isDebug;
        if (mInstance == null) {
            mInstance = new BroadEventBus(context);
        }
    }

    @CheckResult
    public static BroadEventBus setTag(String tag) {
        mTag = tag;
        return mInstance;
    }

    public void post() {
        post(new Reflect.NULL());
    }

    public void post(Object... params) {
        Intent intent = ParamsUtil.createIntentByParams(mTag, params);
        if (mLocalBroadcastManager != null) {
            mLocalBroadcastManager.sendBroadcast(intent);
        } else {
            throw new RuntimeException("需要先调用install()方法");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // register & unregister
    ///////////////////////////////////////////////////////////////////////////

    public static void register(final Object subscriber) {
        if (subscriber == null) {
            Log.e(TAG, "EventSubscriber is null");
            return;
        }
        // 一个类就一个methodMap
        final Map<String, List<Method>> methodMap = new HashMap<>();
        IntentFilter filter = IntentFilterUtil.initFilter(subscriber, methodMap);
        // 如果当前类中没有注册监听，那么就不应该注册广播
        if (methodMap.size() != 0) {
            BroadcastReceiver receiver = new InnerBroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    List<Method> methods = getMethodsFromCache();
                    if (methods == null) {
                        methods = methodMap.get(intent.getAction());
                        setMethodsToCache(methods);
                    }

                    if (methods != null) {
                        SimpleMethod method = ParamsUtil.getParamsFromIntent(intent, methods);
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

    public static void unregister(Object subscriber) {
        BroadcastReceiver receiver = mSubscriberSArr.get(subscriber.hashCode());
        if (receiver != null) {
            mSubscriberSArr.remove(subscriber.hashCode());
            mLocalBroadcastManager.unregisterReceiver(receiver);
        }
    }

}
