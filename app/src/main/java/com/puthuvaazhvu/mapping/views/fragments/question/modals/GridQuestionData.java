package com.puthuvaazhvu.mapping.views.fragments.question.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public class GridQuestionData extends QuestionData implements Parcelable {
    private int count;

    public GridQuestionData(SingleQuestion singleQuestion, OptionData optionOptionData, OptionData responseOptionData, int count) {
        super(singleQuestion, optionOptionData, responseOptionData);
        this.count = count;
    }

    public GridQuestionData(SingleQuestion singleQuestion, OptionData optionOptionData, int count) {
        super(singleQuestion, optionOptionData);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.count);
    }

    protected GridQuestionData(Parcel in) {
        super(in);
        this.count = in.readInt();
    }

    public static final Creator<GridQuestionData> CREATOR = new Creator<GridQuestionData>() {
        @Override
        public GridQuestionData createFromParcel(Parcel source) {
            return new GridQuestionData(source);
        }

        @Override
        public GridQuestionData[] newArray(int size) {
            return new GridQuestionData[size];
        }
    };

    public static GridQuestionData adapter(com.puthuvaazhvu.mapping.modals.Question question) {
        QuestionData questionData = QuestionData.adapter(question);
        return new GridQuestionData(questionData.getSingleQuestion(), questionData.getOptionOptionData(), question.getAnswers().size());
    }

    public static ArrayList<GridQuestionData> adapter(ArrayList<com.puthuvaazhvu.mapping.modals.Question> children) {
        ArrayList<GridQuestionData> result = new ArrayList<>(children.size());
        for (com.puthuvaazhvu.mapping.modals.Question c : children) {
            result.add(GridQuestionData.adapter(c));
        }
        return result;
    }
}
