package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Text;

import java.util.ArrayList;

import static com.google.gson.JsonNull.INSTANCE;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class GpsAnswerData extends AnswerData {
    private final double lat;
    private final double lng;

    public GpsAnswerData(String questionID, String questionText, double lat, double lng) {
        super(questionID, questionText);
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public ArrayList<Option> getOption() {
        ArrayList<Option> options = new ArrayList<>();
        String gps = lat + "," + lng;
        Option option = new Option(null
                , Types.GPS
                , new Text(null, gps, gps, null)
                , null
                , null);
        options.add(option);
        return options;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
    }

    protected GpsAnswerData(Parcel in) {
        super(in);
        this.lat = in.readLong();
        this.lng = in.readLong();
    }

    public static final Creator<GpsAnswerData> CREATOR = new Creator<GpsAnswerData>() {
        @Override
        public GpsAnswerData createFromParcel(Parcel source) {
            return new GpsAnswerData(source);
        }

        @Override
        public GpsAnswerData[] newArray(int size) {
            return new GpsAnswerData[size];
        }
    };
}
