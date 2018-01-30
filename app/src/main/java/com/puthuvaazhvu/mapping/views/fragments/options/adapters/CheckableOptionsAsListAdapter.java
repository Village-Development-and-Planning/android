package com.puthuvaazhvu.mapping.views.fragments.options.adapters;

import android.support.v7.widget.RecyclerView;

import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public abstract class CheckableOptionsAsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final CheckableOptionsAsListUIData optionsUIData;

    public CheckableOptionsAsListAdapter(CheckableOptionsAsListUIData optionsUIData) {
        this.optionsUIData = optionsUIData;
    }

    public boolean hasOptionsLimitReached() {
        int maxCount = optionsUIData.getFlowPattern().getQuestionFlow().getOptionsLimit();
        if (maxCount <= 0) return false;
        return optionsUIData.getLoggedOptions().size() == maxCount;
    }

    @Override
    public int getItemCount() {
        return optionsUIData.getSingleDataOptionArrayList().size();
    }
}
