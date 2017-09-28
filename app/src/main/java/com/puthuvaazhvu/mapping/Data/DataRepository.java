package com.puthuvaazhvu.mapping.Data;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/28/17.
 */

// Implementation for classes that manipulate data from a data source.
public interface DataRepository<T> {
    interface DataLoadedCallback<S> {
        void onDataLoaded(S data);
    }

    void getAllData(DataLoadedCallback<ArrayList<T>> callback);

    void getData(String selection, DataLoadedCallback<T> callback);

    void saveData(T data);

    void refreshData();
}
