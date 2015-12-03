package kale.lib.eventbus.rx;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import rx.Observer;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * @author Jack Tony
 * @date 2015/12/2
 */
public class EventObservable implements Parcelable {

    protected ParcelableObserver subscriber;

    public <T> void subscribe(@NonNull final Observer<T> observer) {
        this.subscriber = new ParcelableObserver<T>(){
            @Override
            protected void onCompleted() {
                observer.onCompleted();
            }

            @Override
            protected void onError(Throwable e) {
                observer.onError(e);
            }

            @Override
            protected void onNext(T o) {
                observer.onNext(o);
            }
        };
    }

    public final <T> void subscribe(final Action1<T> onNext) {
        this.subscribe(onNext, null);
    }

    public final <T> void subscribe(final Action1<T> onNext, final Action1<Throwable> onError) {
        subscribe(onNext, onError, null);
    }

    public final <T> void subscribe(final Action1<T> onNext, final Action1<Throwable> onError, final Action0 onComplete) {
        subscriber = new ParcelableObserver<T>() {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.subscriber, 0);
    }

    public EventObservable() {
    }

    protected EventObservable(Parcel in) {
        this.subscriber = in.readParcelable(ParcelableObserver.class.getClassLoader());
    }

    public static final Creator<EventObservable> CREATOR = new Creator<EventObservable>() {
        public EventObservable createFromParcel(Parcel source) {
            return new EventObservable(source);
        }

        public EventObservable[] newArray(int size) {
            return new EventObservable[size];
        }
    };
}