package com.puthuvaazhvu.mapping.views.helpers;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.helpers.back_navigation.IBackFlow;
import com.puthuvaazhvu.mapping.views.helpers.next_flow.IFlow;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/15/17.
 */

@Deprecated
public class FlowHelper {
    private final IFlow iFlow;

    public FlowHelper(IFlow iFlow) {
        this.iFlow = iFlow;
    }

    public IFlow.FlowData getNext() {
        return iFlow.getNext();
    }

    public IFlow update(ArrayList<Option> options) {
        return iFlow.update(options);
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
