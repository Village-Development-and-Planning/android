package com.puthuvaazhvu.mapping.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.Toast;

import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 9/14/17.
 */

public class Utils {
    public static String generateRandomUUID() {
        return String.valueOf(UUID.randomUUID());
    }

    public static String getErrorMessage(int id, Context context) {
        return context.getResources().getString(id);
    }

    public static void showMessageToast(int msgID, Context context) {
        showMessageToast(context.getString(msgID), context);
    }

    public static void showMessageToast(String msg, Context context) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static String readFromAssetsFile(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            json = readFromInputStream(is);
        } catch (IOException ex) {
            Timber.e("Error reading the JSON file from assets. " + ex.getMessage());
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
            Timber.e("Error reading the file. " + ex.getMessage());
        }
        return json;
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

    public static boolean isPermissionGranted(Activity context, int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> nonPermissions = hasPermissions(context, permissions);
            if (nonPermissions.size() <= 0) {
                Timber.i("Permissions are granted " + permissions);
                return true;
            } else {
                ActivityCompat.requestPermissions(context
                        , permissions, requestCode);
                return false;
            }
        } else {
            // permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public static boolean equalLists(List<String> one, List<String> two) {
        if (one == null && two == null) {
            return true;
        }

        if ((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()) {
            return false;
        }

        //to avoid messing the order of the lists we will use a copy
        //as noted in comments by A. R. S.
        one = new ArrayList<String>(one);
        two = new ArrayList<String>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
