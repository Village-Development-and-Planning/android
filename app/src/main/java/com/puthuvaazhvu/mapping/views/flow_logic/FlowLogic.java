package com.puthuvaazhvu.mapping.views.flow_logic;

import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 16/01/18.
 */

public abstract class FlowLogic {
    protected FlowData currentFlowData;

    public FlowLogic() {
        currentFlowData = new FlowData();
    }

    public abstract void setCurrent(Question question, FlowData.FlowUIType flowUIType);

    /**
     * Gets the current question
     *
     * @return - The currently pointed question
     */
    public abstract FlowData getCurrent();

    /**
     * Finishes the current question and moves to the next one
     *
     * @return Instance of {@link FlowLogic}
     */
    public abstract FlowLogic finishCurrent();

    /**
     * Moves to the particular child of the current question
     *
     * @param index
     * @return Instance of {@link FlowLogic}
     */
    public abstract FlowLogic moveToIndexInChild(int index);

    /**
     * Updates the current question with the answer
     *
     * @param response The data that contains the answer
     * @return Instance of {@link FlowLogic}
     */
    public abstract FlowLogic update(ArrayList<Option> response);

    /**
     * Get's the next question
     * This method also updates the pointer
     *
     * @return The next question to be processed
     */
    public abstract FlowData getNext();

    public abstract FlowData getPrevious();

    public FlowData getCurrentFlowData() {
        return currentFlowData;
    }

    public static class FlowData {
        public enum FlowUIType {
            GRID, TOGETHER, DEFAULT, END, LOOP
        }

        public FlowUIType flowType = FlowUIType.DEFAULT;
        public Question question;

        protected FlowData copy() {
            FlowData flowData = new FlowData();
            flowData.flowType = this.flowType;
            flowData.question = this.question;
            return flowData;
        }
    }
}
