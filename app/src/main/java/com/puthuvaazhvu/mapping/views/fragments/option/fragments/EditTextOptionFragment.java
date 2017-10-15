package com.puthuvaazhvu.mapping.views.fragments.option.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.InputAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.SingleAnswerData;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class EditTextOptionFragment extends OptionsFragment {
    private OptionData optionData;
    private EditText editText;

    public static EditTextOptionFragment getInstance(OptionData optionData) {
        EditTextOptionFragment editTextOptionFragment = new EditTextOptionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("optionData", optionData);
        editTextOptionFragment.setArguments(bundle);
        return editTextOptionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.options_edt, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        optionData = getArguments().getParcelable("optionData");
        editText = view.findViewById(R.id.input_edit_text);
    }

    @Override
    public OptionData getUpdatedData() {
        String input = editText.getText().toString();
        if (!input.isEmpty()) {
            SingleAnswerData singleAnswer = new InputAnswerData(optionData.getQuestionID(), optionData.getQuestionText(), input);
            optionData.setAnswerData(singleAnswer);
        }
        return optionData;
    }
}
