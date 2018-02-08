package com.puthuvaazhvu.mapping.views.flow_logic;

import android.support.v4.app.Fragment;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 16/01/18.
 */

public abstract class FlowLogic {
    JsonObject authJson;

    public FlowLogic() {
    }

    public JsonObject getAuthJson() {
        return authJson;
    }

    public void setAuthJson(JsonObject authJson) {
        this.authJson = authJson;
    }

    public abstract void setCurrent(Question question);

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
    public abstract FlowData finishCurrent();

    /**
     * Moves to the particular child of the current question
     *
     * @param index
     * @return Instance of {@link FlowLogic}
     */
    public abstract FlowData moveToIndexInChild(int index);

    /**
     * Updates the current question with the answer
     *
     * @param response The data that contains the answer
     * @return Instance of {@link FlowLogic}
     */
    public abstract boolean update(ArrayList<Option> response);

    /**
     * Get's the next question
     * This method also updates the pointer
     *
     * @return The next question to be processed
     */
    public abstract FlowData getNext();

    public abstract FlowData getPrevious();

    public static class FlowData {
        private Fragment fragment;
        private Question question;

        public Fragment getFragment() {
            return fragment;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }

        public Question getQuestion() {
            return question;
        }

        public void setQuestion(Question question) {
            this.question = question;
        }

        public FlowData copy() {
            FlowData flowData = new FlowData();
            flowData.fragment = this.fragment;
            flowData.question = this.question;
            return flowData;
        }

    }
}
