package com.puthuvaazhvu.mapping.views.fragments.options;

import android.content.Context;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.views.fragments.options.factory.OptionsUIFactory;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.OptionsUIData;

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
        QuestionFlow.UI ui = question.getFlowPattern().getQuestionFlow().getUiMode();
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
