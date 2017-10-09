package com.puthuvaazhvu.mapping.views.managers;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public interface StackFragment {
    void pushFragment(String tag, Fragment fragment);

    void popFragment(Fragment fragment);

    void popFragment(String tag);

    Fragment getFragment(String tag);

    void popMany(ArrayList<Fragment> fragments);

    void popMany(String[] tags);

    int getStackCount();
}
