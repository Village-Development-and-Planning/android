package com.puthuvaazhvu.mapping.views.fragments.option.adapters.children_question_as_options;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.adapters.children_question_as_options.view_holders.OptionsInputViewHolder;
import com.puthuvaazhvu.mapping.views.fragments.option.adapters.children_question_as_options.view_holders.OptionsListViewHolder;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.SingleQuestion;

import java.util.ArrayList;

public class ChildQuestionsAsOptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<QuestionData> adapterData;

    public ChildQuestionsAsOptionAdapter(ArrayList<QuestionData> adapterData) {
        this.adapterData = adapterData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        OptionData.Type type = OptionData.Type.getType(viewType);

        switch (type) {
            case EDIT_TEXT:
                view = layoutInflater.inflate(R.layout.single_line_question_with_input, parent, false);
                viewHolder = new OptionsInputViewHolder(view);
                break;
            case CHECKBOX_LIST:
            case RADIO_BUTTON_LIST:
                view = layoutInflater.inflate(R.layout.single_line_question_with_options, parent, false);
                viewHolder = new OptionsListViewHolder(view, viewType);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        QuestionData question = adapterData.get(position);
        SingleQuestion singleQuestion = question.getSingleQuestion();
        OptionData optionData = question.getOptionOptionData();

        OptionData.Type type = OptionData.Type.getType(getItemViewType(position));

        switch (type) {
            case EDIT_TEXT:
                ((OptionsInputViewHolder) holder).populateViews(singleQuestion.getText(), optionData.getValidation());
                break;
            case CHECKBOX_LIST:
            case RADIO_BUTTON_LIST:
                ((OptionsListViewHolder) holder).populateViews(singleQuestion.getText(), optionData.getOptions());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return adapterData.size();
    }

    @Override
    public int getItemViewType(int position) {
        QuestionData question = adapterData.get(position);

        OptionData.Type type = question.getOptionOptionData().getType();

        return type.ordinal();
    }
}