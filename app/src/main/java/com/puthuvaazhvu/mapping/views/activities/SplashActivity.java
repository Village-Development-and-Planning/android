package com.puthuvaazhvu.mapping.views.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.application.MappingApplication;
import com.puthuvaazhvu.mapping.data.AuthDataRepository;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.Optional;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.utils.info_file.AnswersInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.SurveyInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswersInfoFileDataModal;
import com.puthuvaazhvu.mapping.utils.info_file.modals.SurveyInfoFileDataModal;
import com.puthuvaazhvu.mapping.utils.storage.DeleteFile;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListActivity;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 11/24/17.
 */

public class SplashActivity extends BaseActivity {
    GetFromFile getFromFile;
    SaveToFile saveToFile;
    ProgressBar progressBar;
    TextView infoTxt;

    AlertDialog retryDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        getFromFile = GetFromFile.getInstance();
        saveToFile = SaveToFile.getInstance();

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
                checkInfoFiles().toObservable(),
                authDataRepository.getAuthData(Utils.isNetworkAvailable(this)),
                new BiFunction<Optional, JsonObject, Boolean>() {
                    @Override
                    public Boolean apply(Optional optional, JsonObject jsonObject) throws Exception {
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

    //Todo: refactor this.
    private Single<Optional> checkInfoFiles() {
        SurveyInfoFile surveyInfoFile = new SurveyInfoFile(getFromFile, saveToFile);
        AnswersInfoFile answersInfoFile = new AnswersInfoFile(getFromFile, saveToFile);

        return Single.zip(
                surveyInfoFile.getInfoJsonParsed(),
                answersInfoFile.getInfoJsonParsed(),
                new BiFunction<SurveyInfoFileDataModal, AnswersInfoFileDataModal, Optional>() {
                    @Override
                    public Optional apply(@NonNull SurveyInfoFileDataModal surveyInfoFileDataModal
                            , @NonNull AnswersInfoFileDataModal answersInfoFileDataModal) throws Exception {

                        int version = surveyInfoFileDataModal.getVersion();
                        if (version != Constants.Versions.SURVEY_INFO_VERSION) {
                            boolean result = DeleteFile.deleteFile(DataFileHelpers.getSurveyInfoFile(true));
                            Timber.i("status of delete survey info file " + result);
                        }

                        version = answersInfoFileDataModal.getVersion();
                        if (version != Constants.Versions.ANSWERS_INFO_VERSION) {
                            boolean result = DeleteFile.deleteFile(DataFileHelpers.getAnswersInfoFile(true));
                            Timber.i("status of delete answers info file " + result);
                        }

                        return new Optional<>(null);
                    }
                });
    }
}
