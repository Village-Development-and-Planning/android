package com.puthuvaazhvu.mapping.views.fragments.option.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.puthuvaazhvu.mapping.views.fragments.option.adapters.RadioButtonOptionsListAdapter;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.SingleOptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.SingleAnswerData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class RadioButtonOptionsListFragment extends OptionsListFragment {
    private RadioButtonOptionsListAdapter adapter;
    private OptionData optionData;

    public static RadioButtonOptionsListFragment getInstance(OptionData optionData) {
        RadioButtonOptionsListFragment radioButtonOptionsListFragment = new RadioButtonOptionsListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("optionData", optionData);
        radioButtonOptionsListFragment.setArguments(bundle);
        return radioButtonOptionsListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        optionData = getArguments().getParcelable("optionData");
        adapter = new RadioButtonOptionsListAdapter(optionData.getOptions());
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
    public OptionData getUpdatedData() {
        return populateAnswers();
    }

    private OptionData populateAnswers() {
        ArrayList<SingleOptionData> singleOptionDatas = optionData.getOptions();
        for (SingleOptionData o : singleOptionDatas) {
            if (o.isSelected()) {
                SingleAnswerData singleAnswer = new SingleAnswerData(optionData.getQuestionID()
                        , optionData.getQuestionText()
                        , o.getId()
                        , o.getText()
                        , o.getPosition());
                optionData.setAnswerData(singleAnswer);
                break;
            }
        }

        return optionData;
    }
}
