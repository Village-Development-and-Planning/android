package org.ptracking.vdp.views.fragments.options;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.ptracking.vdp.R;
import org.ptracking.vdp.modals.FlowPattern;
import org.ptracking.vdp.modals.Option;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.modals.Text;
import org.ptracking.vdp.views.fragments.options.modals.OptionsUIData;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class InputOptionsUI extends OptionsUI {
    private EditText editText;
    private final OptionsUIData optionData;

    public InputOptionsUI(ViewGroup frame, Context context, Question question, OptionsUIData optionData) {
        super(frame, context, question);
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

        if (getLatestOptions() != null) {
            Option lo = getLatestOptions().get(0);
            editText.setText(lo.getTextString());
        }

        return view;
    }

    @Override
    public void onNextPressed() {
        super.onNextPressed();
        dismissKeyboard();
    }

    @Override
    public ArrayList<Option> response() {
        ArrayList<Option> options = new ArrayList<>();
        String input = editText.getText().toString();
        if (input.isEmpty() || !checkDigitCountValidation(input) || !checkValidation(input)) {
            return null;
        }

        Option option = new Option(
                "INPUT",
                new Text(input, input),
                "INPUT"
        );
        option.setValue(input);
        options.add(option);
        return options;
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    // if true no problem
    private boolean checkValidation(String input) {
        FlowPattern flowPattern = question.getFlowPattern();

        if (flowPattern == null) return true;

        FlowPattern.QuestionFlow questionFlow = flowPattern.getQuestionFlow();

        if (questionFlow == null) return true;

        int limit = questionFlow.getValidationLimit();

        if (limit <= 0) return true;

        try {
            int value = Integer.valueOf(input);
            return value <= limit;
        } catch (NumberFormatException e) {
            Timber.e(e);
        }

        return true;
    }

    // if true no problem
    private boolean checkDigitCountValidation(String input) {
        FlowPattern flowPattern = question.getFlowPattern();

        if (flowPattern == null) return true;

        FlowPattern.QuestionFlow questionFlow = flowPattern.getQuestionFlow();

        if (questionFlow == null) return true;

        int limit = questionFlow.getValidationDigitsLimit();

        if (limit <= 0) return true;

        return limit == input.length();
    }
}
