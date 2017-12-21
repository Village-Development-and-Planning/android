package com.puthuvaazhvu.mapping.views.helpers;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.helpers.back_navigation.IBackFlow;
import com.puthuvaazhvu.mapping.views.helpers.next_flow.IFlow;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/15/17.
 */

public class FlowHelper {
    private final IFlow iFlow;

    public FlowHelper(IFlow iFlow) {
        this.iFlow = iFlow;
    }

    public IFlow.FlowData getNext() {
        return iFlow.getNext();
    }

    public IFlow update(ResponseData responseData) {
        return iFlow.update(responseData);
    }

    public IFlow moveToIndex(int index) {
        return iFlow.moveToIndex(index);
    }

    public IFlow finishCurrentQuestion() {
        return iFlow.finishCurrent();
    }

    public Question getCurrent() {
        return iFlow.getCurrent();
    }

    public void setCurrent(Question question) {
        iFlow.setCurrent(question);
    }

    public IFlow.FlowData getPrevious() {
        return iFlow.getPrevious();
    }
}
