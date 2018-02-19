package com.puthuvaazhvu.mapping.views.fragments.options.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.modals.FlowPattern;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class OptionsUIData {
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

    public static OptionsUIData adapter(Question question) {
        return new OptionsUIData("",
                question.getNumber(), question.getTextString(), question.getFlowPattern());
    }
}

