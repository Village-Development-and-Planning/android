package com.puthuvaazhvu.mapping.Question.Loop;

import com.puthuvaazhvu.mapping.Question.QuestionModal;

import java.util.HashMap;

/**
 * Created by muthuveerappans on 8/25/17.
 */

public interface QuestionTreeRootLoopFragmentCommunicationInterface {
    // The question modal object is a deep copy and not a reference to the input.
    public void onLoopFinished(HashMap<String, HashMap<String, QuestionModal>> result);
}
