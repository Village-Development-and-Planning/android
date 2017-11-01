package com.puthuvaazhvu.mapping.views.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.puthuvaazhvu.mapping.BuildConfig;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.Utils;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/20/17.
 */

public class BaseActivity extends AppCompatActivity {
    protected boolean paused;
    protected boolean resumed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkForInitialPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
        paused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
        resumed = false;
    }

    private void checkForInitialPermissions() {
        if (Utils.isPermissionGranted(this, Constants.PermissionRequestCodes.STORAGE_PERMISSION_REQUEST_CODE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Timber.i("Permission granted for read / write external storage");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // call super to pass the request to fragment

        switch (requestCode) {
            case Constants.PermissionRequestCodes.STORAGE_PERMISSION_REQUEST_CODE:

                if (grantResults.length == 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Timber.i("Permission granted for read / write external storage");

                } else {
                    // permission denied
                    checkForInitialPermissions();
                }

                break;
        }
    }
}
