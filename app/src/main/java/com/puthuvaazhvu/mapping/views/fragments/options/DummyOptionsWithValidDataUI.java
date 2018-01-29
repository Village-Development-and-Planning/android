package com.puthuvaazhvu.mapping.views.fragments.options;

import android.content.Context;
import android.graphics.Path;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class DummyOptionsWithValidDataUI extends OptionsUI {
    private String dummyText;

    public DummyOptionsWithValidDataUI(ViewGroup frame, Context context, String dummyText, Question question) {
        super(frame, context, question);
        this.dummyText = dummyText;
    }

    @Override
    public View createView() {
        return null;
    }

    @Override
    public ArrayList<Option> response() {
        return QuestionUtils.generateQuestionWithDummyAndValidOptions();
    }
}
