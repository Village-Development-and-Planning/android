package com.puthuvaazhvu.mapping.utils;

import android.content.Context;
import android.widget.Toast;

import com.puthuvaazhvu.mapping.Constants;

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
}
