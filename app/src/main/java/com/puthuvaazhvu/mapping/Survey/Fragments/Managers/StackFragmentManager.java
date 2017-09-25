package com.puthuvaazhvu.mapping.Survey.Fragments.Managers;

import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.Survey.Modals.QuestionModal;
import com.puthuvaazhvu.mapping.Survey.Adapters.StatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/21/17.
 */

/*
This helper class arranges the child fragments in a stack under the given fragment manager.
Pops the added fragments after all children questions are answered.
- 2.2.3 -
- 2.2.2.1 -
- 2.2.2 -
- 2.2.1 -
- 2.2 -
 */
public class StackFragmentManager<T> {
    private final ViewGroup container;
    private final StatePagerAdapter statePagerAdapter;
    private ArrayList<T> dataStack = new ArrayList<>();

    public StackFragmentManager(ViewGroup container
            , StatePagerAdapter statePagerAdapter) {
        this.statePagerAdapter = statePagerAdapter;
        this.container = container;
    }

    public void addFragment(T data) {
        push(data);
        Fragment f = (Fragment) statePagerAdapter.instantiateItem(container, data);
        statePagerAdapter.finishUpdate(container);
        statePagerAdapter.setPrimaryItem(container, data, f);
    }

    public ArrayList<T> removeAllFragments() {
        ArrayList<T> result = new ArrayList<>();
        T data;
        while ((data = pop()) != null) {
            statePagerAdapter.destroyItem(container, data);
            statePagerAdapter.finishUpdate(container);
            result.add(data);
        }
        return result;
    }

    public T removeFragment() {
        T data = pop();
        if (data != null) {
            statePagerAdapter.destroyItem(container, data);
            statePagerAdapter.finishUpdate(container);
        }
        return data;
    }

    private void push(T data) {
        dataStack.add(data);
    }

    private T pop() {
        int indexToBeRemoved = dataStack.size() - 1;
        if (indexToBeRemoved < 0) {
            // the list is empty
            return null;
        }
        // remove the last element. Replicate a linked-list.
        return dataStack.remove(dataStack.size() - 1);
    }

    public ArrayList<T> getDataStack() {
        return dataStack;
    }

//    private class StackFragmentStatePagerAdapter extends StatePagerAdapter {
//
//        public StackFragmentStatePagerAdapter(FragmentManager fragmentManager) {
//            super(fragmentManager);
//        }
//
//        @Override
//        public Fragment getItem(Object key) {
//            QuestionModal questionModal = (QuestionModal) key;
//            return NormalQuestionFragment.getInstance(questionModal);
//        }
//    }
}
