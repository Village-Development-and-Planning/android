package com.puthuvaazhvu.mapping.Survey.Adapters;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by muthuveerappans on 9/21/17.
 */

public abstract class BasePagerAdapter {
    private final DataSetObservable dataSetObservable = new DataSetObservable();

    public void startUpdate(ViewGroup container) {
    }

    /**
     * @param container
     * @param key       An unique key in the object's toString() method.
     * @return
     */
    public abstract Object instantiateItem(ViewGroup container, Object key);

    public abstract void destroyItem(ViewGroup container, Object key, Object object);

    public void finishUpdate(ViewGroup container) {
    }

    public void setPrimaryItem(ViewGroup container, Object key, Object object) {
    }

    public abstract boolean isViewFromObject(View view, Object object);

    public Parcelable saveState() {
        return null;
    }

    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        dataSetObservable.unregisterObserver(observer);
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        dataSetObservable.registerObserver(observer);
    }

    void setObserver(DataSetObserver observer) {
        synchronized (this) {
            registerDataSetObserver(observer);
        }
    }

    public void notifyDataSetChanged() {
        dataSetObservable.notifyChanged();
    }
}
