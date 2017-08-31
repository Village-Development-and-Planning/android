package com.puthuvaazhvu.mapping.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.Survey.SurveyActivity;
import com.puthuvaazhvu.mapping.Surveyour.DetailsActivity;

/**
 * Created by muthuveerappans on 8/31/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        findViewById(R.id.button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            Intent i = new Intent(LoginActivity.this, DetailsActivity.class);
            startActivity(i);
        }
    }
}
