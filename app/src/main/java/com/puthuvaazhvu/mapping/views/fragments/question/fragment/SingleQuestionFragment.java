package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.CheckBoxOptionsListFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.EditTextOptionFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.GpsOptionFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.OptionsFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.RadioButtonOptionsListFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class SingleQuestionFragment extends SingleQuestionFragmentBase implements View.OnClickListener {
    private Data data;

    private OptionsFragment optionFragment;

    public static SingleQuestionFragment getInstance(Data data) {
        SingleQuestionFragment fragment = new SingleQuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        data = getArguments().getParcelable("data");

        String questionText = data.getQuestion().getText();
        getQuestion_text().setText(questionText);

        loadCorrectOptionFragment();
    }

    @Override
    public void onBackButtonPressed(View view) {
        backButtonPressedInsideQuestion(data);
    }

    @Override
    public void onNextButtonPressed(View view) {
        Data updatedData = getUpdatedQuestion();
        if (isQuestionAnswered(updatedData)) {
            sendQuestionToCaller(updatedData, false, true);
        } else {
            onError(Utils.getErrorMessage(R.string.options_not_entered_err, getContext()));
        }
    }

    /**
     * Helper to load the options based on the correct option type provided.
     */
    private void loadCorrectOptionFragment() {
        com.puthuvaazhvu.mapping.views.fragments.option.modals.Data optionData = data.getOptionData();
        com.puthuvaazhvu.mapping.views.fragments.option.modals.Data.Type type = optionData.getType();

        OptionsFragment optionsFragmentFragment = null;

        switch (type) {
            case CHECKBOX_LIST:
                optionsFragmentFragment = CheckBoxOptionsListFragment.getInstance(optionData);
                break;
            case RADIO_BUTTON_LIST:
                optionsFragmentFragment = RadioButtonOptionsListFragment.getInstance(optionData);
                break;
            case BUTTON:
                optionsFragmentFragment = GpsOptionFragment.getInstance(optionData);
                break;
            case EDIT_TEXT:
                optionsFragmentFragment = EditTextOptionFragment.getInstance(optionData);
                break;
            default:
                Log.e(Constants.LOG_TAG, "OptionsFragment type is NONE. So no options UI loaded.");
                return;
        }

        loadOptionFragment(optionsFragmentFragment, "option:" + optionData.getQuestionID());
    }

    private void loadOptionFragment(OptionsFragment optionFragment, String tag) {
        this.optionFragment = null;
        this.optionFragment = optionFragment;

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.options_container, optionFragment, tag);
        transaction.commit();
    }

    private Data getUpdatedQuestion() {
        if (optionFragment == null) {
            Log.e(Constants.LOG_TAG, "The options fragment is null. Possibly default case is executed in loadCorrectOptionFragment() method.");
            return data;
        }
        com.puthuvaazhvu.mapping.views.fragments.option.modals.Data response = optionFragment.getUpdatedData();
        data.setResponseData(response);

        return data;
    }

    private boolean isQuestionAnswered(Data data) {
        return data.getResponseData().getAnswer() != null;
    }
}
