package com.puthuvaazhvu.mapping.views.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.filestorage.io.DataInfoIO;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.modals.surveyorinfo.SurveyorInfoFromAPI;
import com.puthuvaazhvu.mapping.other.Config;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.repository.AuthRepository;
import com.puthuvaazhvu.mapping.utils.PauseHandler;
import com.puthuvaazhvu.mapping.utils.SharedPreferenceUtils;
import com.puthuvaazhvu.mapping.utils.ThrowableWithErrorCode;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.activities.home.HomeActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 11/24/17.
 */

public class SplashActivity extends BaseActivity {
    DataInfoIO dataInfoIO;

    ProgressBar progressBar;
    TextView infoTxt;

    AlertDialog authDialog;
    EditText surveyorCodeEdt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        dataInfoIO = new DataInfoIO();

        setContentView(R.layout.splash_screen);

        progressBar = findViewById(R.id.progressBar2);
        infoTxt = findViewById(R.id.info_txt);

        infoTxt.setText("Initialising app components. Please wait...");

        super.onCreate(savedInstanceState);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.enter_surveyor_code_dialog, null);
        final EditText surveyorCodeEdt = dialogView.findViewById(R.id.surveyor_code_edt);

        SharedPreferenceUtils sharedPreferenceUtils = SharedPreferenceUtils.getInstance(this);

        SurveyorInfoFromAPI surveyorInfoFromAPI = sharedPreferenceUtils.getSurveyorInfo();

        if (surveyorInfoFromAPI == null) {
            authDialog = Utils.createAlertDialog(
                    this,
                    getString(R.string.enter_surveyor_code),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String surveyorCode = surveyorCodeEdt.getText().toString();
                            initializeApp(surveyorCode);
                        }
                    },
                    null);
            authDialog.setView(dialogView);
            authDialog.setCancelable(false);
            authDialog.show();
        } else {
            initializeApp(surveyorInfoFromAPI.getCode());
        }
    }

    @Override
    public PauseHandler getPauseHandler() {
        return null;
    }

    @Override
    protected void onPermissionsGranted() {
        super.onPermissionsGranted();
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initializeApp(final String surveyorCode) {
        Observable.just(true)
                .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(Boolean aBoolean) throws Exception {
                        AuthRepository authRepository = new AuthRepository(SplashActivity.this,
                                surveyorCode, Constants.PASSWORD);
                        return authRepository.get(false)
                                .flatMap(new Function<SurveyorInfoFromAPI, ObservableSource<Boolean>>() {
                                    @Override
                                    public ObservableSource<Boolean> apply(SurveyorInfoFromAPI surveyorInfoFromAPI) throws Exception {
                                        return checkDataInfo();
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean b) throws Exception {
                        if (authDialog != null) {
                            authDialog.dismiss();
                        }

                        progressBar.setVisibility(View.INVISIBLE);
                        infoTxt.setText("DONE");
                        openMainActivity();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        Utils.showMessageToast("Authentication failed. Please try again."
                                , SplashActivity.this);

                        if (authDialog != null)
                            authDialog.show();
                    }
                });
    }

    private Observable<Boolean> checkDataInfo() {
        return dataInfoIO.read()
                .flatMap(new Function<DataInfo, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(DataInfo dataInfo) throws Exception {
                        int dataInfoVersion = dataInfo.getVersion();
                        if (dataInfoVersion != Config.Versions.DATA_INFO_VERSION) {
                            return dataInfoIO.delete();
                        }
                        return Observable.just(true);
                    }
                })
                .onErrorReturn(new Function<Throwable, Boolean>() {
                    @Override
                    public Boolean apply(Throwable throwable) throws Exception {
                        if (throwable instanceof ThrowableWithErrorCode) {
                            ThrowableWithErrorCode throwableWithErrorCode = (ThrowableWithErrorCode) throwable;

                            if (throwableWithErrorCode.getErrorCode() == Constants.ErrorCodes.FILE_NOT_EXIST ||
                                    throwableWithErrorCode.getErrorCode() == Constants.ErrorCodes.ERROR_READING_FILE) {
                                return true;
                            }
                        }

                        throw new Exception(throwable);
                    }
                });
    }

}
