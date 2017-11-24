package com.puthuvaazhvu.mapping.views.fragments.question.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public class QuestionData implements Parcelable {
    private final SingleQuestion singleQuestion;
    private final OptionData optionOptionData;
    private OptionData responseData;

    private int position = -1;

    public QuestionData(SingleQuestion singleQuestion, OptionData optionOptionData, OptionData responseData) {
        this.singleQuestion = singleQuestion;
        this.optionOptionData = optionOptionData;
        this.responseData = responseData;
    }

    public QuestionData(SingleQuestion singleQuestion, OptionData optionOptionData) {
        this.singleQuestion = singleQuestion;
        this.optionOptionData = optionOptionData;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SingleQuestion getSingleQuestion() {
        return singleQuestion;
    }

    public OptionData getOptionOptionData() {
        return optionOptionData;
    }

    public OptionData getResponseData() {
        return responseData;
    }

    public void setResponseData(OptionData responseOptionData) {
        this.responseData = responseOptionData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.singleQuestion, flags);
        dest.writeParcelable(this.optionOptionData, flags);
        dest.writeParcelable(this.responseData, flags);
    }

    protected QuestionData(Parcel in) {
        this.singleQuestion = in.readParcelable(SingleQuestion.class.getClassLoader());
        this.optionOptionData = in.readParcelable(OptionData.class.getClassLoader());
        this.responseData = in.readParcelable(OptionData.class.getClassLoader());
    }

    public static QuestionData adapter(Question question) {
        SingleQuestion q = SingleQuestion.adapter(question);
        OptionData optionOptionData
                = OptionData.adapter(question);
        return new QuestionData(q, optionOptionData);
    }

}
