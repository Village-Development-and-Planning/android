package com.puthuvaazhvu.mapping.views.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.application.MappingApplication;
import com.puthuvaazhvu.mapping.data.AuthDataRepository;
import com.puthuvaazhvu.mapping.utils.DialogHandler;
import com.puthuvaazhvu.mapping.utils.PauseHandler;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by muthuveerappans on 27/03/18.
 */

@SuppressLint("Registered")
public class AuthActivity extends BaseActivity {
    DialogHandler dialogHandler;
    ProgressDialog progressDialog;
    protected String surveyCode;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog();
        progressDialog.setCancelable(false);

        dialogHandler = new DialogHandler(progressDialog, getSupportFragmentManager());

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            surveyCode = bundle.getString("surveyor_code", "");
            if (surveyCode.isEmpty()) {
                throw new IllegalArgumentException("Survey code cannot be empty.");
            }
        } else {
            // get the survey code

            View dialogView = LayoutInflater.from(this).inflate(R.layout.enter_surveyor_code_dialog, null);
            final EditText surveyorCodeEdt = dialogView.findViewById(R.id.surveyor_code_edt);

            alertDialog = Utils.createAlertDialog(
                    this,
                    getString(R.string.enter_surveyor_code),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            surveyCode = surveyorCodeEdt.getText().toString();
                            getAuth(surveyCode);
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

    private void getAuth(String code) {
        showLoading(R.string.loading);

        AuthDataRepository authDataRepository = new AuthDataRepository(this, code, "none");
        authDataRepository.get(false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                    @Override
                    public void accept(JsonObject jsonObject) throws Exception {
                        hideLoading();

                        if (alertDialog != null) {
                            alertDialog.dismiss();
                        }

                        MappingApplication.globalContext.getApplicationData().setAuthJson(jsonObject);
                        onAuthenticated();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        hideLoading();
                        Utils.showMessageToast("Authentication failed", AuthActivity.this);

                        if (alertDialog != null)
                            alertDialog.show();
                    }
                });
    }

    public void onAuthenticated() {

    }
}
