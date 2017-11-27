package com.puthuvaazhvu.mapping.activities;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Question;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertSame;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by muthuveerappans on 11/27/17.
 */

@RunWith(AndroidJUnit4.class)
public class ParcelableTest {

    @Test
    public void questionParcelableTest() {
        ArrayList<Answer> answers = new ArrayList<>();
        Question question = new Question("1", "", null, null, null, answers, null, null, null, null, null, null, null);
        answers.add(new Answer(null, question));

        Parcel parcel = Parcel.obtain();
        question.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        Question questionFromParcel = Question.CREATOR.createFromParcel(parcel);

        assertThat(questionFromParcel.getAnswers().get(0).getAsJson(), is(answers.get(0).getAsJson()));
        assertSame(answers.get(0).getQuestionReference(), question);
    }
}
