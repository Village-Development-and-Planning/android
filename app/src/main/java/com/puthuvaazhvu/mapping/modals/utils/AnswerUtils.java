package com.puthuvaazhvu.mapping.modals.utils;

import com.puthuvaazhvu.mapping.modals.Answer;

/**
 * Created by muthuveerappans on 30/01/18.
 */

public class AnswerUtils {
    public static boolean isAnswerDummy(Answer answer) {
        return answer.getOptions().size() > 0 && answer.getOptions().get(0).getId().equals("DUMMY");
    }
}
