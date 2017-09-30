package kale.eventbus;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import static kale.eventbus.BroadEventBus.DEBUG;

/**
 * @author Jack Tony
 * @date 2015/11/7
 */
class ParamsUtil {

    private static final String TAG = BroadEventBus.class.getSimpleName();

    private static final String KEY_PARAMS_COUNT = "params_count";

    static Intent createIntentByParams(final String tag, final Object[] params) {
        Intent intent = new Intent();
        intent.setAction(tag);

        if (params[0] instanceof Reflect.NULL) {
            if (DEBUG) {
                Log.d(TAG, "No params");
            }
            intent.putExtra(KEY_PARAMS_COUNT, 0);
        } else {
            intent.putExtra(KEY_PARAMS_COUNT, params.length);

            Object param;
            String key;

            for (int i = 0, paramsLength = params.length; i < paramsLength; i++) {
                key = String.valueOf(i);
                param = params[i];

                if (DEBUG) {
                    Log.d(TAG, "Post: param = " + param);
                }

                if (param == null) {
                    if (DEBUG) {
                        Log.d(TAG, "param = null，null也是参数的一种");
                    }
                } else if (param instanceof CharSequence) {
                    intent.putExtra(key, (CharSequence) param);
                } else if (param instanceof CharSequence[]) {
                    intent.putExtra(key, (CharSequence[]) param);
                } else if (param instanceof Parcelable) {
                    intent.putExtra(key, (Parcelable) param);
                } else if (param instanceof Parcelable[]) {
                    intent.putExtra(key, (Parcelable[]) param);
                } else if (param instanceof Serializable[]) {
                    intent.putExtra(key, (Serializable[]) param);
                } else if (param instanceof Serializable) {
                    intent.putExtra(key, (Serializable) param);
                } else {
                    throw new IllegalArgumentException("参数必须是Intent可以存入的类型");
                }
            }
        }
        return intent;
    }

    static SimpleMethod getParamsFromIntent(Intent intent, List<Method> methods) {
        int paramsCount = intent.getIntExtra(KEY_PARAMS_COUNT, 0);
        for (Method method : methods) {
            if (paramsCount != method.getParameterTypes().length) {
                // 参数个数不同
                continue;
            }
            if (paramsCount == 0) {
                // 无参数，无需进行参数匹配，已经匹配成功
                return new SimpleMethod(method.getName(), new Object[0]);
            } else {
                // 有参数
                Object[] params = getParams(intent, paramsCount);
                if (isMatched(method, params)) {
                    // 传入的参数和接收的参数的类型完全一致，匹配成功
                    return new SimpleMethod(method.getName(), params);
                }
            }
        }
        return null;
    }

    private static Object[] getParams(Intent intent, int paramsCount) {
        Object[] params = new Object[paramsCount];
        for (int i = 0; i < paramsCount; i++) {
            params[i] = intent.getExtras().get(String.valueOf(i));
        }
        return params;
    }

    private static boolean isMatched(Method method, Object[] params) {
        Class<?>[] targetClsSArr = method.getParameterTypes();
        for (int i = 0, paramsLength = params.length; i < paramsLength; i++) {
            if (params[i] == null) {
                continue;
            }
            Class<?> targetClz = Reflect.wrapper(targetClsSArr[i]);

            if (!targetClz.isAssignableFrom(params[i].getClass())) {
                if (DEBUG) {
                    Log.e(TAG, "Param0" + (i + 1) + " not match, "
                            + "expect: " + targetClz.getCanonicalName()
                            + ", actual: " + params[i].getClass().getCanonicalName());
                }
                return false;
            } else {
                if (DEBUG) {
                    Log.d(TAG, "Param0" + (i + 1) + " is matched, type is " + targetClz.getCanonicalName());
                }
            }
        }
        return true;
    }

}
