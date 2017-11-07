package com.puthuvaazhvu.mapping.views.activities.survey_list;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyListData {
    private final String id;
    private final String name;
    private boolean isChecked;

    public SurveyListData(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public static SurveyListData adapter(String id, String name) {
        return new SurveyListData(id, name);
    }
}
