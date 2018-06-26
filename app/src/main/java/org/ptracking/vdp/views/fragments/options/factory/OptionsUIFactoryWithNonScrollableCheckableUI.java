package org.ptracking.vdp.views.fragments.options.factory;

import android.view.ViewGroup;

import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.views.fragments.options.CheckBoxOptionsAsListUI;
import org.ptracking.vdp.views.fragments.options.RadioButtonOptionsAsListUI;
import org.ptracking.vdp.views.fragments.options.modals.CheckableOptionsAsListUIData;

/**
 * Created by muthuveerappans on 1/10/18.
 */

public class OptionsUIFactoryWithNonScrollableCheckableUI extends OptionsUIFactory {

    public OptionsUIFactoryWithNonScrollableCheckableUI(Question question, ViewGroup frame) {
        super(question, frame);
    }

    @Override
    public CheckBoxOptionsAsListUI createCheckBoxOptionsAsListUI() {
        return new CheckBoxOptionsAsListUI(frame,
                context,
                CheckableOptionsAsListUIData.adapter(question),
                question,
                false);
    }

    @Override
    public RadioButtonOptionsAsListUI createRadioButtonOptionsAsListUI() {
        return new RadioButtonOptionsAsListUI(frame,
                context,
                CheckableOptionsAsListUIData.adapter(question),
                question,
                false);
    }
}
