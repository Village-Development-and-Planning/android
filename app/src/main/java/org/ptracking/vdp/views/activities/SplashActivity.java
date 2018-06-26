package org.ptracking.vdp.views.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ptracking.vdp.R;
import org.ptracking.vdp.filestorage.io.DataInfoIO;
import org.ptracking.vdp.filestorage.io.SnapshotIO;
import org.ptracking.vdp.filestorage.io.SurveyIO;
import org.ptracking.vdp.filestorage.modals.DataInfo;
import org.ptracking.vdp.modals.surveyorinfo.SurveyorInfoFromAPI;
import org.ptracking.vdp.other.Config;
import org.ptracking.vdp.other.Constants;
import org.ptracking.vdp.repository.AuthRepository;
import org.ptracking.vdp.utils.PauseHandler;
import org.ptracking.vdp.utils.SharedPreferenceUtils;
import org.ptracking.vdp.utils.ThrowableWithErrorCode;
import org.ptracking.vdp.utils.Utils;
import org.ptracking.vdp.views.activities.home.HomeActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 11/24/17.
 */

public class SplashActivity extends BaseActivity {
    DataInfoIO dataInfoIO;
    SnapshotIO snapshotIO;
    SurveyIO surveyIO;

    ProgressBar progressBar;
    TextView infoTxt;

    AlertDialog authDialog;
    EditText surveyorCodeEdt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        dataInfoIO = new DataInfoIO();
        snapshotIO = new SnapshotIO();
        surveyIO = new SurveyIO();

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
                        return migration();
                    }
                })
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

    private Observable<Boolean> migration() {
        switch (getVersionCode()) {
            case 1:
            case 2:
            case 3:
            case 4:
                return Observable.zip(dataInfoIO.delete(), snapshotIO.deleteAll(), surveyIO.deleteAll(), new Function3<Boolean, Boolean, Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean, Boolean aBoolean2, Boolean aBoolean3) throws Exception {
                        return true;
                    }
                });

            default:
                return Observable.just(true);


        }
    }

    private int getVersionCode() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = null;
            info = manager.getPackageInfo(
                    getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }
        return -1;
    }

}
