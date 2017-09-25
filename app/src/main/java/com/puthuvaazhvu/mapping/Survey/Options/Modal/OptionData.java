package com.puthuvaazhvu.mapping.Survey.Options.Modal;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class OptionData implements Parcelable, Serializable {
    final String position;
    boolean isChecked;
    final String text;
    final String id;
    boolean isOptionDone;

    public OptionData(String position, boolean isChecked, String text, String id, boolean isOptionDone) {
        this.position = position;
        this.isChecked = isChecked;
        this.text = text;
        this.id = id;
        this.isOptionDone = isOptionDone;
    }

    protected OptionData(Parcel in) {
        position = in.readString();
        isChecked = in.readByte() != 0;
        text = in.readString();
        id = in.readString();
        isOptionDone = in.readByte() != 0;
    }

    public String getPosition() {
        return position;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isOptionDone() {
        return isOptionDone;
    }

    public void setOptionDone(boolean optionDone) {
        isOptionDone = optionDone;
    }

    public static final Creator<OptionData> CREATOR = new Creator<OptionData>() {
        @Override
        public OptionData createFromParcel(Parcel in) {
            return new OptionData(in);
        }

        @Override
        public OptionData[] newArray(int size) {
            return new OptionData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(position);
        parcel.writeByte((byte) (isChecked ? 1 : 0));
        parcel.writeString(text);
        parcel.writeString(id);
        parcel.writeByte((byte) (isOptionDone ? 1 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionData that = (OptionData) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
