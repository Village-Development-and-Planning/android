package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.CheckBoxOptionsListFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.EditTextOptionFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.GpsOptionFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.OptionsFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.RadioButtonOptionsListFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class SingleQuestionFragment extends SingleQuestionFragmentBase implements View.OnClickListener {
    private QuestionData questionData;

    private OptionsFragment optionFragment;

    public static SingleQuestionFragment getInstance(QuestionData questionData) {
        SingleQuestionFragment fragment = new SingleQuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("questionData", questionData);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        questionData = getArguments().getParcelable("questionData");

        String questionText = questionData.getSingleQuestion().getText();
        getQuestion_text().setText(questionText);

        loadCorrectOptionFragment();
    }

    @Override
    public void onBackButtonPressed(View view) {
        backButtonPressedInsideQuestion(questionData);
    }

    @Override
    public void onNextButtonPressed(View view) {
        QuestionData updatedQuestionData = getUpdatedQuestion();
        if (isQuestionAnswered(updatedQuestionData)) {
            sendQuestionToCaller(updatedQuestionData, false, true);
        } else {
            onError(Utils.getErrorMessage(R.string.options_not_entered_err, getContext()));
        }
    }

    /**
     * Helper to load the options based on the correct option type provided.
     */
    private void loadCorrectOptionFragment() {
        OptionData optionOptionData = questionData.getOptionOptionData();
        OptionData.Type type = optionOptionData.getType();

        OptionsFragment optionsFragmentFragment = null;

        switch (type) {
            case CHECKBOX_LIST:
                optionsFragmentFragment = CheckBoxOptionsListFragment.getInstance(optionOptionData);
                break;
            case RADIO_BUTTON_LIST:
                optionsFragmentFragment = RadioButtonOptionsListFragment.getInstance(optionOptionData);
                break;
            case BUTTON:
                optionsFragmentFragment = GpsOptionFragment.getInstance(optionOptionData);
                break;
            case EDIT_TEXT:
                optionsFragmentFragment = EditTextOptionFragment.getInstance(optionOptionData);
                break;
            default:
                Log.e(Constants.LOG_TAG, "OptionsFragment type is NONE. So no options UI loaded.");
                return;
        }

        loadOptionFragment(optionsFragmentFragment, "option:" + optionOptionData.getQuestionID());
    }

    private void loadOptionFragment(OptionsFragment optionFragment, String tag) {
        this.optionFragment = null;
        this.optionFragment = optionFragment;

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.options_container, optionFragment, tag);
        transaction.commit();
    }

    private QuestionData getUpdatedQuestion() {
        if (optionFragment == null) {
            Log.e(Constants.LOG_TAG, "The options fragment is null. Possibly default case is executed in loadCorrectOptionFragment() method.");
            return questionData;
        }
        OptionData response = optionFragment.getUpdatedData();
        questionData.setResponseData(response);

        return questionData;
    }

    private boolean isQuestionAnswered(QuestionData questionData) {
        return questionData.getResponseData().getAnswerData() != null
                && !questionData.getResponseData().getAnswerData().getOption().isEmpty();
    }
}
