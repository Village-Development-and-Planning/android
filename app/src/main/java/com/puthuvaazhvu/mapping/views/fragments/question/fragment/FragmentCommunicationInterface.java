package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import com.puthuvaazhvu.mapping.modals.*;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public interface FragmentCommunicationInterface {
    void onQuestionAnswered(Data data, boolean isNewRoot, boolean shouldLogOption);

    void onBackPressedFromQuestion(Data currentData);

    void onErrorWhileAnswering(String message);
}
