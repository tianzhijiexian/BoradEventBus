package kale.lib.eventbus.rx;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Jack Tony
 * @date 2015/12/3
 */
class ParcelableObserver<T> implements Parcelable{

    protected void onCompleted() {
        // do nothing
    }

    protected void onError(Throwable e) {
        // do noting
    }

    protected void onNext(T t) {
        // do noting
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public ParcelableObserver() {
    }

    protected ParcelableObserver(Parcel in) {
    }

    public static final Creator<ParcelableObserver> CREATOR = new Creator<ParcelableObserver>() {
        public ParcelableObserver createFromParcel(Parcel source) {
            return new ParcelableObserver(source);
        }

        public ParcelableObserver[] newArray(int size) {
            return new ParcelableObserver[size];
        }
    };
}
