package com.puthuvaazhvu.mapping.views.activities.survey_list;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyListData {
    private final String id;
    private boolean isChecked;

    public SurveyListData(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public static SurveyListData adapter(String id) {
        return new SurveyListData(id);
    }

    public static List<SurveyListData> adapter(List<String> ids) {
        ArrayList<SurveyListData> surveyListData = new ArrayList<>();
        for (String id : ids) {
            surveyListData.add(SurveyListData.adapter(id));
        }
        return surveyListData;
    }
}
