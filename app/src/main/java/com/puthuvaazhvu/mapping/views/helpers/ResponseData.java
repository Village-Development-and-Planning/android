package com.puthuvaazhvu.mapping.views.helpers;

import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.AnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/15/17.
 */

public class ResponseData {
    public final String id;
    public final ArrayList<Option> response;

    public ResponseData(String id, ArrayList<Option> response) {
        this.id = id;
        this.response = response;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Option> getResponse() {
        return response;
    }

    public static ResponseData adapter(QuestionData questionData) {
        String id = questionData.getSingleQuestion().getId();

        OptionData responseData = questionData.getResponseData();
        if (responseData == null) {
            throw new IllegalArgumentException("The response questionData for the given question id "
                    + questionData.getSingleQuestion().getId() + " is null.");
        }

        AnswerData responseAnswerData = responseData.getAnswerData();
        if (responseAnswerData == null) {
            throw new IllegalArgumentException("The response answer for the given question id "
                    + questionData.getSingleQuestion().getId() + " is null.");
        }

        ArrayList<Option> response = new ArrayList<>();
        response.addAll(responseAnswerData.getOption());

        return new ResponseData(id, response);
    }
}
