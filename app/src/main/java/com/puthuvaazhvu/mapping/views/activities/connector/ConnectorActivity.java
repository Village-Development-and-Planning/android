package com.puthuvaazhvu.mapping.views.activities.connector;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.auth.AuthUtils;
import com.puthuvaazhvu.mapping.data.AuthRepository;
import com.puthuvaazhvu.mapping.utils.DialogHandler;
import com.puthuvaazhvu.mapping.utils.PauseHandler;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.activities.MenuActivity;
import com.puthuvaazhvu.mapping.views.activities.connector.upload.UploadActivity;
import com.puthuvaazhvu.mapping.views.activities.dump_survey_activity.SurveyDataDumpActivity;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by muthuveerappans on 27/03/18.
 */

public class ConnectorActivity extends MenuActivity implements View.OnClickListener {
    Button downloadSurveyBtn;
    Button uploadSurveyBtn;

    DialogHandler dialogHandler;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;

    String surveyorCode;
    String surveyorName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog();
        progressDialog.setCancelable(false);

        dialogHandler = new DialogHandler(progressDialog, getSupportFragmentManager());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setContentView(R.layout.survey_settings_page);

        downloadSurveyBtn = findViewById(R.id.download_survey);
        uploadSurveyBtn = findViewById(R.id.upload_surveys);

        downloadSurveyBtn.setOnClickListener(this);
        uploadSurveyBtn.setOnClickListener(this);

        disableUI();

        // get the survey code
        View dialogView = LayoutInflater.from(this).inflate(R.layout.enter_surveyor_code_dialog, null);
        final EditText surveyorCodeEdt = dialogView.findViewById(R.id.surveyor_code_edt);

        alertDialog = Utils.createAlertDialog(
                this,
                getString(R.string.enter_surveyor_code),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        surveyorCode = surveyorCodeEdt.getText().toString();
                        getAuth(surveyorCode);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alertDialog.setView(dialogView);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_surveys:
                openUploadSurveysActivity(surveyorCode, surveyorName);
                break;
            case R.id.download_survey:
                openDownloadSurveyActivity(surveyorCode);
                break;
        }
    }


    @Override
    public PauseHandler getPauseHandler() {
        return dialogHandler;
    }

    private void showLoading(int msg) {
        progressDialog.setTextView(getString(msg));
        dialogHandler.showDialog("progress_dialog");
    }

    private void hideLoading() {
        dialogHandler.hideDialog();
    }

    private void getAuth(final String code) {
        showLoading(R.string.loading);

        AuthRepository authRepository = new AuthRepository(this, code, "none");
        authRepository.get(false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                    @Override
                    public void accept(JsonObject jsonObject) throws Exception {
                        hideLoading();

                        surveyorName = AuthUtils.getSurveyorName(jsonObject, code);

                        onAuthenticated();

                        if (alertDialog != null) {
                            alertDialog.dismiss();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        hideLoading();
                        Utils.showMessageToast("Authentication failed", ConnectorActivity.this);

                        if (alertDialog != null)
                            alertDialog.show();
                    }
                });
    }

    private void onAuthenticated() {
        enableUI();
    }

    private void openUploadSurveysActivity(String surveyorCode, String name) {
        Intent i = new Intent(this, UploadActivity.class);
        i.putExtra("surveyor_code", surveyorCode);
        i.putExtra("surveyor_name", name);
        startActivity(i);
    }

    private void openDownloadSurveyActivity(String surveyorCode) {
        Intent i = new Intent(this, SurveyDataDumpActivity.class);
        i.putExtra("surveyor_code", surveyorCode);
        startActivity(i);
    }

    private void disableUI() {
        downloadSurveyBtn.setEnabled(false);
        uploadSurveyBtn.setEnabled(false);
    }

    private void enableUI() {
        if (downloadSurveyBtn == null || uploadSurveyBtn == null) return;

        downloadSurveyBtn.setEnabled(true);
        uploadSurveyBtn.setEnabled(true);
    }
}
