package org.ptracking.vdp.views.fragments.question.Communicationinterfaces;

import org.ptracking.vdp.modals.Option;
import org.ptracking.vdp.views.fragments.question.types.QuestionFragmentTypes;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 13/05/18.
 */

public interface QuestionFragmentCallbacks {
    void onNextPressed(QuestionFragmentTypes type, ArrayList<Option> response);

    void onBackPressed(QuestionFragmentTypes type, Object... args);

    void onError(String message);
}
