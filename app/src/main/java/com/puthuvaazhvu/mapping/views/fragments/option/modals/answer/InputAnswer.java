package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import com.google.gson.JsonObject;

/**
 * Created by muthuveerappans on 10/7/17.
 */

public class InputAnswer extends SingleAnswer {
    public InputAnswer(String questionID, String questionText, String text) {
        super(questionID, questionText, null, text);
    }

    @Override
    public SelectedOption getSelectedOptions() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", optionID);
        jsonObject.addProperty("type", Types.INPUT);
        JsonObject data = new JsonObject();
        jsonObject.add("data", data);
        data.addProperty("text", text);
        return new SelectedOption(jsonObject.toString());
    }
}
