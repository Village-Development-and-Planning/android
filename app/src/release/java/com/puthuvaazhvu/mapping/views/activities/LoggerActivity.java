package com.puthuvaazhvu.mapping.views.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.puthuvaazhvu.mapping.other.Config;
import com.puthuvaazhvu.mapping.utils.GlobalExceptionHandler;

/**
 * Created by muthuveerappans on 22/02/18.
 */

@SuppressLint("Registered")
public class LoggerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalExceptionHandler.getInstance(this);
    }
}