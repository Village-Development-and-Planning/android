package com.puthuvaazhvu.mapping.views.fragments.option.adapters.children_question_as_options.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;

public class OptionsInputViewHolder extends RecyclerView.ViewHolder {
    private final TextView q_text;
    private final EditText editText;

    public OptionsInputViewHolder(View itemView) {
        super(itemView);

        q_text = itemView.findViewById(R.id.q_text);
        editText = itemView.findViewById(R.id.editText);
    }

    public void populateViews(String question) {
        q_text.setText(question);
    }

    public String getInput() {
        return editText.getText().toString();
    }
}