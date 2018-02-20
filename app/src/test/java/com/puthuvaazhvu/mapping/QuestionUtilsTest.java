package com.puthuvaazhvu.mapping;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.Helpers.DataTraversing;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.FlowPattern;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

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

    @Before
    public void setUp() throws Exception {
        dummySurvey = createDummySurvey();

        assertThat(
                "Check structure of the dummy survey",
                dummySurvey
                        .getQuestion().getAnswers()
                        .get(0).getChildren()
                        .get(0).getAnswers()
                        .get(0).getChildren()
                        .size(),
                is(3)
        );
    }

    @Test
    public void getIndexOfChild() throws Exception {
        Question root = dummySurvey.getQuestion();

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

        Question node = root.getAnswers().get(0).getChildren().get(1).getAnswers().get(0).getChildren().get(2);

        assertThat(
                "Check node number",
                node.getNumber(),
                is("1.2.3")
        );

        path = QuestionUtils.getPathOfQuestion(node);

        assertThat(
                "Check path of dummy root",
                path.toString(),
                is("[0, 0, 1, 0, 2]")
        );
    }

    @Test
    public void moveToQuestionUsingPath() throws Exception {
        Question root = dummySurvey.getQuestion();

        Question currentNode = QuestionUtils.moveToQuestionUsingPath("0,0,0,0,2", root);

        assertThat(
                "Check number of current question",
                currentNode.getNumber(),
                is("1.1.3")
        );

        assertThat(
                "Check parent of current question",
                currentNode.getParentAnswer().getParentQuestion().getNumber(),
                is("1.1")
        );
    }

    @Test
    public void findQuestion() throws Exception {
        Question root = dummySurvey.getQuestion();

        Question node = root.getAnswers().get(0).getChildren().get(1).getAnswers().get(0).getChildren().get(2);

        assertThat(
                "Check node number",
                node.getNumber(),
                is("1.2.3")
        );

        Question foundQ = QuestionUtils.findQuestionFrom(node, "1.1.3", true);

        assertThat(
                "Check found question number",
                foundQ.getNumber(),
                is("1.1.3")
        );

        assertThat(
                "Check found question parent",
                foundQ.getParentAnswer().getParentQuestion().getNumber(),
                is("1.1")
        );
    }

    @Test
    public void isGridSelectQuestion() throws Exception {
        Question root = dummySurvey.getQuestion();
        assertThat(
                "Check if 1.2 is grid question",
                QuestionUtils.isGridSelectQuestion(DataTraversing.findQuestion("1.2", root)),
                is(true)
        );
    }

    @Test
    public void isLoopOptionsQuestion() throws Exception {
        Question root = dummySurvey.getQuestion();
        assertThat(
                "Check if 1.1 is loop options question",
                QuestionUtils.isLoopOptionsQuestion(DataTraversing.findQuestion("1.1", root)),
                is(true)
        );
    }

    private Survey createDummySurvey() {

        Question c1_1_1 = createDummyQuestion("1.1.1", null);
        Question c1_1_2 = createDummyQuestion("1.1.2", null);
        Question c1_1_3 = createDummyQuestion("1.1.3", null);

        Question c1_2_1 = createDummyQuestion("1.2.1", null);
        Question c1_2_2 = createDummyQuestion("1.2.2", null);
        Question c1_2_3 = createDummyQuestion("1.2.3", null);

        Question c1_1 = createDummyQuestion("1.1", new ArrayList<Question>(Arrays.asList(c1_1_1, c1_1_2, c1_1_3)));
        setLoopOptionsType(c1_1);

        Question c1_2 = createDummyQuestion("1.2", new ArrayList<Question>(Arrays.asList(c1_2_1, c1_2_2, c1_2_3)));
        setGridSelectType(c1_2);

        Question root = createDummyQuestion("1", new ArrayList<Question>(Arrays.asList(c1_1, c1_2)));

        root.addAnswer(createDummyAnswer(root));

        ArrayList<Question> a_children_root = root.getCurrentAnswer().getChildren();

        a_children_root.get(0).addAnswer(createDummyAnswer(a_children_root.get(0)));
        a_children_root.get(1).addAnswer(createDummyAnswer(a_children_root.get(1)));

        ArrayList<Question> a_children_1_1 = root.getCurrentAnswer().getChildren().get(0).getCurrentAnswer().getChildren();
        ArrayList<Question> a_children_1_2 = root.getCurrentAnswer().getChildren().get(1).getCurrentAnswer().getChildren();

        a_children_1_1.get(0).addAnswer(createDummyAnswer(a_children_1_1.get(0)));
        a_children_1_1.get(1).addAnswer(createDummyAnswer(a_children_1_1.get(1)));
        a_children_1_1.get(2).addAnswer(createDummyAnswer(a_children_1_1.get(2)));
        a_children_1_2.get(0).addAnswer(createDummyAnswer(a_children_1_2.get(0)));
        a_children_1_2.get(1).addAnswer(createDummyAnswer(a_children_1_2.get(1)));
        a_children_1_2.get(2).addAnswer(createDummyAnswer(a_children_1_2.get(2)));

        return new Survey(null, null, null, root, null, true);
    }

    private void setLoopOptionsType(Question question) {
        FlowPattern flowPattern = new FlowPattern();

        FlowPattern.ExitFlow exitFlow = new FlowPattern.ExitFlow();
        exitFlow.setStrategy(FlowPattern.ExitFlow.Strategy.LOOP);

        flowPattern.setExitFlow(exitFlow);

        FlowPattern.AnswerFlow answerFlow = new FlowPattern.AnswerFlow();
        answerFlow.setMode(FlowPattern.AnswerFlow.Modes.OPTION);

        flowPattern.setAnswerFlow(answerFlow);

        question.setFlowPattern(flowPattern);
    }

    private void setGridSelectType(Question question) {
        FlowPattern flowPattern = new FlowPattern();

        FlowPattern.ChildFlow childFlow = new FlowPattern.ChildFlow();
        childFlow.setStrategy(FlowPattern.ChildFlow.Strategy.SELECT);
        childFlow.setUiToBeShown(FlowPattern.ChildFlow.UI.GRID);
        childFlow.setRepeatMode(FlowPattern.ChildFlow.RepeatMode.MULTIPLE);

        flowPattern.setChildFlow(childFlow);

        question.setFlowPattern(flowPattern);
    }

    private void setTogetherType(Question question) {
        FlowPattern flowPattern = new FlowPattern();

        FlowPattern.ChildFlow childFlow = new FlowPattern.ChildFlow();
        childFlow.setStrategy(FlowPattern.ChildFlow.Strategy.TOGETHER);

        flowPattern.setChildFlow(childFlow);

        question.setFlowPattern(flowPattern);
    }

    private Answer createDummyAnswer(Question parent) {
        return Answer.createDummyAnswer(parent);
    }

    private Question createDummyQuestion(String number, ArrayList<Question> children) {
        return new Question(
                null,
                null,
                null,
                null,
                null,
                number,
                children,
                null
        );
    }
}
