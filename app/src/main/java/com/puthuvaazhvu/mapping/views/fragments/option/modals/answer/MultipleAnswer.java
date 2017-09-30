package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class MultipleAnswer extends Answer {
    private final ArrayList<SelectedOption> selectedOptionArrayList;

    public MultipleAnswer(String questionID, String questionText, ArrayList<SelectedOption> selectedOptionArrayList) {
        super(questionID, questionText);
        this.selectedOptionArrayList = selectedOptionArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.selectedOptionArrayList);
    }

    protected MultipleAnswer(Parcel in) {
        super(in);
        this.selectedOptionArrayList = in.createTypedArrayList(SelectedOption.CREATOR);
    }

    public static final Creator<MultipleAnswer> CREATOR = new Creator<MultipleAnswer>() {
        @Override
        public MultipleAnswer createFromParcel(Parcel source) {
            return new MultipleAnswer(source);
        }

        @Override
        public MultipleAnswer[] newArray(int size) {
            return new MultipleAnswer[size];
        }
    };
}
