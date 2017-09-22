package com.puthuvaazhvu.mapping.Survey.Adapters;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import static com.puthuvaazhvu.mapping.Constants.DEBUG;
import static com.puthuvaazhvu.mapping.Constants.LOG_TAG;

/**
 * Created by muthuveerappans on 9/21/17.
 */

public abstract class StatePagerAdapter extends BasePagerAdapter {
    private final FragmentManager fragmentManager;
    private FragmentTransaction currentTransaction = null;
    private Fragment currentPrimaryFragment = null;
    private HashMap<String, Fragment> fragments = new HashMap<>();
    private HashMap<String, Fragment.SavedState> savedState = new HashMap<>();

    public StatePagerAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void startUpdate(ViewGroup container) {
        if (container.getId() == View.NO_ID) {
            throw new IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id");
        }
    }

    /**
     * Returns the fragment specific to this key.
     *
     * @param key The key for this fragment.
     * @return Fragment to be shown
     */
    public abstract Fragment getItem(Object key);

    @Override
    public Object instantiateItem(ViewGroup container, Object key) {
        String k = key.toString();
        Fragment fragment = fragments.get(k);
        if (fragment != null) {
            return fragment;
        }

        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction();
        }

        fragment = getItem(k);
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        fragments.put(k, fragment);
        currentTransaction.add(container.getId(), fragment);

        return fragment;
    }

    public void destroyItem(ViewGroup container, Object key) {
        destroyItem(container, new Object[]{key});
    }

    public void destroyItem(ViewGroup container, Object[] keys) {
        for (int i = 0; i < keys.length; i++) {
            String k = keys[i].toString();
            Fragment fragment = fragments.get(k);
            if (fragment == null) {
                if (DEBUG)
                    Log.e(LOG_TAG, "Fragment not available to destroy. KEY: " + k);
            } else {
                destroyItem(container, k, fragment);
            }
        }
    }

    @Override
    public void destroyItem(ViewGroup container, Object key, Object object) {
        Fragment fragment = (Fragment) object;
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction();
        }
        savedState.put(key.toString(), fragment.isAdded() ? fragmentManager.saveFragmentInstanceState(fragment) : null);
        fragments.remove(key.toString());
        currentTransaction.remove(fragment);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    /**
     * This method should be called to make the fragment
     * that was added previously by {@link #instantiateItem(ViewGroup, Object)} visible to the user.
     *
     * @param container The container the fragment is put into.
     * @param key       The key that can identify the fragment.
     * @param object    The fragment itself.
     */
    @Override
    public void setPrimaryItem(ViewGroup container, Object key, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != currentPrimaryFragment) {
            if (currentPrimaryFragment != null) {
                currentPrimaryFragment.setMenuVisibility(false);
                currentPrimaryFragment.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            currentPrimaryFragment = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (currentTransaction != null) {
            currentTransaction.commitNowAllowingStateLoss();
            currentTransaction = null;
        }
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (savedState.size() > 0) {
            state = new Bundle();
            FragmentSaveState[] fss = FragmentSaveState.getObject(savedState);
            state.putParcelableArray("states", fss);
        }
        for (String entry : fragments.keySet()) {
            Fragment f = fragments.get(entry);
            if (f != null && f.isAdded()) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + entry;
                fragmentManager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            savedState.clear();
            fragments.clear();
            if (fss != null) {
                for (int i = 0; i < fss.length; i++) {
                    FragmentSaveState fragmentSaveState = (FragmentSaveState) fss[i];
                    savedState.put(fragmentSaveState.key, fragmentSaveState.fragmentSavedState);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key : keys) {
                if (key.startsWith("f")) {
                    Fragment f = fragmentManager.getFragment(bundle, key);
                    if (f != null) {
                        f.setMenuVisibility(false);
                        fragments.put(key, f);
                    } else {
                        Log.w(LOG_TAG, "Bad fragment at key " + key);
                    }
                }
            }
        }
    }

    // Saving the state of a fragment
    private static class FragmentSaveState implements Parcelable {
        Fragment.SavedState fragmentSavedState;
        String key;

        public FragmentSaveState(Fragment.SavedState fragmentSavedState, String key) {
            this.fragmentSavedState = fragmentSavedState;
            this.key = key;
        }

        public FragmentSaveState(Parcel in) {
            fragmentSavedState = in.readParcelable(Fragment.SavedState.class.getClassLoader());
            key = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(fragmentSavedState, i);
            parcel.writeString(key);
        }

        public static final Creator<FragmentSaveState> CREATOR = new Creator<FragmentSaveState>() {
            @Override
            public FragmentSaveState createFromParcel(Parcel in) {
                return new FragmentSaveState(in);
            }

            @Override
            public FragmentSaveState[] newArray(int size) {
                return new FragmentSaveState[size];
            }
        };

        public static FragmentSaveState[] getObject(HashMap<String, Fragment.SavedState> map) {
            ArrayList<FragmentSaveState> fragmentSaveStates = new ArrayList<>(map.size());
            for (String key : map.keySet()) {
                fragmentSaveStates.add(new FragmentSaveState(map.get(key), key));
            }
            return fragmentSaveStates.toArray(new FragmentSaveState[fragmentSaveStates.size()]);
        }
    }
}
