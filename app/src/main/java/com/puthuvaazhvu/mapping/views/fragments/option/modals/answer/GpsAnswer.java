package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class GpsAnswer extends Answer {
    private final long lat;
    private final long lng;

    public GpsAnswer(String questionID, String questionText, long lat, long lng) {
        super(questionID, questionText);
        this.lat = lat;
        this.lng = lng;
    }

    public long getLat() {
        return lat;
    }

    public long getLng() {
        return lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.lat);
        dest.writeLong(this.lng);
    }

    protected GpsAnswer(Parcel in) {
        super(in);
        this.lat = in.readLong();
        this.lng = in.readLong();
    }

    public static final Creator<GpsAnswer> CREATOR = new Creator<GpsAnswer>() {
        @Override
        public GpsAnswer createFromParcel(Parcel source) {
            return new GpsAnswer(source);
        }

        @Override
        public GpsAnswer[] newArray(int size) {
            return new GpsAnswer[size];
        }
    };
}
