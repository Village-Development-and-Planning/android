package com.puthuvaazhvu.mapping.application.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;

/**
 * Created by muthuveerappans on 11/11/17.
 */

public class ApplicationData implements Parcelable {
    private static ApplicationData applicationData;

    public static ApplicationData getInstance() {
        if (applicationData == null) {
            applicationData = new ApplicationData();
        }
        return applicationData;
    }

    private Survey survey;

    private ApplicationData() {

    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.survey, flags);
    }

    protected ApplicationData(Parcel in) {
        this.survey = in.readParcelable(Survey.class.getClassLoader());
    }

    public static final Creator<ApplicationData> CREATOR = new Creator<ApplicationData>() {
        @Override
        public ApplicationData createFromParcel(Parcel source) {
            return new ApplicationData(source);
        }

        @Override
        public ApplicationData[] newArray(int size) {
            return new ApplicationData[size];
        }
    };
}
