package com.puthuvaazhvu.mapping.views.activities.testing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.FragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.message.Contract;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.message.MessageQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.message.Presenter;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 11/7/17.
 */

public class MessageQuestionTestFragmentActivity
        extends IndividualFragmentTestingActivity
        implements FragmentCommunicationInterface {
    private Question root;
    private QuestionData questionData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        root = getData();
        questionData = QuestionData.adapter(root);

        super.onCreate(savedInstanceState);
    }

    @Override
    public Fragment getFragment() {
        return MessageQuestionFragment.getInstance(root, questionData);
    }

    private Question getData() {
        String questionString = Utils.readFromAssetsFile(this, "message_question.json");
        JsonParser jsonParser = new JsonParser();
        return Question.populateQuestion(jsonParser.parse(questionString).getAsJsonObject());
    }

    @Override
    public void onQuestionAnswered(QuestionData questionData, boolean isNewRoot) {
    }

    @Override
    public void finishCurrentQuestion(QuestionData questionData, boolean shouldLogOptions) {
        Timber.i("Question is DONE!!!");
    }

    @Override
    public void onBackPressedFromQuestion(QuestionData currentQuestionData) {

    }

    @Override
    public void onErrorWhileAnswering(String message) {

    }
}
