package com.puthuvaazhvu.mapping.views.managers.operation;

import android.support.v4.app.Fragment;

import com.puthuvaazhvu.mapping.views.managers.StackFragmentManager;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

/**
 * Helper class that does stack operation on the fragments.
 * Uses {@link StackFragmentManager} to manipulate the fragments in a stack on the UI.
 */
public class CascadeOperation {
    private final StackFragmentManager stackFragmentManager;
    private final Operation operation;

    public CascadeOperation(StackFragmentManager stackFragmentManager) {
        this.stackFragmentManager = stackFragmentManager;
        this.operation = new Operation();
    }

    public void pushOperation(String tag, Fragment fragment) {
        stackFragmentManager.pushFragment(tag, fragment);
        operation.addFirst(fragment);
    }

    public void popOperation(String tag) {
        Fragment fragment = stackFragmentManager.getFragment(tag);
        operation.removeNode(fragment);
        stackFragmentManager.popFragment(fragment);
    }

    public void popManyOperation(String[] tags) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (String t : tags) {
            Fragment fragment = stackFragmentManager.getFragment(t);
            if (fragment!=null){
                fragments.add(fragment);
                operation.removeNode(fragment);
            }
        }
        stackFragmentManager.popMany(fragments);
    }

    public Fragment getFragment(String tag) {
        return stackFragmentManager.getFragment(tag);
    }

    public Fragment getHeadFragment() {
        return operation.getHead();
    }

    public Fragment getTailFragment() {
        return operation.getTail();
    }

    /**
     * Returns the count of the fragments present in the stack.
     *
     * @return currently present fragments count.
     */
    public int getCount() {
        return operation.size();
    }
}
