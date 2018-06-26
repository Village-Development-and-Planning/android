package org.ptracking.vdp.views.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ptracking.vdp.R;
import org.ptracking.vdp.other.Constants;
import org.ptracking.vdp.utils.PauseHandler;
import org.ptracking.vdp.utils.Utils;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/20/17.
 */

@SuppressLint("Registered")
public abstract class BaseActivity extends LoggerActivity {
    protected boolean paused;
    protected boolean resumed;

    protected PauseHandler pauseHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkForInitialPermissions();
    }

    @Override
    protected void onStart() {
        super.onStart();

        pauseHandler = getPauseHandler();
    }

    public abstract PauseHandler getPauseHandler();

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
        paused = false;

        if (pauseHandler != null)
            pauseHandler.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
        resumed = false;

        if (pauseHandler != null)
            pauseHandler.pause();
    }

    private void checkForInitialPermissions() {
        if (Utils.isPermissionGranted(this, Constants.PermissionRequestCodes.STORAGE_PERMISSION_REQUEST_CODE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            Timber.i("Permission granted for read / write external storage, access fine location");
            onPermissionsGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // call super to pass the request to fragment

        switch (requestCode) {
            case Constants.PermissionRequestCodes.STORAGE_PERMISSION_REQUEST_CODE:

                if (grantResults.length == 3
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    Timber.i("Permission granted for read / write external storage, access fine location");
                    onPermissionsGranted();

                } else {
                    // permission denied
                    checkForInitialPermissions();
                    Utils.showMessageToast(R.string.grant_permissions_request, this);
                }

                break;
        }
    }

    protected void onPermissionsGranted() {

    }
}
