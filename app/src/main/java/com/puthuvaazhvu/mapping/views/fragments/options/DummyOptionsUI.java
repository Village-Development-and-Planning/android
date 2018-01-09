package com.puthuvaazhvu.mapping.views.fragments.options;

import android.content.Context;
import android.graphics.Path;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Text;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class DummyOptionsUI extends OptionsUI {
    private String dummyText;

    public DummyOptionsUI(ViewGroup frame, Context context, String dummyText) {
        super(frame, context);
        this.dummyText = dummyText;
    }

    @Override
    public View createView() {
        return null;
    }

    @Override
    public ArrayList<Option> response() {
        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option("", "dummy",
                new Text("", "dummy", "dummy", ""),
                "", ""));
        return options;
    }
}
