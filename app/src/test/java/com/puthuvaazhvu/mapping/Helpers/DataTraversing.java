package com.puthuvaazhvu.mapping.Helpers;

import com.puthuvaazhvu.mapping.modals.Question;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 20/02/18.
 */

public class DataTraversing {
    public static Question findQuestion(String number, Question node) {

        Question q = null;

        if (node.getNumber().equals(number)) {
            q = node;
        } else {
            ArrayList<Question> children = node.getChildren();
            if (children != null) {
                for (Question c : children) {
                    q = findQuestion(number, c);
                    if (q != null) break;
                }
            }
        }

        return q;
    }
}
