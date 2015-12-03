package rx;

import java.io.Serializable;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * @author Jack Tony
 * @date 2015/12/2
 */
public class EventObservable implements Serializable {

    Observer subscriber;

    public Observer getSubscriber() {
        return subscriber;
    }

    public void subscribe(final Observer observer) {
        this.subscriber = observer;
    }

    public final <T> void subscribe(final Action1<T> onNext) {
        this.subscribe(onNext, null);
    }

    public final <T> void subscribe(final Action1<T> onNext, final Action1<Throwable> onError) {
        subscribe(onNext, onError, null);
    }

    public final <T> void subscribe(final Action1<T> onNext, final Action1<Throwable> onError, final Action0 onComplete) {
        subscriber = new Observer<T>() {
            @Override
            public void onCompleted() {
                if (onComplete != null) {
                    onComplete.call();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (onError != null) {
                    onError.call(e);
                }
            }

            @Override
            public void onNext(T t) {
                if (onNext != null) {
                    onNext.call(t);
                }
            }
        };
    }
}