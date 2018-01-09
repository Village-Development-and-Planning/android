package com.puthuvaazhvu.mapping.modals;

import com.puthuvaazhvu.mapping.helpers.ModalHelpers;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswerDataModal;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswersInfoFileDataModal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by muthuveerappans on 11/20/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class AnswersInfoFileDataModalTest {

    private AnswersInfoFileDataModal answersInfoFileDataModal;

    @Before
    public void init() {
        answersInfoFileDataModal = ModalHelpers.getAnswersInfoFileModal(this);

        assertThat(answersInfoFileDataModal.getVersion(), is(1));
        assertThat(answersInfoFileDataModal.getAnswerDataModals().size(), is(2));
    }

    @Test
    public void test_find() {
        AnswerDataModal answerDataModal = answersInfoFileDataModal.find("1");

        assertThat(answerDataModal.getId(), is("1"));
        assertThat(answerDataModal.getSnapshots().size(), is(2));
    }

    @Test
    public void test_updateWithNew() {
        AnswerDataModal answerDataModal = answersInfoFileDataModal.getAnswerDataModals().get(0);
        assertThat(answerDataModal.getSnapshots().size(), is(2));
        assertThat(answerDataModal.getId(), is("1"));

        // test add to existing
        ArrayList<AnswerDataModal> answerDataModals = new ArrayList<>();
        ArrayList<AnswerDataModal.Snapshot> snapShots = new ArrayList<>();
        snapShots.add(
                new AnswerDataModal.Snapshot("c", "test_1", "", false, "")
        );
        answerDataModals.add(
                new AnswerDataModal("1", false, snapShots)
        );
        answersInfoFileDataModal.updateWithNew(answerDataModals);

        assertThat(answerDataModal.getSnapshots().size(), is(3));
        assertThat(answersInfoFileDataModal.getAnswerDataModals().size(), is(2));

        // test add new
        answerDataModals.clear();
        snapShots.clear();
        snapShots.add(
                new AnswerDataModal.Snapshot("b", "test_2", "", false, "")
        );
        answerDataModals.add(
                new AnswerDataModal("3", false, snapShots)
        );
        answersInfoFileDataModal.updateWithNew(answerDataModals);

        assertThat(answersInfoFileDataModal.getAnswerDataModals().size(), is(3));
    }
}
