package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.CheckBoxOptionsList;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.EditTextOption;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.GpsOption;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.Options;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.RadioButtonOptionsList;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class SingleQuestion extends Question implements View.OnClickListener {
    private TextView question_text;
    private Button back_button;
    private Button next_button;
    private Data data;

    private Options optionFragment;

    public static SingleQuestion getInstance(Data data) {
        SingleQuestion fragment = new SingleQuestion();

        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.single_question, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        data = getArguments().getParcelable("data");

        question_text = view.findViewById(R.id.question_text);
        back_button = view.findViewById(R.id.back_button);
        next_button = view.findViewById(R.id.next_button);

        back_button.setOnClickListener(this);
        next_button.setOnClickListener(this);

        String questionText = data.getQuestion().getText();
        question_text.setText(questionText);

        loadCorrectOptionFragment();
    }

    /**
     * Helper to load the options based on the correct option type provided.
     */
    private void loadCorrectOptionFragment() {
        com.puthuvaazhvu.mapping.views.fragments.option.modals.Data optionData = data.getOptionData();
        com.puthuvaazhvu.mapping.views.fragments.option.modals.Data.Type type = optionData.getType();

        Options optionsFragment = null;

        switch (type) {
            case CHECKBOX_LIST:
                optionsFragment = CheckBoxOptionsList.getInstance(optionData);
                break;
            case RADIO_BUTTON_LIST:
                optionsFragment = RadioButtonOptionsList.getInstance(optionData);
                break;
            case BUTTON:
                optionsFragment = GpsOption.getInstance(optionData);
                break;
            case EDIT_TEXT:
                optionsFragment = EditTextOption.getInstance(optionData);
                break;
            default:
                Log.e(Constants.LOG_TAG, "Options type is NONE. So no options UI loaded.");
                return;
        }

        loadOptionFragment(optionsFragment);
    }

    private void loadOptionFragment(Options optionFragment) {
        this.optionFragment = null;
        this.optionFragment = optionFragment;

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.options_container, optionFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                backButtonPressedInsideQuestion(data);
                break;
            case R.id.next_button:
                sendQuestionToCaller(getUpdatedQuestion(), true, false);
                break;
        }
    }

    private Data getUpdatedQuestion() {
        if (optionFragment == null) {
            throw new IllegalArgumentException("The options fragment is null here. " +
                    "Either check if you have called the method too early or there is some internal problem");
        }
        com.puthuvaazhvu.mapping.views.fragments.option.modals.Data response = optionFragment.getUpdatedData();
        data.setResponseData(response);

        return data;
    }
}
