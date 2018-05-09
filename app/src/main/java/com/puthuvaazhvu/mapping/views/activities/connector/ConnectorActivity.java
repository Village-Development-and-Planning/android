package com.puthuvaazhvu.mapping.views.activities.connector;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.activities.AuthActivity;
import com.puthuvaazhvu.mapping.views.activities.connector.upload.UploadActivity;
import com.puthuvaazhvu.mapping.views.activities.dump_survey_activity.SurveyDataDumpActivity;

/**
 * Created by muthuveerappans on 27/03/18.
 */

public class ConnectorActivity extends AuthActivity implements View.OnClickListener {
    Button downloadSurveyBtn;
    Button uploadSurveyBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public void onAuthenticated() {
        enableUI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_surveys:
                openUploadSurveysActivity();
                break;
            case R.id.download_survey:
                openDownloadSurveyActivity();
                break;
        }
    }

    private void openUploadSurveysActivity() {
        Intent i = new Intent(this, UploadActivity.class);
        startActivity(i);
    }

    private void openDownloadSurveyActivity() {
        Intent i = new Intent(this, SurveyDataDumpActivity.class);
        i.putExtra("surveyor_code", surveyCode);
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
