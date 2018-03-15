package com.puthuvaazhvu.mapping.views.activities.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.PauseHandler;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.activities.MenuActivity;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListActivity;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 11/1/17.
 */

public class SettingsActivity extends MenuActivity implements View.OnClickListener {
    Button main_page_button;
    ProgressDialog progressDialog;

    TextView go_to_main_page_title;
    TextView app_primary_language_title;
    RadioGroup language_radio_group;
    RadioButton tamil_rb;
    RadioButton english_rb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView versionTxt = findViewById(R.id.version);
        versionTxt.setText("Version " + getVersionName());

        progressDialog = new ProgressDialog();

        main_page_button = findViewById(R.id.main_page_button);
        main_page_button.setOnClickListener(this);
        go_to_main_page_title = findViewById(R.id.go_to_main_page_title);
        app_primary_language_title = findViewById(R.id.app_primary_language_title);
        language_radio_group = findViewById(R.id.language_radio_group);
        english_rb = findViewById(R.id.english_rb);
        tamil_rb = findViewById(R.id.tamil_rb);

        english_rb.setSelected(false);
        tamil_rb.setSelected(false);

        if (Constants.APP_LANGUAGE == Constants.Language.TAMIL) {
            tamil_rb.setChecked(true);
        } else {
            english_rb.setChecked(true);
        }

        language_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.tamil_rb) {
                    Constants.APP_LANGUAGE = Constants.Language.TAMIL;
                } else {
                    Constants.APP_LANGUAGE = Constants.Language.ENGLISH;
                }

                Utils.showMessageToast("Done", SettingsActivity.this);
            }
        });

        setLanguageForViews();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_page_button:
                Utils.createAlertDialog(
                        v.getContext(),
                        getString(R.string.do_you_want_to_go_to_main_page),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showListOfSurveyActivity();
                            }
                        },
                        null).show();
                break;
        }
    }

    private void setLanguageForViews() {
    }

    private void showListOfSurveyActivity() {
        Intent intent = new Intent(this, SurveyListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private String getVersionName() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = null;
            info = manager.getPackageInfo(
                    getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }
        return "N/A";
    }
}
