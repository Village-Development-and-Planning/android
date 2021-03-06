package org.ptracking.vdp.views.fragments.options;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.ptracking.vdp.modals.Option;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.views.custom_components.ChildLinearLayoutManager;
import org.ptracking.vdp.views.fragments.options.adapters.CheckableOptionsAsListAdapter;
import org.ptracking.vdp.views.fragments.options.adapters.RadioButtonAdapter;
import org.ptracking.vdp.views.fragments.options.modals.CheckableOptionsAsListUIData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class RadioButtonOptionsAsListUI extends CheckableOptionsUI {
    //private RecyclerView recyclerView;

    public RadioButtonOptionsAsListUI(
            ViewGroup frame,
            Context context,
            CheckableOptionsAsListUIData checkableOptionsAsListUIData,
            Question question) {
        super(frame, context, checkableOptionsAsListUIData, question);
    }

    public RadioButtonOptionsAsListUI(
            ViewGroup frame,
            Context context,
            CheckableOptionsAsListUIData checkableOptionsAsListUIData,
            Question question,
            boolean shouldScroll) {
        super(frame, context, checkableOptionsAsListUIData, question, shouldScroll);
    }

    @Override
    public View createView() {
        View view = super.createView();
        //recyclerView = view.findViewById(R.id.options_recycler_view);

        RecyclerView.LayoutManager layoutManager;
        if (shouldScroll) {
            layoutManager = new LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false);
        } else {
            layoutManager = new ChildLinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL,
                    false);
        }

        recyclerView.setLayoutManager(layoutManager);

        // set the adapter
        CheckableOptionsAsListAdapter checkableOptionsAsListAdapter = new RadioButtonAdapter(checkableOptionsAsListUIData);
        recyclerView.setAdapter(checkableOptionsAsListAdapter);

        return view;
    }

    @Override
    public ArrayList<Option> response() {
        if (checkableOptionsAsListUIData.getLoggedOptions().size() <= 0) {
            return null;
        }
        return getResponse("SINGLE_CHOICE");
    }
}
