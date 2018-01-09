package com.puthuvaazhvu.mapping.views.fragments.options;

import android.content.Context;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.OptionsUIData;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class OptionsUIFactory {
    private final Question question;
    private final ViewGroup frame;
    private final Context context;

    public OptionsUIFactory(Question question, ViewGroup frame) {
        this.question = question;
        this.frame = frame;
        this.context = frame.getContext();
    }

    public OptionsUI createOptionsUI() {
        QuestionFlow.UI ui = question.getFlowPattern().getQuestionFlow().getUiMode();
        switch (ui) {
            case GPS:
                return new GPSOptionsUI(frame, context);
            case INPUT:
                return new InputOptionsUI(frame, context, OptionsUIData.adapter(question));
            case MULTIPLE_CHOICE:
                return new CheckBoxOptionsAsListUI(frame, context, CheckableOptionsAsListUIData.adapter(question));
            case SINGLE_CHOICE:
                return new RadioButtonOptionsAsListUI(frame, context, CheckableOptionsAsListUIData.adapter(question));
            case INFO:
                return new DummyOptionsUI(frame, context, question.getTextForLanguage()); // Todo: change this
            case CONFIRMATION:
            case DUMMY:
            case MESSAGE:
            case NONE:
                return new DummyOptionsUI(frame, context, question.getTextForLanguage());
            default:
                Timber.e("No UI found for " + ui.name());
                return null;
        }
    }
}
