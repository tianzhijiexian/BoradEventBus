package kale.lib.eventbus.utils;

/**
 * @author Jack Tony
 * @date 2015/12/6
 */
public class SimpleMethod {

    public String name;

    public Class<?>[] parameterTypes;
    
    public Object[] params;

    public SimpleMethod(String name, Class<?>[] parameterTypes) {
        this.name = name;
        this.parameterTypes = parameterTypes;
    }

    public String getName() {
        return name;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public SimpleMethod setParams(Object[] params) {
        this.params = params;
        return this;
    }
}
