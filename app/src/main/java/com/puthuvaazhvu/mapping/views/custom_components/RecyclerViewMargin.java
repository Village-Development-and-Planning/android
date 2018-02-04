package com.puthuvaazhvu.mapping.views.custom_components;

import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewMargin extends RecyclerView.ItemDecoration {
    private int margin;

    /**
     * constructor
     *
     * @param margin desirable margin size in px between the views in the recyclerView
     */
    public RecyclerViewMargin(int margin) {
        this.margin = margin;
    }

    /**
     * Set different margins for the items inside the recyclerView: no top margin for the first row
     * and no left margin for the first column.
     */
    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        //set right margin to all
        outRect.right = margin;
        //set bottom margin to all
        outRect.bottom = margin;

        outRect.top = margin;
        outRect.left = margin;
    }
}