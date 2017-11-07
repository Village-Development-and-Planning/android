package com.puthuvaazhvu.mapping.views.fragments.question.fragment.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.ChildrenQuestionAsOptionFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.OptionsFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.InputAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.ConformationQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 11/1/17.
 */

public class MessageQuestionFragment extends ConformationQuestionFragment implements Contract.View {
    private QuestionData questionData;
    private Question root;

    private ChildrenQuestionAsOptionFragment fragment;

    private Contract.UserAction presenter;

    public static MessageQuestionFragment getInstance(Question questionReference, QuestionData questionData) {
        MessageQuestionFragment fragment = new MessageQuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("questionData", questionData);
        bundle.putParcelable("root", questionReference);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        questionData = getArguments().getParcelable("questionData");
        root = getArguments().getParcelable("root");

        String questionText = questionData.getSingleQuestion().getText();
        String rawNumber = questionData.getSingleQuestion().getRawNumber();

        String text = rawNumber + ". " + questionText;
        getQuestion_text().setText(text);

        presenter = new Presenter(root, this);
        presenter.getAdapterData();
    }

    private void loadOptionFragment(ArrayList<QuestionData> dataArrayList) {

        fragment = ChildrenQuestionAsOptionFragment
                .getInstance(dataArrayList);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.options_container, fragment);
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
            loadOptionFragment(adapterData);
        }
    }

    @Override
    public void onAnswersUpdated(Question root) {
        finishCurrentQuestion(questionData, false);
    }

}
