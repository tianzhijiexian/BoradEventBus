package kale.lib.eventbus.utils;

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
public class MethodUtil {

    private static final boolean DEBUG = false;

    private static final String TAG = MethodUtil.class.getSimpleName();

    @CheckResult
    public static Map<String, List<SimpleMethod>> getSubscribedMethods(Object subscriber) {
        final Map<String, List<SimpleMethod>> methodsMap = new ArrayMap<>();
        Class<?> clazz = subscriber.getClass();
        // 查找类中符合要求的注册方法,直到Object类
        while (clazz != null && !isSystemCls(clazz.getName())) {
            final Method[] allMethods = clazz.getDeclaredMethods();
            buildMap(methodsMap, allMethods);
            clazz = clazz.getSuperclass();
        }
        return methodsMap;
    }

    private static void buildMap(Map<String, List<SimpleMethod>> methodsMap, Method[] allMethods) {
        Subscriber annotation;
        String key;
        for (Method method : allMethods) {
            annotation = method.getAnnotation(Subscriber.class);
            if (annotation != null) {
                key = annotation.tag(); // 获取方法的tag
                if (!TextUtils.isEmpty(key)) {
                    SimpleMethod simpleMethod = new SimpleMethod(method.getName(), method.getParameterTypes());
                    
                    if (methodsMap.containsKey(key)) {
                        methodsMap.get(key).add(simpleMethod);
                    } else {
                        ArrayList<SimpleMethod> methods = new ArrayList<>();
                        methods.add(simpleMethod);
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
     * 通过intent得到一个method的bean对象
     */
    @Nullable
    @CheckResult
    public static SimpleMethod getMatchedMethod(Map<String, List<SimpleMethod>> methodMap, String key, Object[] params) {
        List<SimpleMethod> methods = methodMap.get(key); // 找到所有标记tag的method
        if (methods != null) {
            for (SimpleMethod method : methods) {
                if (isMatch(method, params)) {
                    return method.setParams(params);
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
    private static boolean isMatch(SimpleMethod method, Object[] params) {
        Class<?>[] targetClsSArr = method.getParameterTypes();
        if (params.length != method.getParameterTypes().length) {
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
                if (DEBUG) {
                    Log.e(TAG, "isMatch: 不匹配！！！");
                }
                return false;
            } else {
                if (DEBUG) {
                    Log.d(TAG, "isMatch: 匹配成功");
                }
            }
        }
        return true;
    }
}
