package com.puthuvaazhvu.mapping.views.fragments.question.modals;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public class Data implements Parcelable {
    private final Question question;
    private final com.puthuvaazhvu.mapping.views.fragments.option.modals.Data optionData;
    private com.puthuvaazhvu.mapping.views.fragments.option.modals.Data responseData;

    public Data(Question question, com.puthuvaazhvu.mapping.views.fragments.option.modals.Data optionData, com.puthuvaazhvu.mapping.views.fragments.option.modals.Data responseData) {
        this.question = question;
        this.optionData = optionData;
        this.responseData = responseData;
    }

    public Question getQuestion() {
        return question;
    }

    public com.puthuvaazhvu.mapping.views.fragments.option.modals.Data getOptionData() {
        return optionData;
    }

    public com.puthuvaazhvu.mapping.views.fragments.option.modals.Data getResponseData() {
        return responseData;
    }

    public void setResponseData(com.puthuvaazhvu.mapping.views.fragments.option.modals.Data responseData) {
        this.responseData = responseData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.question, flags);
        dest.writeParcelable(this.optionData, flags);
        dest.writeParcelable(this.responseData, flags);
    }

    protected Data(Parcel in) {
        this.question = in.readParcelable(Question.class.getClassLoader());
        this.optionData = in.readParcelable(com.puthuvaazhvu.mapping.views.fragments.option.modals.Data.class.getClassLoader());
        this.responseData = in.readParcelable(com.puthuvaazhvu.mapping.views.fragments.option.modals.Data.class.getClassLoader());
    }

}
