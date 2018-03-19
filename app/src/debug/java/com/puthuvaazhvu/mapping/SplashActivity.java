package com.puthuvaazhvu.mapping;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.puthuvaazhvu.mapping.utils.Utils;

/**
 * Created by muthuveerappans on 19/03/18.
 */

public class SplashActivity extends AppCompatActivity {
    public static int OVERLAY_PERMISSION_CODE = 2525;
    Intent overlayService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen_debug);

        // start the overlay service upfront
        addOverlay();

        findViewById(R.id.go_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this
                        , com.puthuvaazhvu.mapping.views.activities.SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void addOverlay() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
                return;
            }
        }

        startOverlayService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Utils.showMessageToast("Overlay permission not granted!", this);
                } else {
                    startOverlayService();
                }
            }
        }
    }

    private void startOverlayService() {
        overlayService = new Intent(this, DebugInformationOverlayService.class);
        // Try to stop the service if it is already running
        // Otherwise start the service
        if (!stopService(overlayService)) {
            startService(overlayService);
        }
    }
}
