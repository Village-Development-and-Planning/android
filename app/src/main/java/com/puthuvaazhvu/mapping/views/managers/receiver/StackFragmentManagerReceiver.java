package com.puthuvaazhvu.mapping.views.managers.receiver;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class StackFragmentManagerReceiver implements IStackFragmentManager {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private final FragmentManager fragmentManager;
    private final int containerViewID;

    public StackFragmentManagerReceiver(FragmentManager fragmentManager, int containerViewID) {
        this.fragmentManager = fragmentManager;
        this.containerViewID = containerViewID;
    }

    @Override
    public void pushFragment(String tag, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        addFragment(fragmentTransaction, fragment, tag);
        fragments.add(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void popFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragment != null) {
            removeFragment(fragmentTransaction, fragment);
            fragments.remove(fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void popMany(ArrayList<Fragment> fragments) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment f : fragments) {
            removeFragment(fragmentTransaction, f);
            this.fragments.remove(f);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public Fragment getFragment(String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }

    @Override
    public int getStackCount() {
        return fragments.size();
    }

    public ArrayList<Fragment> getAddedFragments() {
        return fragments;
    }

    private void addFragment(FragmentTransaction transaction, Fragment fragment, String tag) {
        transaction.add(containerViewID, fragment, tag);
    }

    private void removeFragment(FragmentTransaction transaction, Fragment fragment) {
        transaction.remove(fragment);
    }

}
