package com.puthuvaazhvu.mapping.views.fragments.question.fragment.together;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 11/6/17.
 */

public interface Contract {
    interface View {
        void onAdapterFetched(ArrayList<QuestionData> adapterData);

        void onAnswersUpdated(Question root);
    }

    interface UserAction {
        void getAdapterData();

        void updateAnswers(final ArrayList<QuestionData> adapterData);
    }
}
