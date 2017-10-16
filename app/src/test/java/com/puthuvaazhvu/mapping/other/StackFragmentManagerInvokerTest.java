package com.puthuvaazhvu.mapping.other;

import android.support.v4.app.Fragment;

import com.puthuvaazhvu.mapping.views.managers.StackFragmentManagerInvoker;
import com.puthuvaazhvu.mapping.views.managers.commands.FragmentPushCommand;
import com.puthuvaazhvu.mapping.views.managers.commands.IManagerCommand;
import com.puthuvaazhvu.mapping.views.managers.receiver.IStackFragmentManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by muthuveerappans on 10/16/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class StackFragmentManagerInvokerTest {

    private StackFragmentManagerInvoker stackFragmentManagerInvoker;

    @Before
    public void setUp() {
        stackFragmentManagerInvoker = new StackFragmentManagerInvoker();
    }

    @Test
    public void testCommandExecution() {
        Fragment mockFragment = mock(Fragment.class);
        IStackFragmentManager iStackFragmentManagerMock = mock(IStackFragmentManager.class);

        // mock the add fragment behaviour
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        }).when(iStackFragmentManagerMock).pushFragment(anyString(), any(Fragment.class));

        // mock the pop fragment behaviour
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        }).when(iStackFragmentManagerMock).popFragment(any(Fragment.class));

        FragmentPushCommand fragmentPushCommand = new FragmentPushCommand(
                iStackFragmentManagerMock,
                "test",
                mockFragment);

        stackFragmentManagerInvoker.addCommand(fragmentPushCommand);

        assertThat(stackFragmentManagerInvoker.getPendingCommands().size(), is(1));

        stackFragmentManagerInvoker.executeCommand();

        assertThat(stackFragmentManagerInvoker.getPendingCommands().size(), is(0));
        assertThat(stackFragmentManagerInvoker.getExecutedCommands().size(), is(1));

        stackFragmentManagerInvoker.unExecuteCommand();

        // undo the execution
        assertThat(stackFragmentManagerInvoker.getPendingCommands().size(), is(0));
        assertThat(stackFragmentManagerInvoker.getExecutedCommands().size(), is(0));

    }

}
