package com.puthuvaazhvu.mapping.views.fragments.option.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.modals.Option;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class SingleOptionData implements Parcelable {
    private final String id;
    private final String text;
    private final String position;
    private boolean isSelected;
    private int backgroundColor;

    public SingleOptionData(String id, String text, String position, boolean isSelected, int backgroundColor) {
        this.id = id;
        this.text = text;
        this.position = position;
        this.isSelected = isSelected;
        this.backgroundColor = backgroundColor;
    }

    public String getPosition() {
        return position;
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

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public static SingleOptionData adapter(Option option, boolean isSelected, int backgroundColor) {
        return new SingleOptionData(
                option.getId(),
                option.getTextString(),
                option.getPosition(),
                isSelected,
                backgroundColor
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.text);
        dest.writeString(this.position);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.backgroundColor);
    }

    protected SingleOptionData(Parcel in) {
        this.id = in.readString();
        this.text = in.readString();
        this.position = in.readString();
        this.isSelected = in.readByte() != 0;
        this.backgroundColor = in.readInt();
    }

    public static final Creator<SingleOptionData> CREATOR = new Creator<SingleOptionData>() {
        @Override
        public SingleOptionData createFromParcel(Parcel source) {
            return new SingleOptionData(source);
        }

        @Override
        public SingleOptionData[] newArray(int size) {
            return new SingleOptionData[size];
        }
    };
}
