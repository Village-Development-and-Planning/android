package com.puthuvaazhvu.mapping.views.helpers.flow;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

/**
 * Created by muthuveerappans on 10/15/17.
 */

public interface IFlowHelper {

    /**
     * Gets the current question that the imaginary pointer is pointing to
     *
     * @return - The current pointed question
     */
    Question getCurrent();

    /**
     * Finishes the current question and moves to the next one
     *
     * @return Instance of {@link IFlowHelper}
     */
    IFlowHelper finishCurrentQuestion();

    /**
     * Moves
     *
     * @param index
     * @return Instance of {@link IFlowHelper}
     */
    IFlowHelper moveToIndex(int index);

    /**
     * Updates the current question with the answer
     *
     * @param questionData The data that contains the answer
     * @return Instance of {@link IFlowHelper}
     */
    IFlowHelper update(QuestionData questionData);

    /**
     * Get's the next question
     * This method also updates the pointer
     *
     * @return The next question to be processed
     */
    FlowData getNext();

    class FlowData {
        public Question question;
        public FlowType flowType;
    }
}
