package com.puthuvaazhvu.mapping.views.helpers.next_flow;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.helpers.FlowType;
import com.puthuvaazhvu.mapping.views.helpers.ResponseData;
import com.puthuvaazhvu.mapping.views.helpers.back_navigation.IBackFlow;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/15/17.
 */

public interface IFlow {

    public FlowData getCurrentQuestionFlowData();

    /**
     * Gets the current question that the imaginary pointer is pointing to
     *
     * @return - The currently pointed question
     */
    Question getCurrent();

    /**
     * Finishes the current question and moves to the next one
     *
     * @return Instance of {@link IFlow}
     */
    IFlow finishCurrent();

    /**
     * Moves to the particular child of the current question
     *
     * @param index
     * @return Instance of {@link IFlow}
     */
    IFlow moveToIndex(int index);

    /**
     * Updates the current question with the answer
     *
     * @param responseData The data that contains the answer
     * @return Instance of {@link IFlow}
     */
    IFlow update(ResponseData responseData);

    ArrayList<Question> emptyToBeRemovedList();

    /**
     * Get's the next question
     * This method also updates the pointer
     *
     * @return The next question to be processed
     */
    FlowData getNext();

    IBackFlow.BackFlowData getPrevious();

    class FlowData {
        public Question question;
        public FlowType flowType;
    }
}
