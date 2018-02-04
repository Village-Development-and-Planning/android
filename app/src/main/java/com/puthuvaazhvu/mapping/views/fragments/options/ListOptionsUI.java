package com.puthuvaazhvu.mapping.views.fragments.options;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.custom_components.RecyclerViewMargin;
import com.puthuvaazhvu.mapping.views.fragments.options.CheckableOptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.OptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData;

/**
 * Created by muthuveerappans on 01/02/18.
 */

public abstract class ListOptionsUI extends OptionsUI {
    RecyclerView recyclerView;

    public ListOptionsUI(ViewGroup frame, Context context, Question question) {
        super(frame, context, question);
    }

    @Override
    public View createView() {
        View view = inflateView(R.layout.options_list);
        recyclerView = view.findViewById(R.id.options_recycler_view);
        RecyclerViewMargin decoration = new RecyclerViewMargin(context.getResources().getDimensionPixelSize(R.dimen.options_list_row_margin));
        recyclerView.addItemDecoration(decoration);
        return view;
    }
}
