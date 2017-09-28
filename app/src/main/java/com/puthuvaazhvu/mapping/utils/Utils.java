package com.puthuvaazhvu.mapping.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.puthuvaazhvu.mapping.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by muthuveerappans on 9/14/17.
 */

public class Utils {
    public static String generateRandomUUID() {
        return String.valueOf(UUID.randomUUID());
    }

    public static void showErrorMessage(int msgID, Context context) {
        Toast.makeText(context, context.getString(msgID), Toast.LENGTH_SHORT).show();
    }

    public static String readFromAssetsFile(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            json = readFromInputStream(is);
        } catch (IOException ex) {
            Log.i(Constants.LOG_TAG, "Error reading the JSON file from assets. " + ex.getMessage());
        }
        return json;
    }

    public static String readFromInputStream(InputStream is) {
        String json = null;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.i(Constants.LOG_TAG, "Error reading the JSON file from assets. " + ex.getMessage());
        }
        return json;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static List<String> hasPermissions(Context context, String... permissions) {
        List<String> result = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    result.add(permission);
                }
            }
        }
        return result;
    }

    public static boolean isPermissionGranted(Activity context, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> nonPermissions = hasPermissions(context, permissions);
            if (nonPermissions.size() <= 0) {
                Log.i(Constants.LOG_TAG, "Permissions are granted " + permissions);
                return true;
            } else {
                ActivityCompat.requestPermissions(context
                        , permissions, 1);
                return false;
            }
        } else {
            // permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}
