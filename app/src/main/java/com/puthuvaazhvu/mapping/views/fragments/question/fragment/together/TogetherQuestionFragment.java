package com.puthuvaazhvu.mapping.views.fragments.question.fragment.together;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.ChildrenQuestionAsOptionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.ConformationQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.QuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.SingleQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.SingleQuestionFragmentBase;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 11/1/17.
 */

public class TogetherQuestionFragment extends SingleQuestionFragment implements Contract.View {
    private QuestionData questionData;
    private Question root;

    private TextView question_text;
    private Button back_button;
    private Button next_button;

    private ChildrenQuestionAsOptionFragment fragment;

    private Contract.UserAction presenter;

    public static TogetherQuestionFragment getInstance(Question questionReference, QuestionData questionData) {
        TogetherQuestionFragment fragment = new TogetherQuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("questionData", questionData);
        bundle.putParcelable("root", questionReference);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.together_question, container, false);
        question_text = view.findViewById(R.id.question_text);
        back_button = view.findViewById(R.id.back_button);
        next_button = view.findViewById(R.id.next_button);

        back_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        questionData = getArguments().getParcelable("questionData");
        root = getArguments().getParcelable("root");

        String questionText = questionData.getSingleQuestion().getText();
        String rawNumber = questionData.getSingleQuestion().getRawNumber();

        String text = rawNumber + ". " + questionText;
        question_text.setText(text);

        presenter = new Presenter(root, this);
        presenter.getAdapterData();

        loadCorrectOptionFragment(questionData);

        // hide if the question type is message
        if (root.getFlowPattern().getQuestionFlow().getUiMode() == QuestionFlow.UI.MESSAGE) {
            view.findViewById(R.id.options_container).setVisibility(View.GONE);
        }
    }

    private void loadTogetherQuestionFragment(ArrayList<QuestionData> dataArrayList) {

        fragment = ChildrenQuestionAsOptionFragment
                .getInstance(dataArrayList);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.together_option_container, fragment);
        transaction.commit();
    }

    @Override
    public void onBackButtonPressed(View view) {
        backButtonPressedInsideQuestion(questionData);
    }

    @Override
    public void onNextButtonPressed(View view) {
        presenter.updateAnswers(fragment.getUpdatedData());
    }

    @Override
    public void onAdapterFetched(ArrayList<QuestionData> adapterData) {
        if (getView() != null) {
            loadTogetherQuestionFragment(adapterData);
        }
    }

    @Override
    public void onAnswersUpdated(Question root) {
        finishCurrentQuestion(questionData, false);
    }

}
