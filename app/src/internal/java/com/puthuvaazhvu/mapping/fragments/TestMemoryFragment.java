package com.puthuvaazhvu.mapping.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.filestorage.AnswerIO;
import com.puthuvaazhvu.mapping.filestorage.StorageIO;
import com.puthuvaazhvu.mapping.filestorage.StorageUtils;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.deserialization.SurveyGsonAdapter;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 21/02/18.
 */

public class TestMemoryFragment extends BaseFragment {
    private static final int ANSWERS_UPLOAD_COUNT = 200;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.memory_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView textView = view.findViewById(R.id.upload_answers_txt);

        textView.setText("Uploading answers count : " + ANSWERS_UPLOAD_COUNT);

        view.findViewById(R.id.upload_answers_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showLoading("Uploading answers");
                        uploadAnswersObservable()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        Timber.i("Upload status " + aBoolean);
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Timber.e(throwable);
                                        hideLoading();
                                    }
                                }, new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        hideLoading();
                                        Timber.i("DONE!!!");
                                        Utils.showMessageToast("Upload over.", getContext());
                                    }
                                });
                    }
                });

        view.findViewById(R.id.deserialize_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoading("Deserialize in progress...");
                        convertToSurvey()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<Survey>() {
                                    @Override
                                    public void accept(Survey survey) throws Exception {
                                        Timber.i("Converted successfully " + survey.getId());
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Timber.e(throwable);
                                        hideLoading();
                                    }
                                }, new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        hideLoading();
                                        Timber.i("DONE!!!");
                                    }
                                });
                    }
                });
    }

    private Observable<Survey> convertToSurvey() {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> e) throws Exception {
                InputStream is = getContext().getAssets().open("test_answer_snapshot.bytes");
                byte[] fileBytes = new byte[is.available()];
                is.read(fileBytes);
                is.close();

                e.onNext(fileBytes);
                e.onComplete();
            }
        }).map(new Function<byte[], Survey>() {
            @Override
            public Survey apply(byte[] bytes) throws Exception {
                return (Survey) StorageUtils.deserialize(bytes).blockingFirst();
            }
        });
    }

    private Observable<Boolean> uploadAnswersObservable() {
        List<Observable<Boolean>> observables = new ArrayList<>();

        for (int i = 0; i < ANSWERS_UPLOAD_COUNT; i++) {
            observables.add(saveDummyAnswerObservable());
        }

        return Observable.merge(observables);
    }

    private Observable<Boolean> saveDummyAnswerObservable() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                String surveyHousehold = Utils.readFromAssetsFile(getContext(), "survey_household.json");
                JsonParser jsonParser = new JsonParser();
                JsonObject surveyJson = jsonParser.parse(surveyHousehold).getAsJsonObject();

                final GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Survey.class, new SurveyGsonAdapter());
                gsonBuilder.setPrettyPrinting();

                Gson gson = gsonBuilder.create();

                Survey survey = gson.fromJson(surveyJson, Survey.class);

                addAnswersToTreeFromQuestion(survey.getQuestion());

                AnswerIO answerIO = new AnswerIO(survey.getId(),
                        survey.getName(), "" + System.currentTimeMillis());

                File file = answerIO.save(survey).blockingFirst();

                e.onNext(file.exists());
                e.onComplete();
            }
        });
    }

    public static void addAnswer(Question question) {
        Answer answer2 = Answer.createDummyAnswer(question);
        answer2.setDummy(false);
        question.addAnswer(answer2);
    }

    public static void addAnswersToTreeFromQuestion(Question node) {
        addAnswer(node);
        node.getCurrentAnswer().setExitTimestamp(System.currentTimeMillis());
        for (Question c : node.getCurrentAnswer().getChildren()) {
            addAnswersToTreeFromQuestion(c);
        }
    }
}
