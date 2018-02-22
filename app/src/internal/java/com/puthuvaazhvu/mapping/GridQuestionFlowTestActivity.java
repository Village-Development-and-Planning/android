package com.puthuvaazhvu.mapping;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.puthuvaazhvu.mapping.filestorage.StorageUtils;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.deserialization.SurveyGsonAdapter;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogicImplementation;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.BaseQuestionFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.ConfirmationQuestionCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.GridQuestionFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.QuestionDataFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.SingleQuestionFragmentCommunication;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 22/02/18.
 */

public class GridQuestionFlowTestActivity extends BaseQuestionFlowTestActivity implements
        SingleQuestionFragmentCommunication,
        QuestionDataFragmentCommunication,
        GridQuestionFragmentCommunication,
        BaseQuestionFragmentCommunication,
        ConfirmationQuestionCommunication {

    FlowLogic flowLogic;
    Survey survey;
    Question root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        surveyData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Survey>() {
                    @Override
                    public void accept(Survey survey) throws Exception {
                        GridQuestionFlowTestActivity.this.survey = survey;
                        GridQuestionFlowTestActivity.this.root = survey.getQuestion();
                        flowLogic = new FlowLogicImplementation(survey.getQuestion(), sharedPreferences);
                        loadFragment(flowLogic.getNext());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                    }
                });
    }

    private FlowLogic.FlowData getNext() {
        return flowLogic.getNext();
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
                                        GridQuestionFlowTestActivity.this, "grid_question.json"),
                                Survey.class
                        );
                e.onNext(survey);
                e.onComplete();
            }
        });
    }

    @Override
    public void onBackPressedFromGrid(Question question) {

    }

    @Override
    public void onNextPressedFromGrid(Question question) {
        Timber.i("All question answered..");
        Timber.i("Checking");
        checkSerializeAndDeserialize();
    }

    private void checkSerializeAndDeserialize() {
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
                        Timber.i("Received survey ID " + survey.getId());
                        Timber.i("Checking complete. Bye.");
                        Utils.showMessageToast("Success", GridQuestionFlowTestActivity.this);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                    }
                });
        ;
    }

    @Override
    public void onQuestionSelectedFromGrid(Question question, int pos) {
        loadFragment(flowLogic.moveToIndexInChild(pos));
    }

    @Override
    public void onNextPressedFromSingleQuestion(Question question, ArrayList<Option> response) {
        flowLogic.update(response);
        loadFragment(getNext());
    }

    @Override
    public void onBackPressedFromSingleQuestion(Question question) {
        Utils.showMessageToast("No cannot be pressed", this);
    }

    @Override
    public void onError(String message) {

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
        Utils.showMessageToast("No cannot be pressed", this);
    }
}
