package com.puthuvaazhvu.mapping.Question.SingleQuestion;

import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Question.QuestionModal;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public interface QuestionFragmentCommunicationInterface {
    public void moveToNextQuestion(QuestionModal currentQuestion, ArrayList<OptionData> optionDataList);

    public void moveToPreviousQuestion(QuestionModal currentQuestion);
}
