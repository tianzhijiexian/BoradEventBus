package kale.lib.eventbus.rx;

import android.os.Parcel;
import android.os.Parcelable;

import rx.Observer;

/**
 * @author Jack Tony
 * @date 2015/12/2
 */
public class EventSubscriber<T> implements Parcelable, Observer<T> {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mObservable, 0);
    }

    protected EventSubscriber(Parcel in) {
        this.mObservable = in.readParcelable(EventObservable.class.getClassLoader());
    }

    public static final Creator<EventSubscriber> CREATOR = new Creator<EventSubscriber>() {
        public EventSubscriber createFromParcel(Parcel source) {
            return new EventSubscriber(source);
        }

        public EventSubscriber[] newArray(int size) {
            return new EventSubscriber[size];
        }
    };
}
