package net.csgrebo.mccontrol.cli.support;

import net.csgrebo.mccontrol.cli.mixin.ConfigurationMixin;
import net.csgrebo.mccontrol.cli.mixin.LoggingMixin;
import picocli.CommandLine;
import picocli.CommandLine.ExecutionException;
import picocli.CommandLine.IExecutionStrategy;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

import java.io.IOException;

public class ApplicationExecutionStrategy implements IExecutionStrategy {

    @Override
    public int execute(ParseResult parseResult) throws ExecutionException, ParameterException {
        LoggingMixin.configure(parseResult);
        return new CommandLine.RunLast().execute(parseResult);
    }
}
