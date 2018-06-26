package org.ptracking.vdp.views.fragments.options;

import android.content.Context;
import android.view.ViewGroup;

import org.ptracking.vdp.modals.Option;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.modals.Text;
import org.ptracking.vdp.views.fragments.options.modals.CheckableOptionsAsListUIData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public abstract class CheckableOptionsUI extends ListOptionsUI {
    public final CheckableOptionsAsListUIData checkableOptionsAsListUIData;
    protected boolean shouldScroll;

    public CheckableOptionsUI(ViewGroup frame,
                              Context context,
                              CheckableOptionsAsListUIData checkableOptionsAsListUIData,
                              Question question) {
        super(frame, context, question);
        this.checkableOptionsAsListUIData = checkableOptionsAsListUIData;
        this.shouldScroll = true;
    }

    public CheckableOptionsUI(
            ViewGroup frame,
            Context context,
            CheckableOptionsAsListUIData checkableOptionsAsListUIData,
            Question question,
            boolean shouldScroll) {
        this(frame, context, checkableOptionsAsListUIData, question);
        this.shouldScroll = shouldScroll;
    }

    public ArrayList<Option> getResponse(String type) {
        ArrayList<Option> options = new ArrayList<>();
        for (CheckableOptionsAsListUIData.SingleDataOption singleDataOption :
                checkableOptionsAsListUIData.getLoggedOptions()) {
            Option option = new Option(
                    type,
                    new Text(singleDataOption.getText(), singleDataOption.getText()),
                    singleDataOption.getPosition()
            );
            option.setValue(singleDataOption.getOptionTextObj().getEnglish());
            options.add(option);
        }
        return options;
    }
}
