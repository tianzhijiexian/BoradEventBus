package kale.eventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.IntentFilter;
import android.text.TextUtils;

import kale.eventbus.annotation.EventSubscriber;

/**
 * @author Jack Tony
 * @date 2015/12/2
 */
class IntentFilterUtil {

    static IntentFilter initFilter(Object subscriber, Map<String, List<Method>> methodMap) {
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

    private static void initMethodsMap(Method[] allMethods, IntentFilter filter, Map<String, List<Method>> methodMap) {
        for (Method method : allMethods) {
            EventSubscriber annotation = method.getAnnotation(EventSubscriber.class);
            if (annotation != null) {
                String key = annotation.tag();
                // 获取方法的tag
                if (!TextUtils.isEmpty(key)) {
                    filter.addAction(key); // action = key
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

    private static boolean isSystemCls(String name) {
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.");
    }
}
