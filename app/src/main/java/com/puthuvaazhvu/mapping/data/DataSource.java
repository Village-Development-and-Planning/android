package com.puthuvaazhvu.mapping.data;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/28/17.
 */

public interface DataSource<T> {
    interface DataSourceCallback<S> {
        void onLoaded(S data);
    }

    void getAllData(DataSourceCallback<ArrayList<T>> callback);

    void getData(String selection, DataSourceCallback<T> callback);

    void saveData(T data);
}
