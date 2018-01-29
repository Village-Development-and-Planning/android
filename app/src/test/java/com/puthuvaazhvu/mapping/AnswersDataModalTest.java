package com.puthuvaazhvu.mapping;

import com.puthuvaazhvu.mapping.helpers.DataHelpers;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswerDataModal;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswersInfoFileDataModal;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by muthuveerappans on 11/23/17.
 */

public class AnswersDataModalTest {

    private AnswersInfoFileDataModal answersInfoFileDataModal;

    @Before
    public void init() {
        answersInfoFileDataModal = DataHelpers.getAnswersInfoFileModal(this);

        assertThat(answersInfoFileDataModal.getVersion(), is(1));
        assertThat(answersInfoFileDataModal.getAnswerDataModals().size(), is(2));
    }

    @Test
    public void test_getLatestSnapShot() {
        AnswerDataModal answerDataModal = answersInfoFileDataModal.getAnswerDataModals().get(0);

        assertThat(answerDataModal.getId(), is("1"));

        AnswerDataModal.Snapshot snapshot = answerDataModal.getLatestSnapShot();

        assertThat(snapshot.getSnapshotId(), is("b"));
    }
}
