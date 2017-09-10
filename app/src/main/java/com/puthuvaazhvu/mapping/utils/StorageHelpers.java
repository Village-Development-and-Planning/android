package com.puthuvaazhvu.mapping.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.puthuvaazhvu.mapping.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 9/10/17.
 */

public class StorageHelpers {

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

    public static String getSurveyResponsesFileName(String surveyID, String surveyorID, String surveyeeID) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(surveyID);
        if (surveyorID != null && !surveyorID.isEmpty()) {
            stringBuilder.append(",");
            stringBuilder.append(surveyorID);
        }
        if (surveyeeID != null && !surveyeeID.isEmpty()) {
            stringBuilder.append(",");
            stringBuilder.append(surveyeeID);
        }
        stringBuilder.append(".txt");
        return stringBuilder.toString();
    }
}
