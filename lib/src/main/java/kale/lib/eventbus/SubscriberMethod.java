package kale.lib.eventbus;

/**
 * @author Kale
 * @date 2015/12/6
 */
class SubscriberMethod {

    public String name;

    public Class<?>[] parameterTypes;
    
    public Object[] params;

    public SubscriberMethod(String name, Class<?>[] parameterTypes) {
        this.name = name;
        this.parameterTypes = parameterTypes;
    }

}
