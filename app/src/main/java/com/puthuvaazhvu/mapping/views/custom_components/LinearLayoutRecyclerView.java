package com.puthuvaazhvu.mapping.views.custom_components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by muthuveerappans on 1/10/18.
 */

public class LinearLayoutRecyclerView extends LinearLayout {
    private RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            setUpChildren();
        }
    };

    private RecyclerView.Adapter adapter;

    public LinearLayoutRecyclerView(Context context) {
        super(context);
    }

    public LinearLayoutRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(@NonNull RecyclerView.Adapter adapter) {
        if (this.adapter != null) {
            adapter.unregisterAdapterDataObserver(adapterDataObserver);
        }

        this.adapter = adapter;

        adapter.registerAdapterDataObserver(adapterDataObserver);

        setUpChildren();
    }

    private void setUpChildren() {
        removeAllViews();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            RecyclerView.ViewHolder viewHolder = adapter.createViewHolder(this, adapter.getItemViewType(i));
            adapter.bindViewHolder(viewHolder, i);
            View view = viewHolder.itemView;
            addViewInLayout(view, -1, view.getLayoutParams(), true);
        }
    }
}
