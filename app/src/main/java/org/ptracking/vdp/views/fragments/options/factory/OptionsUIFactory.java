package org.ptracking.vdp.views.fragments.options.factory;

import android.content.Context;
import android.view.ViewGroup;

import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.views.fragments.options.CheckBoxOptionsAsListUI;
import org.ptracking.vdp.views.fragments.options.DummyOptionsWithValidDataUI;
import org.ptracking.vdp.views.fragments.options.GPSOptionsUI;
import org.ptracking.vdp.views.fragments.options.InfoOptionsUI;
import org.ptracking.vdp.views.fragments.options.InputOptionsUI;
import org.ptracking.vdp.views.fragments.options.RadioButtonOptionsAsListUI;
import org.ptracking.vdp.views.fragments.options.modals.CheckableOptionsAsListUIData;
import org.ptracking.vdp.views.fragments.options.modals.OptionsUIData;

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
        return new DummyOptionsWithValidDataUI(frame, context, question.getTextString(), question);
    }

    public InfoOptionsUI createInfoOptionsUI() {
        return new InfoOptionsUI(frame, context, question);
    }
}
