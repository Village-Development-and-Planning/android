package com.puthuvaazhvu.mapping.views.managers.commands;

/**
 * Created by muthuveerappans on 10/16/17.
 */

public interface IManagerCommand {

    /**
     * Executes some command
     */
    void execute();

    /**
     * Un-do's the previousely executed command by the execute() function
     */
    void unExecute();
}
