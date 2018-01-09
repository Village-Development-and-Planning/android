package com.puthuvaazhvu.mapping.views.fragments.options;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.views.fragments.options.adapters.CheckBoxAdapter;
import com.puthuvaazhvu.mapping.views.fragments.options.adapters.CheckableOptionsAsListAdapter;
import com.puthuvaazhvu.mapping.views.fragments.options.adapters.RadioButtonAdapter;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class CheckBoxOptionsAsListUI extends CheckableOptionsUI {
    private RecyclerView recyclerView;

    public CheckBoxOptionsAsListUI(ViewGroup frame, Context context, CheckableOptionsAsListUIData checkableOptionsAsListUIData) {
        super(frame, context, checkableOptionsAsListUIData);
    }

    @Override
    public View createView() {
        View view = super.createView();
        recyclerView = view.findViewById(R.id.options_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false);
        recyclerView.setLayoutManager(linearLayoutManager);

        // set the adapter
        CheckableOptionsAsListAdapter checkableOptionsAsListAdapter = new CheckBoxAdapter(checkableOptionsAsListUIData);
        recyclerView.setAdapter(checkableOptionsAsListAdapter);

        return view;
    }

    @Override
    public ArrayList<Option> response() {
        if (checkableOptionsAsListUIData.getLoggedOptions().size() <= 0) {
            return null;
        }
        return getResponse();
    }
}
