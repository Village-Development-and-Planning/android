package com.puthuvaazhvu.mapping.views.fragments.option.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.Data;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.InputAnswer;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.SingleAnswer;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class EditTextOption extends Options {
    private Data data;
    private EditText editText;

    public static EditTextOption getInstance(Data data) {
        EditTextOption editTextOption = new EditTextOption();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        editTextOption.setArguments(bundle);
        return editTextOption;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.options_edt, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        data = getArguments().getParcelable("data");
        editText = view.findViewById(R.id.input_edit_text);
    }

    @Override
    public Data getUpdatedData() {
        String input = editText.getText().toString();
        SingleAnswer singleAnswer = new InputAnswer(data.getQuestionID(), data.getQuestionText(), input);
        data.setAnswer(singleAnswer);
        return data;
    }
}
