package edu.sjsu.amigo.cp.tasks.api;

/**
 * This is the interface for a command executor.
 *
 * @author rwatsh on 2/16/17.
 */
public interface CommandExecutor {
    Response executeCommand(Command cmd) throws CommandExecutionException;
}
