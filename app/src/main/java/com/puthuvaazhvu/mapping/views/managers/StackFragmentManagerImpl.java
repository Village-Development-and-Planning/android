package com.puthuvaazhvu.mapping.views.managers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class StackFragmentManagerImpl implements StackFragmentManager {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private final FragmentManager fragmentManager;

    public StackFragmentManagerImpl(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void pushFragment(String tag, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        addFragment(fragmentTransaction, fragment, tag);
        fragments.add(fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void popFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragment != null) {
            removeFragment(fragmentTransaction, fragment);
            fragments.remove(fragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void popMany(ArrayList<Fragment> fragments) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment f : fragments) {
            removeFragment(fragmentTransaction, f);
            this.fragments.remove(f);
        }
        fragmentTransaction.commit();
    }

    @Override
    public Fragment getFragment(String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }

    @Override
    public int getStackCount() {
        return fragments.size();
    }

    private void addFragment(FragmentTransaction transaction, Fragment fragment, String tag) {
        transaction.add(fragment, tag);
    }

    private void removeFragment(FragmentTransaction transaction, Fragment fragment) {
        transaction.remove(fragment);
    }

}
