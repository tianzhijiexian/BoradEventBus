package rx;

import java.io.Serializable;

/**
 * @author Jack Tony
 * @date 2015/12/2
 */
public class EventSubscriber<T> implements Serializable, Observer<T>{

    private EventObservable mObservable;

    public EventSubscriber(EventObservable observable) {
        mObservable = observable;
    }

    @Override
    public void onCompleted() {
        mObservable.subscriber.onCompleted();
    }

    @Override
    public void onError(Throwable e) {
        mObservable.subscriber.onError(e);
    }

    public void onNext(T t) {
        mObservable.subscriber.onNext(t);
    }

}
