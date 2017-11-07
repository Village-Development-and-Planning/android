package com.puthuvaazhvu.mapping.views.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListActivity;
import com.puthuvaazhvu.mapping.views.activities.testing.TogetherQuestionTestFragmentActivity;

/**
 * Created by muthuveerappans on 11/1/17.
 */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    Button show_list_of_surveys_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        show_list_of_surveys_btn = findViewById(R.id.show_list_of_surveys_btn);
        show_list_of_surveys_btn.setOnClickListener(this);

        findViewById(R.id.message_fragment_test).setOnClickListener(this);

        Switch isTamilSwitch = findViewById(R.id.language_switch);
        isTamilSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Constants.APP_LANGUAGE = Constants.Language.TAMIL;
                } else {
                    Constants.APP_LANGUAGE = Constants.Language.ENGLISH;
                }

                Utils.showMessageToast("Restarting", SettingsActivity.this);

                showListOfSurveyActivity();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_list_of_surveys_btn:
                Utils.showMessageToast("Restarting", this);
                showListOfSurveyActivity();
                break;
            case R.id.message_fragment_test:
                showMessageQuestionTestActivity();
                break;
        }
    }

    private void showListOfSurveyActivity() {
        Intent intent = new Intent(this, SurveyListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showMessageQuestionTestActivity() {
        Intent intent = new Intent(this, TogetherQuestionTestFragmentActivity.class);
        startActivity(intent);
    }
}
