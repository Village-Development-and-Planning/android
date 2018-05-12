package com.puthuvaazhvu.mapping.views.activities.connector.upload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.application.MappingApplication;
import com.puthuvaazhvu.mapping.auth.AuthUtils;
import com.puthuvaazhvu.mapping.upload.AnswersUploadTask;
import com.puthuvaazhvu.mapping.upload.FileUploadResultReceiver;
import com.puthuvaazhvu.mapping.utils.PauseHandler;
import com.puthuvaazhvu.mapping.views.activities.BaseActivity;
import com.puthuvaazhvu.mapping.views.activities.MenuActivity;

/**
 * Created by muthuveerappans on 09/05/18.
 */

public class UploadActivity extends MenuActivity implements FileUploadResultReceiver {
    TextView surveyorCodeTxt, surveyorNameTxt, uploadingProgressTxt, uploadedCountTxt, failureAnswersTitle;
    Button uploadBtn;
    Button doneBtn;

    AnswersUploadTask answersUploadTask;
    String uploadProgress = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String surveyorCode = getIntent().getExtras().getString("surveyor_code");
        String surveyorName = getIntent().getExtras().getString("surveyor_name");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setContentView(R.layout.upload_activity);

        surveyorNameTxt = findViewById(R.id.surveyor_name);
        surveyorCodeTxt = findViewById(R.id.surveyor_code);

        uploadingProgressTxt = findViewById(R.id.uploading_status);

        View failure = findViewById(R.id.failed_count_holder);
        TextView pendingTitle = failure.findViewById(R.id.info_label);
        pendingTitle.setText(R.string.failure_answers_title);
        failureAnswersTitle = failure.findViewById(R.id.value);
        failureAnswersTitle.setText("" + 0);

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

        answersUploadTask = new AnswersUploadTask(surveyorCode, "none", this);

        toggleUIForLoading(false);

        updateTitles(surveyorName, surveyorCode);
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
        failureAnswersTitle.setText("" + failureCount);
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
}
