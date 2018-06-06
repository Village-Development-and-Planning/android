package com.puthuvaazhvu.mapping.filestorage.modals;

import com.puthuvaazhvu.mapping.other.Config;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by muthuveerappans on 16/02/18.
 */

public class DataInfo implements Serializable {
    private int version;
    private HashMap<String, SurveyorInfo> surveyorInfoHashMap;

    public DataInfo() {
        this.version = Config.Versions.DATA_INFO_VERSION;
        this.surveyorInfoHashMap = new HashMap<>();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public SurveyorInfo getSurveyorInfo(String surveyorCode) {
        return surveyorInfoHashMap.get(surveyorCode);
    }

    public void addSurveyorInfoToMap(String surveyorCode, SurveyorInfo surveyorInfo) {
        surveyorInfoHashMap.put(surveyorCode, surveyorInfo);
    }
}