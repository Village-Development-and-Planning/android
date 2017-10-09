package com.puthuvaazhvu.mapping.views.helpers.flow;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/9/17.
 */

public interface QuestionFlowHelper {
    Question getNext();

    void setCurrent(Question currentQuestion);

    Question getCurrent();

    ArrayList<Question> clearToBeRemovedList();
}
