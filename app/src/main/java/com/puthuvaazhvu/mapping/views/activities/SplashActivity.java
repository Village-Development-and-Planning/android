package com.puthuvaazhvu.mapping.views.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.application.MappingApplication;
import com.puthuvaazhvu.mapping.data.AuthDataRepository;
import com.puthuvaazhvu.mapping.other.Config;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.utils.saving.AnswerIOUtils;
import com.puthuvaazhvu.mapping.utils.saving.SurveyIOUtils;
import com.puthuvaazhvu.mapping.utils.saving.modals.SnapshotsInfo;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 11/24/17.
 */

public class SplashActivity extends BaseActivity {
    ProgressBar progressBar;
    TextView infoTxt;

    AlertDialog retryDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        setContentView(R.layout.splash_screen);

        progressBar = findViewById(R.id.progressBar2);
        infoTxt = findViewById(R.id.info_txt);

        infoTxt.setText("Initialising app components. Please wait...");

        super.onCreate(savedInstanceState);

        retryDialog = Utils.createAlertDialog(this,
                getString(R.string.error_init),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        initThings();
                    }
                },
                null);

        retryDialog.setCancelable(false);
    }

    @Override
    protected void onPermissionsGranted() {
        super.onPermissionsGranted();

        initThings();
    }

    private void openSurveyListActivity() {
        Intent intent = new Intent(this, SurveyListActivity.class);
        startActivity(intent);
    }

    private void initThings() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);
        AuthDataRepository authDataRepository = AuthDataRepository.getInstance(sharedPreferences);

        Observable.zip(
                checkInfoFiles(),
                authDataRepository.getAuthData(Utils.isNetworkAvailable(this)),
                new BiFunction<Boolean, JsonObject, Boolean>() {
                    @Override
                    public Boolean apply(Boolean optional, JsonObject jsonObject) throws Exception {
                        MappingApplication.globalContext.getApplicationData().setAuthJson(jsonObject);
                        return true;
                    }
                }
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean o) throws Exception {
                        progressBar.setVisibility(View.INVISIBLE);
                        infoTxt.setText("DONE");
                        openSurveyListActivity();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (!retryDialog.isShowing()) {
                            retryDialog.show();
                        }
                    }
                });
    }

    private Observable<Boolean> checkInfoFiles() {
        final SurveyIOUtils surveyIOUtils = SurveyIOUtils.getInstance();
        final AnswerIOUtils answerIOUtils = AnswerIOUtils.getInstance();

        return Observable.zip(
                surveyIOUtils.readSurveysInfoFile()
                        .onErrorReturn(new Function<Throwable, SurveyInfo>() {
                            @Override
                            public SurveyInfo apply(Throwable throwable) throws Exception {
                                return new SurveyInfo();
                            }
                        }),
                answerIOUtils.readAnswerInfoFile()
                        .onErrorReturn(new Function<Throwable, SnapshotsInfo>() {
                            @Override
                            public SnapshotsInfo apply(Throwable throwable) throws Exception {
                                return new SnapshotsInfo();
                            }
                        }),
                new BiFunction<SurveyInfo, SnapshotsInfo, Boolean>() {
                    @Override
                    public Boolean apply(SurveyInfo surveyInfo, SnapshotsInfo snapshotsInfo) throws Exception {
                        if (surveyInfo.getVersion() != Config.Versions.SURVEY_INFO_VERSION) {
                            boolean result = surveyIOUtils.deleteInfoFile();
                            Timber.i("Deleted status of surveys info file " + result);
                        }
                        if (snapshotsInfo.getVersion() != Config.Versions.ANSWERS_INFO_VERSION) {
                            boolean result = answerIOUtils.deleteInfoFile();
                            Timber.i("Deleted status of answers info file " + result);
                        }
                        return true;
                    }
                }
        );
    }
}
