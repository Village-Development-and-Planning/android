package com.puthuvaazhvu.mapping.views.fragments.question.fragment.message;

import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.SingleOptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.InputAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.MultipleAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 11/6/17.
 */

public class Presenter implements Contract.UserAction {
    private final Question root;
    private ArrayList<Question> unmodifiedAdapterData;
    private final Contract.View view;

    public Presenter(Question root, Contract.View view) {
        this.root = root;
        this.unmodifiedAdapterData = new ArrayList<>();
        this.view = view;
    }

    @Override
    public void getAdapterData() {
        unmodifiedAdapterData.clear();

        unmodifiedAdapterData = populateAdapterData(unmodifiedAdapterData, root);

        ArrayList<QuestionData> modifiedAdapterData = new ArrayList<>();

        for (Question question : unmodifiedAdapterData) {
            modifiedAdapterData.add(QuestionData.adapter(question));
        }

        view.onAdapterFetched(modifiedAdapterData);
    }

    @Override
    public void updateAnswers(final ArrayList<QuestionData> adapterData) {

        for (QuestionData questionData : adapterData) {

            String rawNumber = questionData.getSingleQuestion().getRawNumber();
            ArrayList<SingleOptionData> loggedOption = questionData.getOptionOptionData().getSelectedOptions();

            MultipleAnswerData multipleAnswerData = new MultipleAnswerData(
                    questionData.getSingleQuestion().getId(),
                    questionData.getSingleQuestion().getText(),
                    loggedOption
            );

            for (Question question : unmodifiedAdapterData) {

                if (question.getRawNumber().equals(rawNumber)) {
                    // update the answer
                    Answer answer = new Answer(multipleAnswerData.getOption(), question);
                    question.setAnswer(answer);
                }
            }
        }

        view.onAnswersUpdated(root);
    }

    public static ArrayList<Question> populateAdapterData(ArrayList<Question> adapterData, Question root) {

        if (root.getCurrentAnswer() == null) {
            root = addDummyAnswerToRoot(root);
        }

        for (Question child : root.getCurrentAnswer().getChildren()) {
            adapterData.add(child);
            populateAdapterData(adapterData, child);
        }

        return adapterData;
    }

    private static Question addDummyAnswerToRoot(Question root) {
        InputAnswerData inputAnswerData = new InputAnswerData(root.getRawNumber(), root.getTextString(), "DUMMY");

        Answer answer = new Answer(inputAnswerData.getOption(), root);
        root.setAnswer(answer);

        return root;
    }
}
