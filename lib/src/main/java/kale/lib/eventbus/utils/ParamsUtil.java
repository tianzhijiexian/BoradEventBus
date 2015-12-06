package kale.lib.eventbus.utils;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;


/**
 * @author Jack Tony
 * @date 2015/11/7
 */
public class ParamsUtil {

    private static final boolean DEBUG = false;

    private static final String TAG = ParamsUtil.class.getSimpleName();

    private static final String PARAMS_COUNT = "params_count";

    /**
     * 通过参数来装配好一个intent对象
     *
     * @param tag    intent的action
     * @param params intent中要包含的数据对象
     */
    public static Intent makeIntentByParams(final String tag, final Object[] params) {
        Intent intent = new Intent();
        intent.setAction(tag);

        if (params[0] instanceof NullParam) {
            if (DEBUG) {
                Log.d(TAG, "无参数");
            }
            intent.putExtra(PARAMS_COUNT, 0);
        } else {
            intent.putExtra(PARAMS_COUNT, params.length);
            Object param;
            String key;

            for (int i = 0, paramsLength = params.length; i < paramsLength; i++) {
                key = String.valueOf(i);
                param = params[i];
                if (DEBUG) {
                    Log.d(TAG, "Post: param type is " + param);
                }

                if (param == null) {
                    if (DEBUG) {
                        Log.d(TAG, "param type is null，null也是参数的一种");
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
                    throw new IllegalArgumentException("参数必须是intent可以存入的类型");
                }
            }
        }
        return intent;
    }

    /**
     * 通过intent拿到传过来的参数
     *
     * @return intent中包含的参数数组，如果无参数，那么就是new object[0]
     */
    public static Object[] getParamsFromIntent(Intent intent) {
        int paramsCount = intent.getIntExtra(PARAMS_COUNT, 0);
        Object[] params = new Object[paramsCount];
        for (int i = 0; i < paramsCount; i++) {
            params[i] = intent.getExtras().get(String.valueOf(i));
        }
        return params;
    }

    public static class NullParam {

    }
}
