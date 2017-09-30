package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class SingleAnswer extends Answer {
    private final SelectedOption selectedOption;

    public SingleAnswer(String questionID, String questionText, SelectedOption selectedOption) {
        super(questionID, questionText);
        this.selectedOption = selectedOption;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.selectedOption, flags);
    }

    protected SingleAnswer(Parcel in) {
        super(in);
        this.selectedOption = in.readParcelable(SelectedOption.class.getClassLoader());
    }

    public static final Creator<SingleAnswer> CREATOR = new Creator<SingleAnswer>() {
        @Override
        public SingleAnswer createFromParcel(Parcel source) {
            return new SingleAnswer(source);
        }

        @Override
        public SingleAnswer[] newArray(int size) {
            return new SingleAnswer[size];
        }
    };
}
