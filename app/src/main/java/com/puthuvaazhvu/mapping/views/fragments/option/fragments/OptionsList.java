package com.puthuvaazhvu.mapping.views.fragments.option.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.R;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public abstract class OptionsList extends Options {
    protected RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.options_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.options_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()
                , LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerView.Adapter adapter = getAdapter();
        recyclerView.setAdapter(adapter);
    }

    public abstract RecyclerView.Adapter getAdapter();
}
