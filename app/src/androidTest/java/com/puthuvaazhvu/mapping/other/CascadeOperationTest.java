package com.puthuvaazhvu.mapping.other;

import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import com.puthuvaazhvu.mapping.views.activities.MainActivity;
import com.puthuvaazhvu.mapping.views.managers.StackFragmentManager;
import com.puthuvaazhvu.mapping.views.managers.StackFragmentManagerImpl;
import com.puthuvaazhvu.mapping.views.managers.operation.CascadeOperation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by muthuveerappans on 10/9/17.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class CascadeOperationTest {

    private CascadeOperation cascadeOperation;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() {
        StackFragmentManager stackFragmentManager = new StackFragmentManagerImpl(mainActivityActivityTestRule.getActivity()
                .getSupportFragmentManager());
        cascadeOperation = new CascadeOperation(stackFragmentManager);
    }

    @Test
    public void testOperation() {
        for (int i = 0; i < 100; i++) {
            Fragment fragment = new Fragment();
            cascadeOperation.pushOperation("" + i, fragment);
        }

        assertThat(cascadeOperation.getCount(), is(100));
        assertThat(cascadeOperation.getHeadFragment().getTag(), is("99"));
        assertThat(cascadeOperation.getTailFragment().getTag(), is("0"));

        getInstrumentation().waitForIdleSync();

        cascadeOperation.popOperation("99");

        getInstrumentation().waitForIdleSync();

        assertThat(cascadeOperation.getHeadFragment().getTag(), is("98"));
        assertThat(cascadeOperation.getCount(), is(99));

        cascadeOperation.popManyOperation(new String[]{"98", "97", "96", "95"});

        getInstrumentation().waitForIdleSync();

        assertThat(cascadeOperation.getHeadFragment().getTag(), is("94"));
        assertThat(cascadeOperation.getCount(), is(95));

        cascadeOperation.popOperation("90");

        getInstrumentation().waitForIdleSync();

        assertThat(cascadeOperation.getHeadFragment().getTag(), is("94"));
        assertThat(cascadeOperation.getCount(), is(94));
    }
}
