package org.ptracking.vdp.views.fragments.options;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.ptracking.vdp.modals.Option;
import org.ptracking.vdp.modals.Question;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class DummyOptionsWithValidDataUI extends OptionsUI {
    private String dummyText;

    public DummyOptionsWithValidDataUI(ViewGroup frame, Context context, String dummyText, Question question) {
        super(frame, context, question);
        this.dummyText = dummyText;
    }

    @Override
    public View createView() {
        return null;
    }

    @Override
    public ArrayList<Option> response() {
        return new ArrayList<>();
    }
}
