package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Text;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/7/17.
 */

public class InputAnswerData extends SingleAnswerData {
    public InputAnswerData(String questionID, String questionText, String text) {
        super(questionID, questionText, null, text, null);
    }

    @Override
    public ArrayList<Option> getOption() {
        ArrayList<Option> options = new ArrayList<>();
        Option option = new Option(optionID
                , Types.INPUT
                , new Text(null, text, text, null)
                , null
                , null);
        options.add(option);
        return options;
    }
}
