package com.puthuvaazhvu.mapping.views.custom_components;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;

/**
 * Created by muthuveerappans on 11/7/17.
 */

public class LinearRecyclerView extends LinearListView {

    private RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            setUpChildren();
        }
    };

    private RecyclerView.Adapter adapter;

    public LinearRecyclerView(Context context) {
        super(context);
    }

    public LinearRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerView.Adapter getRecyclerAdapter() {
        return adapter;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver(dataObserver);
        }

        setUpChildren();

        this.adapter = adapter;

        if (adapter != null) {
            adapter.registerAdapterDataObserver(dataObserver);
        }
    }

    private void setUpChildren() {

        // remove all the previously added views
        removeAllViews();

        updateEmptyStatus((adapter == null) || adapter.getItemCount() == 0);

        if (adapter == null) {
            return;
        }

        for (int i = 0; i < adapter.getItemCount(); i++) {
            int viewType = adapter.getItemViewType(i);
            View child = adapter.createViewHolder(this, viewType).itemView;
            addViewInLayout(child, -1, child.getLayoutParams(), true);
        }
    }
}
