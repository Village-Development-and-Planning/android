package com.puthuvaazhvu.mapping.Question.SingleQuestion;

import android.content.Context;
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
import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Options.OptionsFragment;
import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class QuestionFragment extends Fragment implements View.OnClickListener {
    TextView question_text;
    Button back_button;
    Button next_button;

    QuestionModal questionModal;
    OptionsFragment optionsFragment;

    QuestionFragmentCommunicationInterface communicationInterface;

    public static QuestionFragment getInstance(QuestionModal questionModal) {
        QuestionFragment questionFragment = new QuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("question_data", questionModal);

        questionFragment.setArguments(bundle);

        return questionFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof QuestionFragmentCommunicationInterface) {
            communicationInterface = (QuestionFragmentCommunicationInterface) context;
        }
    }

    public void setCommunicationInterface(QuestionFragmentCommunicationInterface communicationInterface) {
        this.communicationInterface = communicationInterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.question_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        questionModal = getArguments().getParcelable("question_data");

        question_text = view.findViewById(R.id.question_text);
        back_button = view.findViewById(R.id.back_button);
        next_button = view.findViewById(R.id.next_button);

        if (!questionModal.isNextPresent()) {
            next_button.setVisibility(View.INVISIBLE);
        }

        if (!questionModal.isPreviousPresent()) {
            back_button.setVisibility(View.INVISIBLE);
        }

        next_button.setOnClickListener(this);
        back_button.setOnClickListener(this);

        question_text.setText(questionModal.getText());

        populateOptionsContainer();
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

    private void populateOptionsContainer() {
        optionsFragment = null;
        optionsFragment = OptionsFragment.getInstance(questionModal.getOptionDataList(), questionModal.getOptionType());

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.options_container, optionsFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                communicationInterface.moveToPreviousQuestion(questionModal);
                break;
            case R.id.next_button:
                if (checkOptionData()) {
                    communicationInterface.moveToNextQuestion(questionModal, getSelectedOptions());
                } else {
                    // TODO: show error.
                    Log.e(Constants.LOG_TAG, "The options entered are not valid.");
                }
                break;
        }
    }
}
