package kale.lib.eventbus.utils;

import android.support.annotation.CheckResult;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kale.lib.eventbus.annotation.Subscriber;

/**
 * @author Jack Tony
 * @date 2015/12/2
 */
public class MethodsMapUtil {

    @CheckResult
    public static Map<String, List<Method>> initMap(Object subscriber) {
        final Map<String, List<Method>> methodsMap = new ArrayMap<>();
        Class<?> clazz = subscriber.getClass();
        // 查找类中符合要求的注册方法,直到Object类
        while (clazz != null && !isSystemCls(clazz.getName())) {
            final Method[] allMethods = clazz.getDeclaredMethods();
            buildMap(methodsMap, allMethods);
            clazz = clazz.getSuperclass();
        }
        return methodsMap;
    }

    private static void buildMap(Map<String, List<Method>> methodsMap, Method[] allMethods) {
        Subscriber annotation;
        String key;
        for (Method method : allMethods) {
            annotation = method.getAnnotation(Subscriber.class);
            if (annotation != null) {
                key = annotation.tag(); // 获取方法的tag
                if (!TextUtils.isEmpty(key)) {
                    if (methodsMap.containsKey(key)) {
                        methodsMap.get(key).add(method);
                    } else {
                        ArrayList<Method> methods = new ArrayList<>();
                        methods.add(method);
                        methodsMap.put(key, methods);
                    }
                }
            }
        }
    }

    private static boolean isSystemCls(String name) {
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.");
    }
}
