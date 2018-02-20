package com.puthuvaazhvu.mapping.views.fragments.options;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.options.adapters.ListAdapter;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 01/02/18.
 */

public class InfoOptionsUI extends ListOptionsUI {
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;

    public InfoOptionsUI(ViewGroup frame, Context context, Question question) {
        super(frame, context, question);
    }

    @Override
    public View createView() {
        View view = super.createView();
        recyclerView = view.findViewById(R.id.options_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false);

        recyclerView.setLayoutManager(layoutManager);

        listAdapter = new ListAdapter(question.getOptions());
        recyclerView.setAdapter(listAdapter);

        return view;
    }

    @Override
    public ArrayList<Option> response() {
        ArrayList<Option> response = new ArrayList<>();
        for (Option option : question.getOptions()) {
            Option o = new Option(option);
            o.setValue(option.getTextString());
            response.add(o);
        }
        return response;
    }
}
