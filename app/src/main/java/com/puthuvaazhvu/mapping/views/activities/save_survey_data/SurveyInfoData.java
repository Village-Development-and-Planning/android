package com.puthuvaazhvu.mapping.views.activities.save_survey_data;

import com.puthuvaazhvu.mapping.modals.SurveyInfo;

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

    public static SurveyInfoData adapter(SurveyInfo surveyInfo) {
        return new SurveyInfoData(surveyInfo.get_id(), surveyInfo.getName());
    }

    public static List<SurveyInfoData> adapter(List<SurveyInfo> surveyInfoList) {
        ArrayList<SurveyInfoData> surveyInfoData = new ArrayList<>(surveyInfoList.size());
        for (SurveyInfo surveyInfo : surveyInfoList) {
            surveyInfoData.add(adapter(surveyInfo));
        }
        return surveyInfoData;
    }
}