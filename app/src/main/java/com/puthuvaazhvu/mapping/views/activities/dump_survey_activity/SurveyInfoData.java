package com.puthuvaazhvu.mapping.views.activities.dump_survey_activity;

import com.puthuvaazhvu.mapping.modals.SurveyAPIInfo;

import java.util.ArrayList;
import java.util.List;

public class SurveyInfoData {
    public final String id;
    public final String name;
    public boolean isSelected;

    public SurveyInfoData(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static SurveyInfoData adapter(SurveyAPIInfo surveyInfo) {
        return new SurveyInfoData(surveyInfo.get_id(), surveyInfo.getName());
    }

    public static List<SurveyInfoData> adapter(List<SurveyAPIInfo> surveyInfoList) {
        ArrayList<SurveyInfoData> surveyInfoData = new ArrayList<>(surveyInfoList.size());
        for (SurveyAPIInfo surveyInfo : surveyInfoList) {
            surveyInfoData.add(adapter(surveyInfo));
        }
        return surveyInfoData;
    }
}