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
import com.puthuvaazhvu.mapping.filestorage.DataInfoIO;
import com.puthuvaazhvu.mapping.filestorage.StorageUtils;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.other.Config;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.PauseHandler;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListActivity;

import java.io.File;

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
                        init();
                    }
                },
                null);

        retryDialog.setCancelable(false);
    }

    @Override
    public PauseHandler getPauseHandler() {
        return null;
    }

    @Override
    protected void onPermissionsGranted() {
        super.onPermissionsGranted();

        init();
    }

    private void openSurveyListActivity() {
        Intent intent = new Intent(this, SurveyListActivity.class);
        startActivity(intent);
    }

    private void init() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);
        AuthDataRepository authDataRepository = new AuthDataRepository(sharedPreferences, this);
        final DataInfoIO dataInfoIO = new DataInfoIO();

        Observable.zip(
                authDataRepository.get(Utils.isNetworkAvailable(this)),
                dataInfoIO.read()
                        .onErrorReturn(new Function<Throwable, DataInfo>() {
                            @Override
                            public DataInfo apply(Throwable throwable) throws Exception {
                                DataInfo dataInfo = new DataInfo();
                                File file = dataInfoIO.save(dataInfo).blockingFirst();
                                if (!file.exists())
                                    throw new Exception("Cannot create data info file");
                                return dataInfo;
                            }
                        }),
                new BiFunction<JsonObject, DataInfo, Object>() {
                    @Override
                    public Object apply(JsonObject jsonObject, DataInfo dataInfo) throws Exception {
                        // set the auth to the global context
                        MappingApplication.globalContext.getApplicationData().setAuthJson(jsonObject);

                        // check datainfo.json file version
                        if (dataInfo.getVersion() != Config.Versions.DATA_INFO_VERSION) {
                            File file = new File(StorageUtils.root() + "/" + Constants.DATA_DIR);
                            if (file.exists()) {
                                StorageUtils.deleteDir(file);
                                dataInfo = new DataInfo();
                                dataInfoIO.save(dataInfo).blockingFirst();
                            }
                        }
                        return new Object();
                    }
                }
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
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
}
