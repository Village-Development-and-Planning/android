package com.puthuvaazhvu.mapping.utils.info_file.modals;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class InfoFile {
    private final List<SavedSurveyInfoFile> savedSurveyInfoFileList;

    public InfoFile(List<SavedSurveyInfoFile> savedSurveyInfoFileList) {
        this.savedSurveyInfoFileList = savedSurveyInfoFileList;
    }

    public InfoFile(JsonObject jsonObject) {
        JsonArray array = JsonHelper.getJsonArray(jsonObject, "saved_surveys");

        if (array != null) {
            savedSurveyInfoFileList = new ArrayList<>();
            for (JsonElement e : array) {
                savedSurveyInfoFileList.add(new SavedSurveyInfoFile(e.getAsJsonObject()));
            }
        } else {
            savedSurveyInfoFileList = null;
        }
    }

    public List<SavedSurveyInfoFile> getSavedSurveyInfoFileList() {
        return savedSurveyInfoFileList;
    }
}
