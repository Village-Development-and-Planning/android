package com.puthuvaazhvu.mapping.activities;

import android.content.Context;

import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;

/**
 * Created by muthuveerappans on 10/16/17.
 */

public class ModalHelpers {

    public static Survey getSurvey(Context context) {
        String surveyString = Utils.readFromAssetsFile(context, "survey_data_test_UI.json");

        JsonParser jsonParser = new JsonParser();

        return new Survey(jsonParser.parse(surveyString).getAsJsonObject());
    }
}
