package com.puthuvaazhvu.mapping.views.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.application.MappingApplication;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.Optional;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;
import com.puthuvaazhvu.mapping.views.activities.BaseDataActivity;
import com.puthuvaazhvu.mapping.views.activities.main.Presenter;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListActivity;
import com.puthuvaazhvu.mapping.views.activities.testing.TogetherQuestionTestFragmentActivity;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by muthuveerappans on 11/1/17.
 */

public class SettingsActivity extends BaseDataActivity implements View.OnClickListener {
    Button show_list_of_surveys_btn;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        progressDialog = new ProgressDialog();

        show_list_of_surveys_btn = findViewById(R.id.show_list_of_surveys_btn);
        show_list_of_surveys_btn.setOnClickListener(this);

        Button dumpSurveyButton = findViewById(R.id.dump_survey);
        dumpSurveyButton.setOnClickListener(this);
        dumpSurveyButton.setVisibility(View.GONE);

        findViewById(R.id.message_fragment_test).setOnClickListener(this);

        Switch isTamilSwitch = findViewById(R.id.language_switch);
        isTamilSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Constants.APP_LANGUAGE = Constants.Language.TAMIL;
                } else {
                    Constants.APP_LANGUAGE = Constants.Language.ENGLISH;
                }

                Utils.showMessageToast("Restarting", SettingsActivity.this);

                showListOfSurveyActivity();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_list_of_surveys_btn:
                Utils.showMessageToast("Restarting", this);
                showListOfSurveyActivity();
                break;
            case R.id.message_fragment_test:
                showMessageQuestionTestActivity();
                break;
            case R.id.dump_survey:
                // dumpSurvey();
                break;
        }
    }

    private void showLoading() {
        if (progressDialog.isVisible()) {
            progressDialog.dismiss();
        }
        progressDialog.setTextView("Loading...");
        progressDialog.show(getSupportFragmentManager(), "progress_dialog");
    }

    private void dismissProgressDialog() {
        if (progressDialog.isVisible())
            progressDialog.dismiss();
    }

//    private void dumpSurvey() {
//
//        Survey survey = applicationData.getSurvey();
//
//        if (survey != null) {
//            showLoading();
//            DataFileHelpers.dumpSurvey(survey, true)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(new Consumer<Optional>() {
//                        @Override
//                        public void accept(@NonNull Optional optional) throws Exception {
//                            dismissProgressDialog();
//                            Utils.showMessageToast("Survey dumped successfully", SettingsActivity.this);
//                        }
//                    });
//        } else {
//            Utils.showMessageToast("No survey present", this);
//        }
//    }

    private void showListOfSurveyActivity() {
        Intent intent = new Intent(this, SurveyListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showMessageQuestionTestActivity() {
        Intent intent = new Intent(this, TogetherQuestionTestFragmentActivity.class);
        startActivity(intent);
    }
}
