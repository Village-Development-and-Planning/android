package com.puthuvaazhvu.mapping.views.fragments.option.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.puthuvaazhvu.mapping.views.fragments.option.adapters.CheckBoxOptionsListAdapter;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.Data;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.Option;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.MultipleAnswer;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class CheckBoxOptionsListFragment extends OptionsListFragment {
    private CheckBoxOptionsListAdapter adapter;
    private Data data;

    public static CheckBoxOptionsListFragment getInstance(Data data) {
        CheckBoxOptionsListFragment checkBoxOptionsList = new CheckBoxOptionsListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        checkBoxOptionsList.setArguments(bundle);
        return checkBoxOptionsList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = getArguments().getParcelable("data");
        adapter = new CheckBoxOptionsListAdapter(data.getOptions());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    @Override
    public Data getUpdatedData() {
        return populateAnswers();
    }

    private Data populateAnswers() {
        ArrayList<Option> options = data.getOptions();
        ArrayList<Option> selectedOptions = new ArrayList<>();
        for (Option o : options) {
            if (o.isSelected()) {
                selectedOptions.add(o);
            }
        }
        MultipleAnswer multipleAnswer = new MultipleAnswer(data.getQuestionID(), data.getQuestionText(), selectedOptions);
        data.setAnswer(multipleAnswer);

        return data;
    }
}
