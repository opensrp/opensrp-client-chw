package org.smartregister.chw.util;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


public class RxUtils {

    public static <T> Observable<T> makeObservable(final Callable<T> func) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emmiter) throws Exception {
                if (emmiter != null) {
                    emmiter.onNext(func.call());
                }
                if (emmiter != null && !emmiter.isDisposed()) {
                    emmiter.onComplete();
                }

            }
        });
    }
}
