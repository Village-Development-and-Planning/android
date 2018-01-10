package com.puthuvaazhvu.mapping.views.fragments.options;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.OptionsUIData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class InputOptionsUI extends OptionsUI {
    private EditText editText;
    private final OptionsUIData optionData;

    public InputOptionsUI(ViewGroup frame, Context context, OptionsUIData optionData) {
        super(frame, context);
        this.optionData = optionData;
    }

    @Override
    public View createView() {
        View view = inflateView(R.layout.options_edt);
        editText = view.findViewById(R.id.input_edit_text);
        switch (optionData.getFlowPattern().getQuestionFlow().getValidation()) {
            case NUMBER:
                editText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case TEXT:
                editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                break;
            default:
                editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
        return view;
    }

    @Override
    public ArrayList<Option> response() {
        ArrayList<Option> options = new ArrayList<>();
        String input = editText.getText().toString();
        if (input.isEmpty()) {
            return null;
        }
        options.add(new Option("", "INPUT", new Text("", input, input, ""), "", ""));
        return options;
    }
}
