package kale.lib.eventbus;

import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kale.lib.eventbus.annotation.Subscriber;

/**
 * @author Jack Tony
 * @date 2015/12/2
 */
class SubscriberMethodUtil {

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TAG = SubscriberMethodUtil.class.getSimpleName();

    @CheckResult
    public static Map<String, List<SubscriberMethod>> getSubscribedMethods(Object subscriber) {
        final Map<String, List<SubscriberMethod>> methodsMap = new ArrayMap<>();
        Class<?> clazz = subscriber.getClass();
        while (clazz != null && !isSystemCls(clazz.getName())) {
            final Method[] allMethods = clazz.getDeclaredMethods();
            buildMap(methodsMap, allMethods);
            clazz = clazz.getSuperclass(); // 跳转到父类中继续，直到没有注册的方法
        }
        return methodsMap;
    }

    private static void buildMap(Map<String, List<SubscriberMethod>> methodsMap, Method[] allMethods) {
        Subscriber annotation;
        String key;
        for (Method method : allMethods) {
            annotation = method.getAnnotation(Subscriber.class);
            if (annotation != null) {
                key = annotation.tag(); // 获取方法的tag
                if (!TextUtils.isEmpty(key)) {
                    SubscriberMethod subscriberMethod = new SubscriberMethod(
                            method.getName(),
                            method.getParameterTypes());
                    
                    if (methodsMap.containsKey(key)) {
                        methodsMap.get(key).add(subscriberMethod);
                    } else {
                        ArrayList<SubscriberMethod> methods = new ArrayList<>();
                        methods.add(subscriberMethod);
                        methodsMap.put(key, methods);
                    }
                }
            }
        }
    }

    private static boolean isSystemCls(String name) {
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.");
    }

    /**
     * 通过intent得到一个{@link SubscriberMethod}
     */
    @Nullable
    @CheckResult
    public static SubscriberMethod getMatchedMethod(Map<String, List<SubscriberMethod>> methodMap, String key, Object[] params) {
        List<SubscriberMethod> methods = methodMap.get(key); // 找到所有标记tag的method
        if (methods != null) {
            for (SubscriberMethod method : methods) {
                // 只有参数类型和参数个数都匹配才算是匹配成功
                if (isMatch(method, params)) {
                    method.params = params;
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 如果是都是无参数，那么就直接匹配成功
     * 如果传入的参数和接收的参数的类型完全一致，匹配成功
     *
     * @return 是否匹配成功
     */
    private static boolean isMatch(SubscriberMethod method, Object[] params) {
        Class<?>[] targetClsSArr = method.parameterTypes;
        if (params.length != targetClsSArr.length) {
            return false;
        }

        for (int i = 0, paramsLength = params.length; i < paramsLength; i++) {
            if (params[i] == null) {
                continue;
            }
            Class<?> targetClz = Reflect.wrapper(targetClsSArr[i]);
            if (DEBUG) {
                Log.d(TAG, "收到 param type = " + params[i].getClass().getCanonicalName());
                Log.d(TAG, "应该 param type = " + targetClz.getCanonicalName());
            }

            if (!targetClz.isAssignableFrom(params[i].getClass())) {
                if (DEBUG) Log.e(TAG, "isMatch: 不匹配！！！");
                return false;
            }
        }
        return true;
    }
}
