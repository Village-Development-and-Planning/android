package com.puthuvaazhvu.mapping.Survey.Fragments.DynamicTypes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Survey.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Survey.Options.OptionTypes;
import com.puthuvaazhvu.mapping.Survey.Options.OptionsFragment;
import com.puthuvaazhvu.mapping.Survey.Modals.QuestionType;
import com.puthuvaazhvu.mapping.Survey.Modals.QuestionModal;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.DataHelper;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.puthuvaazhvu.mapping.Survey.Options.OptionTypes.BUTTON;
import static com.puthuvaazhvu.mapping.Survey.Options.OptionTypes.MULTIPLE_SELECTION;
import static com.puthuvaazhvu.mapping.Survey.Options.OptionTypes.NONE;
import static com.puthuvaazhvu.mapping.Survey.Options.OptionTypes.SINGLE_SELECTION;
import static com.puthuvaazhvu.mapping.Survey.Options.OptionTypes.TEXT_FIELD;

/**
 * Created by muthuveerappans on 9/21/17.
 */

public class NormalQuestionFragment extends BaseDynamicTypeFragment {
    private TextView question_text;
    private Button back_button;
    private Button next_button;
    private QuestionModal questionModal;
    private OptionsFragment optionsFragment;

    public static NormalQuestionFragment getInstance(QuestionModal questionModal) {
        NormalQuestionFragment questionFragment = new NormalQuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("question_data", questionModal);

        questionFragment.setArguments(bundle);

        return questionFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionModal = getArguments().getParcelable("question_data");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.question_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        question_text = view.findViewById(R.id.question_text);
        back_button = view.findViewById(R.id.back_button);
        next_button = view.findViewById(R.id.next_button);

        next_button.setOnClickListener(this);
        back_button.setOnClickListener(this);

        if (!questionModal.isNextPresent()) {
            next_button.setVisibility(View.INVISIBLE);
        }

        if (!questionModal.isPreviousPresent()) {
            back_button.setVisibility(View.INVISIBLE);
        }

        question_text.setText(questionModal.getText());

        populateOptionsContainer();
    }

    private void populateOptionsContainer() {
        optionsFragment = null;
        optionsFragment = OptionsFragment.getInstance(questionModal.getOptionDataList(), getOptionType());

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.options_container, optionsFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private ArrayList<OptionData> getSelectedOptions() {
        return optionsFragment.getSelectedOptions();
    }

    private boolean checkOptionData() {
        List<OptionData> optionDataList = getSelectedOptions();
        boolean result = optionDataList.size() != 0;

        for (OptionData optionData : optionDataList) {
            result = !optionData.getText().isEmpty();
        }

        return result;
    }

    private void updateOptions() {
        ArrayList<OptionData> optionDataList = getSelectedOptions();
        DataHelper.updateOptions(questionModal, optionDataList);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                getDynamicFragmentTypeCommunicationInterface().OnShowPreviousFragment(questionModal);
                break;
            case R.id.next_button:
                if (checkOptionData()) {
                    // add the new options to the question data.
                    updateOptions();
                    getDynamicFragmentTypeCommunicationInterface().OnShowNextFragment(questionModal);
                } else
                    Utils.showErrorMessage(Constants.ErrorMessages.OPTIONS_NOT_SELECTED, getContext());
                break;
        }
    }

    public OptionTypes getOptionType() {
        QuestionType questionType = questionModal.getQuestionTypes();
        OptionTypes result = NONE;
        switch (questionType) {
            case INPUT_GPS:
                result = BUTTON;
                break;
            case INPUT_KEYBOARD:
                result = TEXT_FIELD;
                break;
            case MULTIPLE_CHOICE:
                result = MULTIPLE_SELECTION;
                break;
            case SINGLE_CHOICE:
                result = SINGLE_SELECTION;
                break;
        }
        return result;
    }
}
