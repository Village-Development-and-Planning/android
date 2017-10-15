package com.puthuvaazhvu.mapping.views.helpers;

import com.puthuvaazhvu.mapping.modals.Question;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/15/17.
 */

public class FlowHelper {
    private final IFlowHelper iFlowHelper;

    public FlowHelper(IFlowHelper iFlowHelper) {
        this.iFlowHelper = iFlowHelper;
    }

    public IFlowHelper.FlowData getNext() {
        return iFlowHelper.getNext();
    }

    public IFlowHelper update(ResponseData responseData) {
        return iFlowHelper.update(responseData);
    }

    public IFlowHelper moveToIndex(int index) {
        return iFlowHelper.moveToIndex(index);
    }

    public IFlowHelper finishCurrentQuestion() {
        return iFlowHelper.finishCurrent();
    }

    public Question getCurrent() {
        return iFlowHelper.getCurrent();
    }

    public ArrayList<Question> emptyToBeRemovedList() {
        return iFlowHelper.emptyToBeRemovedList();
    }
}
