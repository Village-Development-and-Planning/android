package com.puthuvaazhvu.mapping;

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
    public void test() throws Exception {

        Question root = dummySurvey.getQuestion();

        assertNotSame(
                "Check children object are not same for answer",
                root.getChildren().get(0),
                root.getCurrentAnswer().getChildren().get(0)
        );

        assertSame(
                "Check parent answer has same reference",
                root.getCurrentAnswer().getChildren().get(0),
                root.getCurrentAnswer().getChildren().get(0)
                        .getCurrentAnswer().getChildren().get(0).getParentAnswer().getParentQuestion()
        );


        assertSame(
                "Check same parent reference",
                root.getCurrentAnswer().getChildren().get(0).getParent(),
                root
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

        Question c1_2 = createDummyQuestion("1.2", new ArrayList<Question>(Arrays.asList(c1_2_1, c1_2_2, c1_2_3)));

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
