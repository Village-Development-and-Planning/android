package com.puthuvaazhvu.mapping.views.managers;

import android.support.annotation.VisibleForTesting;

import com.puthuvaazhvu.mapping.views.managers.commands.IManagerCommand;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/16/17.
 */

public class StackFragmentManagerInvoker {
    private ArrayList<IManagerCommand> pendingCommands;
    private ArrayList<IManagerCommand> executedCommands;

    public StackFragmentManagerInvoker() {
        pendingCommands = new ArrayList<>();
        executedCommands = new ArrayList<>();
    }

    public void addCommand(IManagerCommand iManagerCommand) {
        pendingCommands.add(iManagerCommand);
    }

    public void executeCommand() {
        for (int i = 0; i < pendingCommands.size(); i++) {
            IManagerCommand command = pendingCommands.get(i);
            command.execute();
            executedCommands.add(command);
        }
        pendingCommands.clear();
    }

    public void unExecuteCommand() {
        if (!executedCommands.isEmpty()) {
            IManagerCommand command = executedCommands.remove(executedCommands.size() - 1);
            command.unExecute();
        }
    }

    @VisibleForTesting
    public ArrayList<IManagerCommand> getPendingCommands() {
        return pendingCommands;
    }

    @VisibleForTesting
    public ArrayList<IManagerCommand> getExecutedCommands() {
        return executedCommands;
    }
}
