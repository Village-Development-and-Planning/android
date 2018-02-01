package com.puthuvaazhvu.mapping.views.fragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.options.CheckableOptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.OptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData;

/**
 * Created by muthuveerappans on 01/02/18.
 */

public abstract class ListOptionsUI extends OptionsUI {

    public ListOptionsUI(ViewGroup frame, Context context, Question question) {
        super(frame, context, question);
    }

    @Override
    public View createView() {
        return inflateView(R.layout.options_list);
    }
}
