package com.puthuvaazhvu.mapping.utils.ObjectToFromDisk;

import com.puthuvaazhvu.mapping.Question.QuestionModal;

import java.io.Serializable;

/**
 * Created by muthuveerappans on 9/1/17.
 */

public class QuestionOutput implements Serializable {
    String surveyID;
    String questionID;
    String optionID;
    QuestionModal questionModal;
    String filePath;

    public QuestionOutput(String surveyID, String questionID, String optionID, QuestionModal questionModal, String filePath) {
        this.surveyID = surveyID;
        this.questionID = questionID;
        this.optionID = optionID;
        this.questionModal = questionModal;
        this.filePath = filePath;
    }

    public String getSurveyID() {
        return surveyID;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getOptionID() {
        return optionID;
    }

    public QuestionModal getQuestionModal() {
        return questionModal;
    }

    public String getFilePath() {
        return filePath;
    }
}
