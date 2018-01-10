package com.puthuvaazhvu.mapping.views.fragments.options;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public abstract class CheckableOptionsUI extends OptionsUI {
    public final CheckableOptionsAsListUIData checkableOptionsAsListUIData;
    protected boolean shouldScroll;

    public CheckableOptionsUI(ViewGroup frame, Context context, CheckableOptionsAsListUIData checkableOptionsAsListUIData) {
        super(frame, context);
        this.checkableOptionsAsListUIData = checkableOptionsAsListUIData;
        this.shouldScroll = true;
    }

    public CheckableOptionsUI(
            ViewGroup frame,
            Context context,
            CheckableOptionsAsListUIData checkableOptionsAsListUIData,
            boolean shouldScroll) {
        this(frame, context, checkableOptionsAsListUIData);
        this.shouldScroll = shouldScroll;
    }

    public ArrayList<Option> getResponse() {
        ArrayList<Option> options = new ArrayList<>();
        for (CheckableOptionsAsListUIData.SingleData singleData : checkableOptionsAsListUIData.getLoggedOptions()) {
            options.add(new Option(
                    "",
                    "MULTIPLE",
                    new Text("", singleData.getText(), singleData.getText(), ""),
                    "",
                    singleData.getPosition()
            ));
        }
        return options;
    }

    @Override
    public View createView() {
        return inflateView(R.layout.options_list);
    }
}
