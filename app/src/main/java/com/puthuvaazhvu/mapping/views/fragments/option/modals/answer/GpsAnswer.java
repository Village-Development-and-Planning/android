package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;

import com.google.gson.JsonObject;

/**
 * Created by muthuveerappans on 9/30/17.
 */

/*
    Json data of the form:
    {
        id: <val>,
        type: <val>,
        data: {
            lat: <val>,
            lng: <cal>
        }
    }
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
    public SelectedOption getSelectedOptions() {
        String optionID = null;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", optionID);
        jsonObject.addProperty("type", Types.GPS);
        JsonObject data = new JsonObject();
        jsonObject.add("data", data);
        data.addProperty("lat", lat);
        data.addProperty("lng", lng);
        return new SelectedOption(jsonObject.toString());
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
