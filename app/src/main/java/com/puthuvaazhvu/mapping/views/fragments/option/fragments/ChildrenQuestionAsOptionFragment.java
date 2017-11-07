package com.puthuvaazhvu.mapping.views.fragments.option.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.puthuvaazhvu.mapping.views.fragments.option.adapters.children_question_as_options.ChildQuestionsAsOptionAdapter;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 11/7/17.
 */

public class ChildrenQuestionAsOptionFragment extends OptionsListFragment {
    private ArrayList<QuestionData> questionDataArrayList;

    public static ChildrenQuestionAsOptionFragment getInstance(ArrayList<QuestionData> questionDataArrayList) {
        ChildrenQuestionAsOptionFragment fragment = new ChildrenQuestionAsOptionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", questionDataArrayList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        questionDataArrayList = getArguments().getParcelableArrayList("data");
    }

    @Override
    public OptionData.Type getType() {
        return OptionData.Type.MESSAGE;
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return new ChildQuestionsAsOptionAdapter(questionDataArrayList);
    }

    public ArrayList<QuestionData> getUpdatedData() {
        return questionDataArrayList;
    }
}
