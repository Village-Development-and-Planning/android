package com.puthuvaazhvu.mapping.modals.flow;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.BaseObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.io.Serializable;

public class ExitFlow extends BaseObject implements Parcelable {
    private final Modes mode;

    @Override
    public JsonElement getAsJson() {
        return null;
    }

    public enum Modes {
        NONE, PARENT, LOOP, END
    }

    public ExitFlow(Modes mode) {
        this.mode = mode;
    }

    public ExitFlow(JsonObject jsonObject) {
        String mode = JsonHelper.getString(jsonObject, "strategy");
        this.mode = parseModes(mode);
    }

    public Modes getMode() {
        return mode;
    }

    public static Modes parseModes(String mode) {
        if (mode == null) {
            return Modes.NONE;
        }

        switch (mode) {
            case "parent":
                return Modes.PARENT;
            case "LOOP":
                return Modes.LOOP;
            case "END":
                return Modes.END;
            default:
                return Modes.NONE;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mode == null ? -1 : this.mode.ordinal());
    }

    protected ExitFlow(Parcel in) {
        int tmpMode = in.readInt();
        this.mode = tmpMode == -1 ? null : Modes.values()[tmpMode];
    }

    public static final Creator<ExitFlow> CREATOR = new Creator<ExitFlow>() {
        @Override
        public ExitFlow createFromParcel(Parcel source) {
            return new ExitFlow(source);
        }

        @Override
        public ExitFlow[] newArray(int size) {
            return new ExitFlow[size];
        }
    };
}