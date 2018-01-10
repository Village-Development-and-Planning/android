package com.puthuvaazhvu.mapping.views.testing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.QuestionDataFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.SingleQuestionFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.ShownTogetherFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.SingleQuestionFragment;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 1/10/18.
 */

public class MultipleChoiceQuestionFragmentTesting extends AppCompatActivity
        implements SingleQuestionFragmentCommunication,
        QuestionDataFragmentCommunication {
    Question root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_individual_fragment);

        root = getData();

        addFragment();
    }

    private Question getData() {
        String questionString = Utils.readFromAssetsFile(this, "multiple_choice_with_options_count.json");
        JsonParser jsonParser = new JsonParser();
        return Question.populateQuestion(jsonParser.parse(questionString).getAsJsonObject());
    }

    private void addFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, new SingleQuestionFragment());
        fragmentTransaction.commit();
    }

    @Override
    public Question getCurrentQuestionFromActivity() {
        return root;
    }

    @Override
    public void onNextPressedFromSingleQuestion(Question question, ArrayList<Option> response) {
        Timber.i("Next button pressed");
    }

    @Override
    public void onBackPressedFromSingleQuestion(Question question) {
        Timber.i("Back button pressed");
    }

    @Override
    public void onError(String message) {
        Timber.i(message);
    }
}
