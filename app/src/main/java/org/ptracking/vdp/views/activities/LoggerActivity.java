package org.ptracking.vdp.views.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.ptracking.vdp.utils.GlobalExceptionHandler;

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