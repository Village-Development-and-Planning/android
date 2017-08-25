package com.puthuvaazhvu.mapping.Question.Loop;

import com.puthuvaazhvu.mapping.Question.QuestionModal;

import java.util.HashMap;

/**
 * Created by muthuveerappans on 8/25/17.
 */

public interface QuestionTreeRootLoopFragmentCommunicationInterface {
    public void onLoopFinished(HashMap<String, HashMap<String, QuestionModal>> result);
}
