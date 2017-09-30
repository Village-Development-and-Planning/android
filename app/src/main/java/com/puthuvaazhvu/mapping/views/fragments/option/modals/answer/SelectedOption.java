package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class SelectedOption implements Parcelable {
    private String id;
    private String text;

    public SelectedOption(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.text);
    }

    protected SelectedOption(Parcel in) {
        this.id = in.readString();
        this.text = in.readString();
    }

    public static final Creator<SelectedOption> CREATOR = new Creator<SelectedOption>() {
        @Override
        public SelectedOption createFromParcel(Parcel source) {
            return new SelectedOption(source);
        }

        @Override
        public SelectedOption[] newArray(int size) {
            return new SelectedOption[size];
        }
    };
}
