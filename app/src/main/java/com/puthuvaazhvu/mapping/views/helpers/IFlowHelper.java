package com.puthuvaazhvu.mapping.views.helpers;

import com.puthuvaazhvu.mapping.modals.Question;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/15/17.
 */

public interface IFlowHelper {

    /**
     * Gets the current question that the imaginary pointer is pointing to
     *
     * @return - The currently pointed question
     */
    Question getCurrent();

    /**
     * Finishes the current question and moves to the next one
     *
     * @return Instance of {@link IFlowHelper}
     */
    IFlowHelper finishCurrent();

    /**
     * Moves to the particular child of the current question
     *
     * @param index
     * @return Instance of {@link IFlowHelper}
     */
    IFlowHelper moveToIndex(int index);

    /**
     * Updates the current question with the answer
     *
     * @param responseData The data that contains the answer
     * @return Instance of {@link IFlowHelper}
     */
    IFlowHelper update(ResponseData responseData);

    ArrayList<Question> emptyToBeRemovedList();

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
