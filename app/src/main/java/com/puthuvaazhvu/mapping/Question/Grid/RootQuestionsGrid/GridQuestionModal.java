package com.puthuvaazhvu.mapping.Question.Grid.RootQuestionsGrid;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Options.Modal.OPTION_TYPES;
import com.puthuvaazhvu.mapping.Question.QuestionModal;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/25/17.
 */

public class GridQuestionModal extends QuestionModal implements Parcelable {
    boolean isQuestionAnswered;

    public GridQuestionModal(String questionID, String text, ArrayList<OptionData> optionDataList, OPTION_TYPES optionType, ArrayList<QuestionModal> children, boolean isNextPresent, boolean isPreviousPresent, boolean isQuestionAnswered) {
        super(questionID, text, optionDataList, optionType, children, isNextPresent, isPreviousPresent);
        this.isQuestionAnswered = isQuestionAnswered;
    }

    public GridQuestionModal(Parcel in) {
        super(in);
        in.writeInt(isQuestionAnswered ? 1 : 0);
    }

    public boolean isQuestionAnswered() {
        return isQuestionAnswered;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(isQuestionAnswered ? 1 : 0);
    }

    public void setQuestionAnswered(boolean questionAnswered) {
        isQuestionAnswered = questionAnswered;
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

    public static GridQuestionModal questionModalAdapter(QuestionModal questionModal) {
        return new GridQuestionModal(questionModal.getQuestionID(), questionModal.getText(), questionModal.getOptionDataList(), questionModal.getOptionType(), questionModal.getChildren(), questionModal.isNextPresent(), questionModal.isPreviousPresent(), false);
    }
}
