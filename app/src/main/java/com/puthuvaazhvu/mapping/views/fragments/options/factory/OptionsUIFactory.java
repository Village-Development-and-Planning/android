package com.puthuvaazhvu.mapping.views.fragments.options.factory;

import android.content.Context;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.views.fragments.options.CheckBoxOptionsAsListUI;
import com.puthuvaazhvu.mapping.views.fragments.options.DummyOptionsWithValidDataUI;
import com.puthuvaazhvu.mapping.views.fragments.options.GPSOptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.InputOptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.RadioButtonOptionsAsListUI;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.OptionsUIData;

/**
 * Created by muthuveerappans on 1/10/18.
 */

public class OptionsUIFactory {
    protected final Question question;
    protected final ViewGroup frame;
    protected final Context context;

    public OptionsUIFactory(Question question, ViewGroup frame) {
        this.question = question;
        this.frame = frame;
        this.context = frame.getContext();
    }

    public GPSOptionsUI createGpsOptionUI() {
        return new GPSOptionsUI(frame, context, question);
    }

    public InputOptionsUI createInputOptionsUI() {
        return new InputOptionsUI(frame, context, question, OptionsUIData.adapter(question));
    }

    public CheckBoxOptionsAsListUI createCheckBoxOptionsAsListUI() {
        return new CheckBoxOptionsAsListUI(frame, context, CheckableOptionsAsListUIData.adapter(question), question);
    }

    public RadioButtonOptionsAsListUI createRadioButtonOptionsAsListUI() {
        return new RadioButtonOptionsAsListUI(frame, context, CheckableOptionsAsListUIData.adapter(question), question);
    }

    public DummyOptionsWithValidDataUI createDummyOptionsUI() {
        return new DummyOptionsWithValidDataUI(frame, context, QuestionUtils.getTextString(question), question);
    }
}
