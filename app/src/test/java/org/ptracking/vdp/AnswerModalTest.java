package org.ptracking.vdp;

import org.ptracking.vdp.Helpers.DataTraversing;
import org.ptracking.vdp.Helpers.ReadTestFile;
import org.ptracking.vdp.modals.Answer;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.modals.Survey;

import org.junit.Before;
import org.junit.Test;

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
