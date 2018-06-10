package com.puthuvaazhvu.mapping.views.activities.upload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.filestorage.io.DataInfoIO;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveyorData;
import com.puthuvaazhvu.mapping.modals.surveyorinfo.SurveyorInfoFromAPI;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.upload.AnswersUploadTask;
import com.puthuvaazhvu.mapping.upload.FileUploadResultReceiver;
import com.puthuvaazhvu.mapping.utils.PauseHandler;
import com.puthuvaazhvu.mapping.utils.SharedPreferenceUtils;
import com.puthuvaazhvu.mapping.views.activities.MenuActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by muthuveerappans on 09/05/18.
 */

public class UploadActivity extends MenuActivity implements FileUploadResultReceiver {
    TextView surveyorCodeTxt, surveyorNameTxt, uploadingProgressTxt, uploadedCountTxt, answersCountTitle;
    Button uploadBtn;
    Button doneBtn;

    AnswersUploadTask answersUploadTask;
    String uploadProgress = "";

    DataInfoIO dataInfoIO;

    String surveyorCode;
    String surveyorName;

    SurveyorInfoFromAPI surveyorInfoFromAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        surveyorInfoFromAPI = SharedPreferenceUtils.getInstance(this).getSurveyorInfo();

        dataInfoIO = new DataInfoIO();

        surveyorCode = getIntent().getExtras().getString("surveyor_code");
        surveyorName = getIntent().getExtras().getString("surveyor_name");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setContentView(R.layout.upload_activity);

        surveyorNameTxt = findViewById(R.id.surveyor_name);
        surveyorCodeTxt = findViewById(R.id.surveyor_code);

        uploadingProgressTxt = findViewById(R.id.uploading_status);

        View countHolder = findViewById(R.id.count_holder);
        TextView pendingTitle = countHolder.findViewById(R.id.info_label);
        pendingTitle.setText(R.string.answers_count_title);
        answersCountTitle = countHolder.findViewById(R.id.value);
        answersCountTitle.setText("" + 0);

        View uploaded = findViewById(R.id.done_count_holder);
        TextView uploadedTitle = uploaded.findViewById(R.id.info_label);
        uploadedTitle.setText(R.string.uploaded_answers_title);
        uploadedCountTxt = uploaded.findViewById(R.id.value);
        uploadedCountTxt.setText("" + 0);

        findViewById(R.id.cancel).setVisibility(View.GONE);

        doneBtn = findViewById(R.id.done);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        doneBtn.setVisibility(View.GONE);

        uploadBtn = findViewById(R.id.upload);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleUIForLoading(true);
                uploadProgress = "";
                answersUploadTask.execute();
            }
        });

        answersUploadTask = new AnswersUploadTask(this, surveyorCode, Constants.PASSWORD, this);

        toggleUIForLoading(false);

        updateTitles(surveyorName, surveyorCode);

        updateAnswersCount();
    }

    @Override
    public PauseHandler getPauseHandler() {
        return null;
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
    public void onProgress(String message) {
        updateStatusText(message);
    }

    @Override
    public void onError(String message) {
        updateStatusText(message);
        toggleUIForLoading(false);
    }

    @Override
    public void onUploadCompleted(int successfulCount, int failedCount) {
        updateInfo(successfulCount, failedCount);
        updateStatusText("Upload complete!");
        toggleUIForLoading(false);
        uiForDone();
    }

    private void updateInfo(int successCount, int failureCount) {
        uploadedCountTxt.setText("" + successCount);
    }

    private void updateTitles(String name, String code) {
        if (surveyorCodeTxt == null || surveyorNameTxt == null) return;

        surveyorNameTxt.setText(name);
        surveyorCodeTxt.setText(code);
    }

    private void updateStatusText(String status) {
        uploadProgress += status;
        uploadProgress += "\n";

        uploadingProgressTxt.setText(uploadProgress);
    }

    private void toggleUIForLoading(boolean isProcessing) {
        uploadBtn.setEnabled(!isProcessing);
        doneBtn.setVisibility(View.GONE);
    }

    private void uiForDone() {
        doneBtn.setVisibility(View.VISIBLE);
    }

    private void updateAnswersCount() {
        dataInfoIO.read()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataInfo>() {
                    @Override
                    public void accept(DataInfo dataInfo) throws Exception {
                        SurveyorData surveyorData = dataInfo.getSurveyorData(surveyorCode);

                        if (surveyorData != null) {
                            answersCountTitle.setText("" + surveyorData.getAnswersInfo()
                                    .getAnswersCount(surveyorInfoFromAPI.getSurveyId()));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        answersCountTitle.setText("" + 0);
                    }
                });
    }
}
