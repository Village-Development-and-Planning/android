package com.puthuvaazhvu.mapping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.filestorage.StorageUtils;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.deserialization.SurveyGsonAdapter;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.SharedPreferenceUtils;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogicImplementation;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.BaseQuestionFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.ConfirmationQuestionCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.GridQuestionFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.QuestionDataFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.ShowTogetherQuestionCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.SingleQuestionFragmentCommunication;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.internal.Util;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 24/02/18.
 */

public class SurveyTestActivity extends AppCompatActivity
        implements
        SingleQuestionFragmentCommunication,
        QuestionDataFragmentCommunication,
        GridQuestionFragmentCommunication,
        ConfirmationQuestionCommunication,
        ShowTogetherQuestionCommunication,
        BaseQuestionFragmentCommunication {

    FlowLogic flowLogic;
    Survey survey;
    Question root;
    String surveyFilename;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.survey_test);

        findViewById(R.id.check_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkSerializationAndDeserialization();
                    }
                });

        surveyFilename = getIntent().getExtras().getString("file_name");

        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);

        surveyData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Survey>() {
                    @Override
                    public void accept(Survey survey) throws Exception {
                        SurveyTestActivity.this.survey = survey;
                        SurveyTestActivity.this.root = survey.getQuestion();

                        SharedPreferenceUtils.putSurveyID(sharedPreferences, "21010430");

                        flowLogic = new FlowLogicImplementation(survey.getQuestion(), sharedPreferences);

                        String auth = Utils.readFromAssetsFile(SurveyTestActivity.this, "auth.json");
                        JsonParser jsonParser = new JsonParser();
                        JsonObject authJson = jsonParser.parse(auth).getAsJsonObject();

                        flowLogic.setAuthJson(authJson);
                        loadFragment(flowLogic.getNext());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                    }
                });
    }

    void loadFragment(FlowLogic.FlowData flowData) {
        if (flowData == null) {
            Utils.showMessageToast("Survey Over!", SurveyTestActivity.this);
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, flowData.getFragment());
        fragmentTransaction.commit();
    }

    private Observable<Survey> surveyData() {
        return Observable.create(new ObservableOnSubscribe<Survey>() {
            @Override
            public void subscribe(ObservableEmitter<Survey> e) throws Exception {
                final GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Survey.class, new SurveyGsonAdapter());

                Gson gson = gsonBuilder.create();

                Survey survey =
                        gson.fromJson(
                                Utils.readFromAssetsFile(
                                        SurveyTestActivity.this, surveyFilename),
                                Survey.class
                        );
                e.onNext(survey);
                e.onComplete();
            }
        });
    }

    private void checkSerializationAndDeserialization() {
        StorageUtils.serialize(survey)
                .map(new Function<byte[], Survey>() {
                    @Override
                    public Survey apply(byte[] bytes) throws Exception {
                        return (Survey) StorageUtils.deserialize(bytes).blockingFirst();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Survey>() {
                    @Override
                    public void accept(Survey survey) throws Exception {
                        Utils.showMessageToast("Serialization and deserialization success."
                                , SurveyTestActivity.this);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                    }
                });
    }

    @Override
    public void onBackPressedFromGrid(Question question) {

    }

    @Override
    public void onNextPressedFromGrid(Question question) {
        loadFragment(flowLogic.finishCurrentAndGetNext());
    }

    @Override
    public void onQuestionSelectedFromGrid(Question question, int pos) {
        loadFragment(flowLogic.moveToIndexInChild(pos));
    }

    @Override
    public void onBackPressedFromShownTogetherQuestion(Question question) {

    }

    @Override
    public void onNextPressedFromShownTogetherQuestion(Question question) {
        loadFragment(flowLogic.finishCurrentAndGetNext());
    }

    @Override
    public FlowLogic getFlowLogic() {
        return flowLogic;
    }

    @Override
    public Question getCurrentQuestionFromActivity() {
        return flowLogic.getCurrent().getQuestion();
    }

    @Override
    public void onBackPressedFromConformationQuestion(Question question, ArrayList<Option> response) {
        loadFragment(flowLogic.finishCurrentAndGetNext());
    }

    @Override
    public void onNextPressedFromSingleQuestion(Question question, ArrayList<Option> response) {
        flowLogic.update(response);
        loadFragment(flowLogic.getNext());
    }

    @Override
    public void onBackPressedFromSingleQuestion(Question question) {

    }

    @Override
    public void onError(String message) {

    }
}
