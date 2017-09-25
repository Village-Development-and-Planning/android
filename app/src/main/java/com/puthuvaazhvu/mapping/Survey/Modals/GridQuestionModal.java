package com.puthuvaazhvu.mapping.Survey.Modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.Survey.Options.Modal.OptionData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/25/17.
 */

public class GridQuestionModal extends QuestionModal implements Parcelable {
    int questionCount;

    private GridQuestionModal(String questionID
            , String rawNumber
            , String text
            , ArrayList<OptionData> optionDataList
            , ArrayList<QuestionType> questionTypes
            , ArrayList<QuestionModal> children
            , boolean isNextPresent
            , boolean isPreviousPresent
            , boolean isQuestionAnswered
            , int count
            , Info info) {
        super(questionID
                , rawNumber
                , text
                , optionDataList
                , questionTypes
                , children
                , isNextPresent
                , isPreviousPresent
                , isQuestionAnswered
                , info);
        this.questionCount = count;
    }

    public GridQuestionModal(Parcel in) {
        super(in);
        questionCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(questionCount);
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public static final Parcelable.Creator<GridQuestionModal> CREATOR = new Parcelable.Creator<GridQuestionModal>() {
        @Override
        public GridQuestionModal createFromParcel(Parcel in) {
            return new GridQuestionModal(in);
        }

        @Override
        public GridQuestionModal[] newArray(int size) {
            return new GridQuestionModal[size];
        }
    };


    public static GridQuestionModal questionModalAdapter(QuestionModal questionModal, int questionCount) {
        return new GridQuestionModal(questionModal.getQuestionID()
                , questionModal.getRawNumber()
                , questionModal.getText()
                , questionModal.getOptionDataList()
                , questionModal.getQuestionTypes()
                , questionModal.getChildren()
                , questionModal.isNextPresent()
                , questionModal.isPreviousPresent()
                , questionModal.isAnswered()
                , questionCount
                , questionModal.getInfo());
    }
}
