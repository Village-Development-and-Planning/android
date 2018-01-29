package com.puthuvaazhvu.mapping.views.fragments.options.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.flow.FlowPattern;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class OptionsUIData implements Parcelable {
    private final String questionID;
    private final String questionRawNumber;
    private final String questionText;
    private final FlowPattern flowPattern;

    public OptionsUIData(String questionID, String questionRawNumber, String questionText, FlowPattern flowPattern) {
        this.questionID = questionID;
        this.questionRawNumber = questionRawNumber;
        this.questionText = questionText;
        this.flowPattern = flowPattern;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getQuestionRawNumber() {
        return questionRawNumber;
    }

    public String getQuestionText() {
        return questionText;
    }

    public FlowPattern getFlowPattern() {
        return flowPattern;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.questionID);
        dest.writeString(this.questionRawNumber);
        dest.writeString(this.questionText);
        dest.writeParcelable(this.flowPattern, flags);
    }

    protected OptionsUIData(Parcel in) {
        this.questionID = in.readString();
        this.questionRawNumber = in.readString();
        this.questionText = in.readString();
        this.flowPattern = in.readParcelable(FlowPattern.class.getClassLoader());
    }

    public static OptionsUIData adapter(Question question) {
        return new OptionsUIData("",
                question.getRawNumber(), QuestionUtils.getTextString(question), question.getFlowPattern());
    }
}

