package kale.eventbus;

import java.lang.reflect.Method;
import java.util.List;

import android.content.BroadcastReceiver;

/**
 * @author Kale
 * @date 2017/9/1
 */
abstract class InnerBroadcastReceiver extends BroadcastReceiver {

    private List<Method> methods;

    public void setMethodsToCache(List<Method> methods) {
        this.methods = methods;
    }

    public List<Method> getMethodsFromCache() {
        return methods;
    }
}
