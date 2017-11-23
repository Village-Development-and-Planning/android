package com.puthuvaazhvu.mapping.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.Optional;
import com.puthuvaazhvu.mapping.utils.info_file.AnswersInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.SurveyInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswersInfoFileDataModal;
import com.puthuvaazhvu.mapping.utils.info_file.modals.SurveyInfoFileDataModal;
import com.puthuvaazhvu.mapping.utils.storage.DeleteFile;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListActivity;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by muthuveerappans on 11/24/17.
 */

public class SplashActivity extends AppCompatActivity {
    GetFromFile getFromFile;
    SaveToFile saveToFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ProgressBar progressBar = findViewById(R.id.progressBar2);
        final TextView infoTxt = findViewById(R.id.info_txt);

        infoTxt.setText("Initialising app components. Please wait...");

        checkInfoFiles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Optional>() {
                    @Override
                    public void accept(@NonNull Optional optional) throws Exception {
                        progressBar.setVisibility(View.INVISIBLE);
                        infoTxt.setText("DONE");
                        openSurveyListActivity();
                    }
                });
    }

    private void openSurveyListActivity() {
        Intent intent = new Intent(this, SurveyListActivity.class);
        startActivity(intent);
    }

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
                            DeleteFile.deleteFile(DataFileHelpers.getSurveyInfoFile(true));
                        }

                        version = answersInfoFileDataModal.getVersion();
                        if (version != Constants.Versions.ANSWERS_INFO_VERSION) {
                            DeleteFile.deleteFile(DataFileHelpers.getAnswersInfoFile(true));
                        }

                        return new Optional<>(null);
                    }
                });
    }
}
