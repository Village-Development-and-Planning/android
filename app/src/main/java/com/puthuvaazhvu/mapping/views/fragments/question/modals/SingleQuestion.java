package com.puthuvaazhvu.mapping.views.fragments.question.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public class SingleQuestion implements Parcelable {
    private String id;
    private String text;
    private String rawNumber;
    private String position;
    private boolean back = true;

    public SingleQuestion(String id, String text, String rawNumber, String position) {
        this.id = id;
        this.text = text;
        this.rawNumber = rawNumber;
        this.position = position;
    }

    public SingleQuestion(String id, String text, String rawNumber, String position, boolean back) {
        this(id, text, rawNumber, position);
        this.back = back;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getRawNumber() {
        return rawNumber;
    }

    public String getPosition() {
        return position;
    }

    public boolean isBack() {
        return back;
    }

    public static SingleQuestion adapter(Question question) {
        boolean backAllowed = true;
        if (question.getFlowPattern() != null) {
            QuestionFlow questionFlow = question.getFlowPattern().getQuestionFlow();
            if (questionFlow != null) {
                backAllowed = questionFlow.isBack();
            }
        }
        SingleQuestion q = new SingleQuestion(question.getRawNumber(),
                question.getTextString(),
                question.getRawNumber(),
                question.getPosition(),
                backAllowed);
        return q;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.text);
        dest.writeString(this.rawNumber);
        dest.writeString(this.position);
        dest.writeByte(this.back ? (byte) 1 : (byte) 0);
    }

    protected SingleQuestion(Parcel in) {
        this.id = in.readString();
        this.text = in.readString();
        this.rawNumber = in.readString();
        this.position = in.readString();
        this.back = in.readByte() != 0;
    }

    public static final Creator<SingleQuestion> CREATOR = new Creator<SingleQuestion>() {
        @Override
        public SingleQuestion createFromParcel(Parcel source) {
            return new SingleQuestion(source);
        }

        @Override
        public SingleQuestion[] newArray(int size) {
            return new SingleQuestion[size];
        }
    };
}
