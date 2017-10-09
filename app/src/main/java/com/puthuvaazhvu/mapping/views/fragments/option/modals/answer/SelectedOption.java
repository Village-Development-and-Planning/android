package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class SelectedOption implements Parcelable {
    private String json;

    public SelectedOption(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.json);
    }

    protected SelectedOption(Parcel in) {
        this.json = in.readString();
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
