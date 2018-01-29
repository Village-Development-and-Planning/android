package com.puthuvaazhvu.mapping.views.testing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogicImplementation;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.BaseQuestionFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.QuestionDataFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.ShowTogetherQuestionCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.ShownTogetherFragment;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 1/10/18.
 */

public class ShownTogetherQuestionFragmentActivity extends AppCompatActivity
        implements ShowTogetherQuestionCommunication, QuestionDataFragmentCommunication,
        BaseQuestionFragmentCommunication {

    Question root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_individual_fragment);
        root = getData();

        addFragment();
    }

    private Question getData() {
        String questionString = Utils.readFromAssetsFile(this, "message_question.json");
        JsonParser jsonParser = new JsonParser();
        return QuestionUtils.populateQuestionFromJson(null, jsonParser.parse(questionString).getAsJsonObject());
    }

    @Override
    public void onBackPressedFromShownTogetherQuestion(Question question) {
        Timber.i("Back button pressed");
    }

    @Override
    public void onNextPressedFromShownTogetherQuestion(Question question) {
        Timber.i("Next button pressed");
    }

    @Override
    public Question getCurrentQuestionFromActivity() {
        return root;
    }

    private void addFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, new ShownTogetherFragment());
        fragmentTransaction.commit();
    }

    @Override
    public FlowLogic getFlowLogic() {
        return new FlowLogicImplementation();
    }
}
