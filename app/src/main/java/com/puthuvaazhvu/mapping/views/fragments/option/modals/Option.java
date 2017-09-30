package com.puthuvaazhvu.mapping.views.fragments.option.modals;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class Option implements Parcelable {
    private String id;
    private String text;
    private boolean isSelected;

    public Option(String id, String text, boolean isSelected) {
        this.id = id;
        this.text = text;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.text);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected Option(Parcel in) {
        this.id = in.readString();
        this.text = in.readString();
        this.isSelected = in.readByte() != 0;
        int tmpOptionType = in.readInt();
    }

    public static final Creator<Option> CREATOR = new Creator<Option>() {
        @Override
        public Option createFromParcel(Parcel source) {
            return new Option(source);
        }

        @Override
        public Option[] newArray(int size) {
            return new Option[size];
        }
    };
}
