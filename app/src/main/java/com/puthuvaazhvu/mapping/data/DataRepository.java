package com.puthuvaazhvu.mapping.data;

import android.content.Context;

import io.reactivex.Observable;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public abstract class DataRepository<T> {
    Context context;

    public DataRepository(Context context) {
        this.context = context;
    }

    public abstract Observable<T> getFromNetwork();

    public abstract Observable<T> getFromFileSystem();

}
