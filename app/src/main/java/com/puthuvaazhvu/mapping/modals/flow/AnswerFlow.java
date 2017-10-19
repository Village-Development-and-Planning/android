
package com.puthuvaazhvu.mapping.modals.flow;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.BaseObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.io.Serializable;

public class AnswerFlow extends BaseObject implements Parcelable {
    private final Modes mode;

    @Override
    public JsonElement getAsJson() {
        return null;
    }

    public enum Modes {
        NONE, ONCE, OPTION, MULTIPLE
    }

    public AnswerFlow(Modes mode) {
        this.mode = mode;
    }

    public AnswerFlow(JsonObject jsonObject) {
        String mode = JsonHelper.getString(jsonObject, "scope");
        this.mode = parseMode(mode);
    }

    public static AnswerFlow.Modes parseMode(String mode) {
        if (mode == null) {
            return Modes.NONE;
        }

        switch (mode) {
            case "once":
                return Modes.ONCE;
            case "options":
                return Modes.OPTION;
            case "multiple":
                return Modes.MULTIPLE;
            default:
                return Modes.NONE;
        }
    }

    public Modes getMode() {
        return mode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mode == null ? -1 : this.mode.ordinal());
    }

    protected AnswerFlow(Parcel in) {
        int tmpMode = in.readInt();
        this.mode = tmpMode == -1 ? null : Modes.values()[tmpMode];
    }

    public static final Parcelable.Creator<AnswerFlow> CREATOR = new Creator<AnswerFlow>() {
        @Override
        public AnswerFlow createFromParcel(Parcel source) {
            return new AnswerFlow(source);
        }

        @Override
        public AnswerFlow[] newArray(int size) {
            return new AnswerFlow[size];
        }
    };
}
