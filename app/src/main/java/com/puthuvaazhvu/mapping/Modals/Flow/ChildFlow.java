package com.puthuvaazhvu.mapping.Modals.Flow;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

public class ChildFlow implements Parcelable {
    private final Modes mode;
    private final UI uiToBeShown;
    private final RepeatMode repeatMode;

    public enum Modes {
        NONE, CASCADE, SELECT
    }

    public enum RepeatMode {
        NONE, ONCE, MULTIPLE
    }

    public enum UI {
        NONE, GRID;
    }

    public ChildFlow(Modes mode, UI uiToBeShown, RepeatMode repeatMode) {
        this.mode = mode;
        this.uiToBeShown = uiToBeShown;
        this.repeatMode = repeatMode;
    }

    public ChildFlow(JsonObject jsonObject) {
        String mode = JsonHelper.getString(jsonObject, "strategy");

        JsonObject selectJson = JsonHelper.getJsonObject(jsonObject, "select");
        String ui = null;
        String repeat = null;
        if (selectJson != null) {
            ui = JsonHelper.getString(selectJson, "ui");
            repeat = JsonHelper.getString(selectJson, "repeat");
        }

        this.mode = parseMode(mode);
        this.uiToBeShown = parseUI(ui);
        this.repeatMode = parseRepeatMode(repeat);
    }


    public Modes getMode() {
        return mode;
    }

    public UI getUiToBeShown() {
        return uiToBeShown;
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public static ChildFlow.Modes parseMode(String mode) {
        if (mode == null) {
            return Modes.NONE;
        }

        switch (mode) {
            case "cascade":
                return Modes.CASCADE;
            case "select":
                return Modes.SELECT;
            default:
                return Modes.NONE;
        }
    }

    public static ChildFlow.UI parseUI(String ui) {
        if (ui == null) {
            return UI.NONE;
        }

        switch (ui) {
            case "grid":
                return UI.GRID;
            default:
                return UI.NONE;
        }
    }

    public static ChildFlow.RepeatMode parseRepeatMode(String rmode) {
        if (rmode == null) {
            return RepeatMode.NONE;
        }

        switch (rmode) {
            case "once":
                return RepeatMode.ONCE;
            case "multiple":
                return RepeatMode.MULTIPLE;
            default:
                return RepeatMode.NONE;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mode == null ? -1 : this.mode.ordinal());
        dest.writeInt(this.uiToBeShown == null ? -1 : this.uiToBeShown.ordinal());
        dest.writeInt(this.repeatMode == null ? -1 : this.repeatMode.ordinal());
    }

    protected ChildFlow(Parcel in) {
        int tmpMode = in.readInt();
        this.mode = tmpMode == -1 ? null : Modes.values()[tmpMode];
        int tmpUiToBeShown = in.readInt();
        this.uiToBeShown = tmpUiToBeShown == -1 ? null : UI.values()[tmpUiToBeShown];
        int tmpRepeatMode = in.readInt();
        this.repeatMode = tmpRepeatMode == -1 ? null : RepeatMode.values()[tmpRepeatMode];
    }

    public static final Creator<ChildFlow> CREATOR = new Creator<ChildFlow>() {
        @Override
        public ChildFlow createFromParcel(Parcel source) {
            return new ChildFlow(source);
        }

        @Override
        public ChildFlow[] newArray(int size) {
            return new ChildFlow[size];
        }
    };
}
