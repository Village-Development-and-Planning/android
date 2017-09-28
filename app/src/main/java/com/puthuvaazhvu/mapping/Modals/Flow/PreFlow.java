package com.puthuvaazhvu.mapping.Modals.Flow;

import android.os.Parcel;
import android.os.Parcelable;

public class PreFlow implements Parcelable {
    private final String[] fill;
    private final String questionSkip;
    private final String[] optionSkip;

    public PreFlow(String[] fill, String questionSkip, String[] optionSkip) {
        this.fill = fill;
        this.questionSkip = questionSkip;
        this.optionSkip = optionSkip;
    }

    // TODO:


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(this.fill);
        dest.writeString(this.questionSkip);
        dest.writeStringArray(this.optionSkip);
    }

    protected PreFlow(Parcel in) {
        this.fill = in.createStringArray();
        this.questionSkip = in.readString();
        this.optionSkip = in.createStringArray();
    }

    public static final Creator<PreFlow> CREATOR = new Creator<PreFlow>() {
        @Override
        public PreFlow createFromParcel(Parcel source) {
            return new PreFlow(source);
        }

        @Override
        public PreFlow[] newArray(int size) {
            return new PreFlow[size];
        }
    };
}
