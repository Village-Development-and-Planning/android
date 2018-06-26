package org.ptracking.vdp.views.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import org.ptracking.vdp.R;
import org.ptracking.vdp.modals.Survey;
import org.ptracking.vdp.modals.surveyorinfo.SurveyorInfoFromAPI;
import org.ptracking.vdp.utils.DialogHandler;
import org.ptracking.vdp.utils.PauseHandler;
import org.ptracking.vdp.utils.Utils;
import org.ptracking.vdp.views.activities.MenuActivity;
import org.ptracking.vdp.views.activities.SplashActivity;
import org.ptracking.vdp.views.activities.modals.CurrentSurveyInfo;
import org.ptracking.vdp.views.activities.upload.UploadActivity;
import org.ptracking.vdp.views.activities.main.MainActivity;
import org.ptracking.vdp.views.dialogs.ProgressDialog;

/**
 * Created by muthuveerappans on 07/06/18.
 */

public class HomeActivity extends MenuActivity implements View.OnClickListener, Contract.View {
    ProgressDialog progressDialog;
    DialogHandler dialogHandler;

    TextView surveyorCodeTxt;
    TextView surveyorNameTxt;

    HomeActivityPresenter homeActivityPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);

        progressDialog = new ProgressDialog();
        progressDialog.setCancelable(false);
        progressDialog.setTextView("Loading. Please wait.");

        dialogHandler = new DialogHandler(progressDialog, getSupportFragmentManager());

        findViewById(R.id.upload_btn).setOnClickListener(this);
        findViewById(R.id.download_survey_btn).setOnClickListener(this);
        findViewById(R.id.logout_btn).setOnClickListener(this);
        findViewById(R.id.start_survey_btn).setOnClickListener(this);

        surveyorCodeTxt = findViewById(R.id.code_txt);
        surveyorNameTxt = findViewById(R.id.name_txt);

        homeActivityPresenter = new HomeActivityPresenter(this);
        homeActivityPresenter.getSurveyorInfo();
    }

    @Override
    public PauseHandler getPauseHandler() {
        return dialogHandler;
    }

    @Override
    public void onLogoutSuccessful() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void openUploadActivity(String surveyorCode, String surveyorName) {
        Intent i = new Intent(this, UploadActivity.class);
        i.putExtra("surveyor_code", surveyorCode);
        i.putExtra("surveyor_name", surveyorName);
        startActivity(i);
    }

    @Override
    public void onSurveyDownloadedSuccessfully(String surveyorCode, Survey survey) {
        String message = "Survey " + survey.getName() + " (" + survey.getId() + ") downloaded successfully.";
        Utils.showMessageToast(message, this);
    }

    @Override
    public void openMainActivity(CurrentSurveyInfo surveyInfo) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("survey_list_data", surveyInfo);
        startActivity(intent);
    }

    @Override
    public void onError(String msg) {
        Utils.showMessageToast(msg, this);
    }

    @Override
    public void showLoading() {
        if (!progressDialog.isAdded())
            dialogHandler.showDialog("progress_dialog");
    }

    @Override
    public void hideLoading() {
        dialogHandler.hideDialog();
    }

    @Override
    public void onSurveyorInfoFetched(SurveyorInfoFromAPI surveyorInfoFromAPI) {
        surveyorNameTxt.setText(surveyorInfoFromAPI.getName());
        surveyorCodeTxt.setText(surveyorInfoFromAPI.getCode());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_survey_btn:
                homeActivityPresenter.startSurvey();
                break;
            case R.id.download_survey_btn:
                homeActivityPresenter.doDownload();
                break;
            case R.id.upload_btn:
                homeActivityPresenter.doUpload();
                break;
            case R.id.logout_btn:
                homeActivityPresenter.doLogout();
                break;
        }
    }
}
