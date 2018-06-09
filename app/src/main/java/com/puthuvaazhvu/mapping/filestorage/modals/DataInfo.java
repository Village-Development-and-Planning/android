package com.puthuvaazhvu.mapping.filestorage.modals;

import com.puthuvaazhvu.mapping.other.Config;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by muthuveerappans on 16/02/18.
 */

public class DataInfo implements Serializable {
    private int version;
    private HashMap<String, SurveyorData> surveyorDataHashMap;

    public DataInfo() {
        this.version = Config.Versions.DATA_INFO_VERSION;
        this.surveyorDataHashMap = new HashMap<>();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public SurveyorData getSurveyorData(String surveyorCode) {
        return surveyorDataHashMap.get(surveyorCode);
    }

    public void addSurveyorInfoToMap(String surveyorCode, SurveyorData surveyorData) {
        surveyorDataHashMap.put(surveyorCode, surveyorData);
    }
}