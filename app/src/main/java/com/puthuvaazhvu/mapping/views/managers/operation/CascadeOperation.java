package com.puthuvaazhvu.mapping.views.managers.operation;

import android.support.v4.app.Fragment;

import com.puthuvaazhvu.mapping.views.managers.StackFragment;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class CascadeOperation {
    private final StackFragment stackFragment;
    private final Operation operation;

    public CascadeOperation(StackFragment stackFragment) {
        this.stackFragment = stackFragment;
        this.operation = new Operation();
    }

    public void pushOperation(String tag, Fragment fragment) {
        stackFragment.pushFragment(tag, fragment);
        operation.addFirst(fragment);
    }

    public void popOperation(String tag) {
        Fragment fragment = stackFragment.getFragment(tag);
        stackFragment.popFragment(fragment);
        operation.removeNode(fragment);
    }

    public void popManyOperation(String[] tags) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (String t : tags) {
            Fragment fragment = stackFragment.getFragment(t);
            fragments.add(fragment);
            operation.removeNode(fragment);
        }
        stackFragment.popMany(fragments);
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
