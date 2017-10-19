package com.puthuvaazhvu.mapping.modals.flow;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.BaseObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.io.Serializable;

public class QuestionFlow extends BaseObject implements Parcelable {
    private final Validation validation;
    private final UI uiMode;

    @Override
    public JsonElement getAsJson() {
        return null;
    }

    public enum Validation {
        NONE, NUMBER, SURVEYOR_CODE
    }

    public enum UI {
        NONE, SINGLE_CHOICE, MULTIPLE_CHOICE, GPS, INPUT, INFO, CONFIRMATION
    }

    public QuestionFlow(Validation validation, UI uiMode) {
        this.validation = validation;
        this.uiMode = uiMode;
    }

    public QuestionFlow(JsonObject jsonObject) {
        String validation = JsonHelper.getString(jsonObject, "validation");
        String ui = JsonHelper.getString(jsonObject, "ui");

        this.validation = parseValidation(validation);
        this.uiMode = parseUI(ui);
    }

    public static UI parseUI(String ui) {
        if (ui == null) {
            return UI.NONE;
        }

        switch (ui) {
            case "SINGLE_CHOICE":
                return UI.SINGLE_CHOICE;
            case "MULTIPLE_CHOICE":
                return UI.MULTIPLE_CHOICE;
            case "GPS":
                return UI.GPS;
            case "INPUT":
                return UI.INPUT;
            case "INFO":
                return UI.INFO;
            case "CONFIRMATION":
                return UI.CONFIRMATION;
            default:
                return UI.NONE;
        }
    }

    public static Validation parseValidation(String validation) {
        if (validation == null) {
            return Validation.NONE;
        }

        switch (validation) {
            case "NUMBER":
                return Validation.NUMBER;
            case "SURVEYOR_CODE":
                return Validation.SURVEYOR_CODE;
            default:
                return Validation.NONE;
        }
    }

    public Validation getValidation() {
        return validation;
    }

    public UI getUiMode() {
        return uiMode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.validation == null ? -1 : this.validation.ordinal());
        dest.writeInt(this.uiMode == null ? -1 : this.uiMode.ordinal());
    }

    protected QuestionFlow(Parcel in) {
        int tmpValidation = in.readInt();
        this.validation = tmpValidation == -1 ? null : Validation.values()[tmpValidation];
        int tmpUiMode = in.readInt();
        this.uiMode = tmpUiMode == -1 ? null : UI.values()[tmpUiMode];
    }

    public static final Creator<QuestionFlow> CREATOR = new Creator<QuestionFlow>() {
        @Override
        public QuestionFlow createFromParcel(Parcel source) {
            return new QuestionFlow(source);
        }

        @Override
        public QuestionFlow[] newArray(int size) {
            return new QuestionFlow[size];
        }
    };
}
