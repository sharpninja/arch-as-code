package net.trilogy.arch.commands.mixin;

import picocli.CommandLine;

public interface DisplaysErrorMixin {

    CommandLine.Model.CommandSpec getSpec();

    default void printError(String errorMessage) {
        getSpec().commandLine().getErr().println(errorMessage);
    }

    default void printError(String errorMessage, Exception exception) {
        printError(errorMessage);
        printError("Error thrown: " + exception);
        if(exception.getCause() != null) {
            printError("Cause: " + exception.getCause());
        }
    }
}
