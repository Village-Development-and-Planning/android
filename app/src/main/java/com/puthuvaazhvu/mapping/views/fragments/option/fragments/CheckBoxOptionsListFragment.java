package com.puthuvaazhvu.mapping.views.fragments.option.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.puthuvaazhvu.mapping.views.fragments.option.adapters.CheckBoxOptionsListAdapter;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.SingleOptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.MultipleAnswerData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class CheckBoxOptionsListFragment extends OptionsListFragment {
    private CheckBoxOptionsListAdapter adapter;
    private OptionData optionData;

    public static CheckBoxOptionsListFragment getInstance(OptionData optionData) {
        CheckBoxOptionsListFragment checkBoxOptionsList = new CheckBoxOptionsListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("optionData", optionData);
        checkBoxOptionsList.setArguments(bundle);
        return checkBoxOptionsList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        optionData = getArguments().getParcelable("optionData");
        adapter = new CheckBoxOptionsListAdapter(optionData.getOptions());
    }

    @Override
    public OptionData.Type getType() {
        return OptionData.Type.CHECKBOX_LIST;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public OptionData getUpdatedData() {
        return populateAnswers();
    }

    private OptionData populateAnswers() {
        ArrayList<SingleOptionData> singleOptionDatas = optionData.getOptions();
        ArrayList<SingleOptionData> selectedSingleOptionDatas = new ArrayList<>();
        for (SingleOptionData o : singleOptionDatas) {
            if (o.isSelected()) {
                selectedSingleOptionDatas.add(o);
            }
        }
        MultipleAnswerData multipleAnswer = new MultipleAnswerData(optionData.getQuestionID(), optionData.getQuestionText(), selectedSingleOptionDatas);
        optionData.setAnswerData(multipleAnswer);

        return optionData;
    }
}
