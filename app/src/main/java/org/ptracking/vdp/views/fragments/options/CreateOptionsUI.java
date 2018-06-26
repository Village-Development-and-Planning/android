package org.ptracking.vdp.views.fragments.options;

import org.ptracking.vdp.modals.FlowPattern;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.views.fragments.options.factory.OptionsUIFactory;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class CreateOptionsUI {
    private final Question question;

    public CreateOptionsUI(Question question) {
        this.question = question;
    }

    public OptionsUI createOptionsUI(OptionsUIFactory optionsUIFactory) {
        FlowPattern.QuestionFlow.UI ui = question.getFlowPattern().getQuestionFlow().getUiMode();
        switch (ui) {
            case GPS:
                return optionsUIFactory.createGpsOptionUI();
            case INPUT:
                return optionsUIFactory.createInputOptionsUI();
            case MULTIPLE_CHOICE:
                return optionsUIFactory.createCheckBoxOptionsAsListUI();
            case SINGLE_CHOICE:
                return optionsUIFactory.createRadioButtonOptionsAsListUI();
            case INFO:
                return optionsUIFactory.createInfoOptionsUI();
            case CONFIRMATION:
            case DUMMY:
            case MESSAGE:
            case NONE:
                return optionsUIFactory.createDummyOptionsUI();
            default:
                Timber.e("No UI found for " + ui.name());
                return null;
        }
    }
}
