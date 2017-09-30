package com.puthuvaazhvu.mapping.views.fragments.option.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.puthuvaazhvu.mapping.views.fragments.option.adapters.RadioButtonOptionsListAdapter;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.Data;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.Option;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.SelectedOption;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.SingleAnswer;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class RadioButtonOptionsList extends OptionsList {
    private RadioButtonOptionsListAdapter adapter;
    private Data data;

    public static RadioButtonOptionsList getInstance(Data data) {
        RadioButtonOptionsList radioButtonOptionsList = new RadioButtonOptionsList();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        radioButtonOptionsList.setArguments(bundle);
        return radioButtonOptionsList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = getArguments().getParcelable("data");
        adapter = new RadioButtonOptionsListAdapter(data.getOptions());
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
        SelectedOption selectedOption = null;
        for (Option o : options) {
            if (o.isSelected()) {
                selectedOption = new SelectedOption(o.getId(), o.getText());
                break;
            }
        }
        SingleAnswer singleAnswer = new SingleAnswer(data.getQuestionID(), data.getQuestionText(), selectedOption);
        data.setAnswer(singleAnswer);

        return data;
    }
}
