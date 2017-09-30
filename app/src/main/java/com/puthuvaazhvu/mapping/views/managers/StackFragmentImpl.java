package com.puthuvaazhvu.mapping.views.managers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class StackFragmentImpl implements StackFragment {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private final FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    public StackFragmentImpl(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void pushFragment(String tag, Fragment fragment) {
        addFragment(fragment, tag);
        fragments.add(fragment);
        commitTransaction();
    }

    @Override
    public void popFragment(Fragment fragment) {
        removeFragment(fragment);
        fragments.remove(fragment);
        commitTransaction();
    }

    @Override
    public Fragment getFragment(String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }

    @Override
    public int getStackCount() {
        return fragments.size();
    }

    @Override
    public void popMany(ArrayList<Fragment> fragments) {
        for (Fragment f : fragments) {
            removeFragment(f);
            this.fragments.remove(f);
        }
        commitTransaction();
    }

    private void addFragment(Fragment fragment, String tag) {
        if (fragmentTransaction == null)
            fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, tag);
    }

    private void removeFragment(Fragment fragment) {
        if (fragmentTransaction == null)
            fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
    }

    private void commitTransaction() {
        if (fragmentTransaction != null) {
            fragmentTransaction.commit();
        } else {
            throw new IllegalArgumentException("fragment transaction is not initialized.");
        }
    }
}
