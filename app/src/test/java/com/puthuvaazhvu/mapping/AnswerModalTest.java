package com.puthuvaazhvu.mapping;

import com.puthuvaazhvu.mapping.Helpers.DataTraversing;
import com.puthuvaazhvu.mapping.Helpers.ReadTestFile;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.FlowPattern;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by muthuveerappans on 20/02/18.
 */

public class AnswerModalTest {
    private Survey dummySurvey;

    @Before
    public void setUp() throws Exception {
        dummySurvey = ReadTestFile.getTestSurvey(this, "answer_modal_testing.json");

        assertThat(
                "Check ID of the survey",
                dummySurvey.getId(),
                is("5a7886d9fdb3a526f48afe25")
        );
    }

    @Test
    public void test() throws Exception {

        Question root = dummySurvey.getQuestion();

        Question node = DataTraversing.findQuestion("1.1", root);

        Answer answer = Answer.createDummyAnswer(node);

        assertNotSame(
                "Check children object are not same for answer",
                answer.getChildren().get(0),
                node.getChildren().get(0)
        );

        assertSame(
                "Check parent is the same",
                answer.getChildren().get(0).getParent(),
                node
        );


        assertSame(
                "Check 2nd degree children is the same",
                answer.getChildren().get(0).getChildren().get(0),
                node.getChildren().get(0).getChildren().get(0)
        );

    }
}
