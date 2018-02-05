package com.puthuvaazhvu.mapping.modals.utils;

import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;

/**
 * Created by muthuveerappans on 30/01/18.
 */

public class AnswerUtils {
    public static boolean isAnswerDummy(Answer answer) {
        return answer.getOptions().size() > 0 && answer.getOptions().get(0).getId().equals("DUMMY");
    }

    public static boolean containsOption(String position, Answer answer) {
        for (Option option : answer.getOptions()) {
            if (option.getPosition().equals(position)) return true;
        }
        return false;
    }
}
