package com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces;

import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.question.types.QuestionFragmentTypes;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 13/05/18.
 */

public interface QuestionFragmentCallbacks {
    void onNextPressed(QuestionFragmentTypes type, ArrayList<Option> response);

    void onBackPressed(QuestionFragmentTypes type, Object... args);

    void onError(String message);
}
