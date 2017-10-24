package com.puthuvaazhvu.mapping.views.helpers.back_navigation;

import com.puthuvaazhvu.mapping.modals.Question;

/**
 * Created by muthuveerappans on 10/24/17.
 */

public interface IBackFlow {

    BackFlowData getPreviousQuestion(Question current);

    class BackFlowData {
        public Question question;
        public boolean isError;
    }
}
