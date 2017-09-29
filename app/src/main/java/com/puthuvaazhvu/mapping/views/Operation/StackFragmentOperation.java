package com.puthuvaazhvu.mapping.views.Operation;

import android.support.v4.app.Fragment;

import com.puthuvaazhvu.mapping.views.managers.StackFragmentManager;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class StackFragmentOperation {
    private final StackFragmentManager stackFragmentManager;
    private final Operation operation;

    public StackFragmentOperation(StackFragmentManager stackFragmentManager) {
        this.stackFragmentManager = stackFragmentManager;
        this.operation = new Operation();
    }

    public void pushOperation(String tag, Fragment fragment) {
        stackFragmentManager.pushFragment(tag, fragment);
        operation.addFirst(fragment);
    }

    public void popOperation(String tag) {
        Fragment fragment = stackFragmentManager.getFragment(tag);
        stackFragmentManager.popFragment(fragment);
        operation.removeNode(fragment);
    }

    public void popManyOperation(ArrayList<String> tags) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (String t : tags) {
            Fragment fragment = stackFragmentManager.getFragment(t);
            fragments.add(fragment);
            operation.removeNode(fragment);
        }
        stackFragmentManager.popMany(fragments);
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
