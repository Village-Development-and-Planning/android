package org.ptracking.vdp;

import org.ptracking.vdp.Helpers.DataTraversing;
import org.ptracking.vdp.Helpers.ReadTestFile;
import org.ptracking.vdp.modals.Answer;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.modals.Survey;
import org.ptracking.vdp.modals.utils.QuestionUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;

/**
 * Created by muthuveerappans on 10/19/17.
 */


public class QuestionUtilsTest {
    private Survey dummySurvey;
    Question root;

    @Before
    public void setUp() throws Exception {
        dummySurvey = ReadTestFile.getTestSurvey(this, "question_utils_testing.json");
        root = dummySurvey.getQuestion();

        addAnswersToTreeFromQuestion(root);

        assertThat(
                "Check ID of the survey",
                dummySurvey
                        .getId(),
                is("5a7886d9fdb3a526f48afe25")
        );
    }

    @Test
    public void getIndexOfChild() throws Exception {
        assertThat(
                "Check if the index of 1.2 is 1",
                QuestionUtils.getIndexOfChild(root, DataTraversing.findQuestion("1.2", root)),
                is(1)
        );
    }

    @Test
    public void snapshotPathToQuestion() throws Exception {
        ArrayList<Integer> path;

        Question root = dummySurvey.getQuestion();

        Question node = root.getAnswers().get(0)
                .getChildren().get(4)
                .getAnswers().get(0)
                .getChildren().get(1)
                .getAnswers().get(0)
                .getChildren().get(2);

        assertThat(
                "Check node number",
                node.getNumber(),
                is("2.1.3")
        );

        path = QuestionUtils.getPathOfQuestion(node);

        assertThat(
                "Check path of dummy root",
                path.toString(),
                is("[0, 0, 4, 0, 1, 0, 2]")
        );
    }

    @Test
    public void moveToQuestionUsingPath() throws Exception {
        Question root = dummySurvey.getQuestion();

        Question currentNode = QuestionUtils.moveToQuestionUsingPath("0,0,4,0,1,0,2", root);

        assertThat(
                "Check number of current question",
                currentNode.getNumber(),
                is("2.1.3")
        );
    }

    @Test
    public void findQuestion() throws Exception {
        Question node = root.getAnswers().get(0)
                .getChildren().get(4)
                .getAnswers().get(0)
                .getChildren().get(1)
                .getAnswers().get(0)
                .getChildren().get(2);

        assertThat(
                "Check node number",
                node.getNumber(),
                is("2.1.3")
        );

        Question foundQ = QuestionUtils.findQuestionFrom(node, "2.7.1.11", false);

        assertThat(
                "Check found question number",
                foundQ.getNumber(),
                is("2.7.1.11")
        );
    }

    @Test
    public void isGridSelectQuestion() throws Exception {
        assertThat(
                "Check if 2.1.6 is grid question",
                QuestionUtils.isGridSelectQuestion(DataTraversing.findQuestion("2.1.6", root)),
                is(true)
        );
    }

    @Test
    public void isLoopOptionsQuestion() throws Exception {
        assertThat(
                "Check if 2 is loop options question",
                QuestionUtils.isLoopOptionsQuestion(DataTraversing.findQuestion("2", root)),
                is(true)
        );
    }

    public static void addAnswer(Question question) {
        Answer answer2 = Answer.createDummyAnswer(question);
        answer2.setDummy(false);
        question.addAnswer(answer2);
    }

    public static void addAnswersToTreeFromQuestion(Question node) {
        addAnswer(node);
        node.getCurrentAnswer().setExitTimestamp(System.currentTimeMillis());
        for (Question c : node.getCurrentAnswer().getChildren()) {
            addAnswersToTreeFromQuestion(c);
        }
    }
}
