package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Survey extends BaseObject {
    private String id;
    private String name;
    private String description;
    private Question question;
    private String modifiedAt;
    private boolean enabled;
    private String surveyorCode;

    public Survey() {
    }

    public Survey(String id, String name, String description, Question question, String modifiedAt, boolean enabled) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.question = question;
        this.modifiedAt = modifiedAt;
        this.enabled = enabled;
    }

    public String getSurveyorCode() {
        return surveyorCode;
    }

    public void setSurveyorCode(String surveyorCode) {
        this.surveyorCode = surveyorCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Question getQuestion() {
        return question;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public String getDescription() {
        return description;
    }
}
