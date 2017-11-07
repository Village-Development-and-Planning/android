package com.puthuvaazhvu.mapping.views.fragments.option.adapters.children_question_as_options.view_holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.custom_components.ChildLinearLayoutManager;
import com.puthuvaazhvu.mapping.views.fragments.option.adapters.CheckBoxOptionsListAdapter;
import com.puthuvaazhvu.mapping.views.fragments.option.adapters.RadioButtonOptionsListAdapter;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.SingleOptionData;

import java.util.ArrayList;

public class OptionsListViewHolder extends RecyclerView.ViewHolder {
    private final TextView q_text;
    private final RecyclerView linearRecyclerView;
    private final Context context;

    private final ArrayList<SingleOptionData> checkBoxAdapterData = new ArrayList<>();
    private final ArrayList<SingleOptionData> radioButtonAdapterData = new ArrayList<>();

    private final CheckBoxOptionsListAdapter checkBoxOptionsListAdapter;
    private final RadioButtonOptionsListAdapter radioButtonOptionsListAdapter;

    private final OptionData.Type questionType;

    public OptionsListViewHolder(View itemView, int type) {
        super(itemView);

        context = itemView.getContext();

        questionType = OptionData.Type.getType(type);

        q_text = itemView.findViewById(R.id.q_text);

        linearRecyclerView = itemView.findViewById(R.id.horizontal_list);
        linearRecyclerView.setLayoutManager(new ChildLinearLayoutManager(context));

        checkBoxOptionsListAdapter = new CheckBoxOptionsListAdapter(checkBoxAdapterData);
        radioButtonOptionsListAdapter = new RadioButtonOptionsListAdapter(radioButtonAdapterData);

        switch (questionType) {
            case CHECKBOX_LIST:
                linearRecyclerView.setAdapter(checkBoxOptionsListAdapter);
                break;
            case RADIO_BUTTON_LIST:
                linearRecyclerView.setAdapter(radioButtonOptionsListAdapter);
                break;
        }
    }

    public void populateViews(String questionText, ArrayList<SingleOptionData> optionArrayList) {
        q_text.setText(questionText);
        setAdapter(optionArrayList);
    }

    private void setAdapter(ArrayList<SingleOptionData> optionArrayList) {

        if (questionType == OptionData.Type.CHECKBOX_LIST) {

            checkBoxAdapterData.clear();
            checkBoxAdapterData.addAll(optionArrayList);
            checkBoxOptionsListAdapter.notifyDataSetChanged();

        } else if (questionType == OptionData.Type.RADIO_BUTTON_LIST) {

            radioButtonAdapterData.clear();
            radioButtonAdapterData.addAll(optionArrayList);
            radioButtonOptionsListAdapter.notifyDataSetChanged();

        }
    }
}