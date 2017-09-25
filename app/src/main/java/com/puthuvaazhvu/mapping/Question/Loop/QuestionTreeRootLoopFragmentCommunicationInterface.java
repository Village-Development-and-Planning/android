package com.puthuvaazhvu.mapping.Question.Loop;

import com.puthuvaazhvu.mapping.Survey.Modals.QuestionModal;

import java.util.HashMap;

/**
 * Created by muthuveerappans on 8/25/17.
 */

public interface QuestionTreeRootLoopFragmentCommunicationInterface {
    // The question modal object is a deep copy and not a reference to the input.
    // Format: HashMap<question_id, HashMap<option_id, question_object>>
    public void onLoopFinished(HashMap<String, HashMap<String, QuestionModal>> result);
}
