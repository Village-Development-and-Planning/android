package com.puthuvaazhvu.mapping.views.helpers.next_flow;

import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.flow.ChildFlow;
import com.puthuvaazhvu.mapping.views.helpers.FlowType;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/15/17.
 */

public interface IFlow {

    void setCurrent(Question question);

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
     * @param response The data that contains the answer
     * @return Instance of {@link IFlow}
     */
    IFlow update(ArrayList<Option> response);

    /**
     * Get's the next question
     * This method also updates the pointer
     *
     * @return The next question to be processed
     */
    FlowData getNext();

    IFlow.FlowData getPrevious();

    class FlowData {
        public Question question;
        public FlowType flowType;

        public static FlowData getFlowData(Question question) {
            FlowData flowData = new FlowData();
            flowData.question = question;

            ChildFlow childFlow = question.getFlowPattern().getChildFlow();
            ChildFlow.Modes childFlowMode = childFlow.getMode();
            ChildFlow.UI childFlowUI = childFlow.getUiToBeShown();

            if (childFlowMode == ChildFlow.Modes.TOGETHER) {
                flowData.flowType = FlowType.TOGETHER;
                return flowData;
            }

            flowData.flowType = FlowType.SINGLE;
            return flowData;
        }
    }
}
