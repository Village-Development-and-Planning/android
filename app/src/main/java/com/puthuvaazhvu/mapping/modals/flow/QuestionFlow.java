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
    private boolean back = true;
    private int optionsLimit;
    private boolean showImage;

    @Override
    public JsonElement getAsJson() {
        return null;
    }

    public enum Validation {
        NONE, NUMBER, TEXT
    }

    public enum UI {
        NONE, SINGLE_CHOICE, MULTIPLE_CHOICE, GPS, INPUT, INFO, CONFIRMATION, MESSAGE, DUMMY
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

        if (JsonHelper.isJsonValid(jsonObject, "back")) {
            back = JsonHelper.getBoolean(jsonObject, "back");
        }

        optionsLimit = JsonHelper.getInt(jsonObject, "optionsLimit");

        showImage = JsonHelper.getBoolean(jsonObject, "showImage");
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
            case "MESSAGE":
                return UI.MESSAGE;
            default:
                return UI.NONE;
        }
    }

    public static Validation parseValidation(String validation) {
        if (validation == null) {
            return Validation.NONE;
        }

        switch (validation) {
            case "[0-9]+":
                return Validation.NUMBER;
            case "TEXT":
                return Validation.TEXT;
            default:
                return Validation.NONE;
        }
    }

    public boolean isShowImage() {
        return showImage;
    }

    public int getOptionsLimit() {
        return optionsLimit;
    }

    public void setOptionsLimit(int optionsLimit) {
        this.optionsLimit = optionsLimit;
    }

    public Validation getValidation() {
        return validation;
    }

    public UI getUiMode() {
        return uiMode;
    }

    public boolean isBack() {
        return back;
    }

    public void setBack(boolean back) {
        this.back = back;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.validation == null ? -1 : this.validation.ordinal());
        dest.writeInt(this.uiMode == null ? -1 : this.uiMode.ordinal());
        dest.writeByte(this.back ? (byte) 1 : (byte) 0);
        dest.writeInt(this.optionsLimit);
        dest.writeByte(this.showImage ? (byte) 1 : (byte) 0);
    }

    protected QuestionFlow(Parcel in) {
        int tmpValidation = in.readInt();
        this.validation = tmpValidation == -1 ? null : Validation.values()[tmpValidation];
        int tmpUiMode = in.readInt();
        this.uiMode = tmpUiMode == -1 ? null : UI.values()[tmpUiMode];
        this.back = in.readByte() != 0;
        this.optionsLimit = in.readInt();
        this.showImage = in.readByte() != 0;
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
