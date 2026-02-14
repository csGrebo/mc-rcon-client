package net.csgrebo.mccontrol.cli.support;

import net.csgrebo.mccontrol.exception.ImproperCommandException;
import net.csgrebo.mccontrol.exception.MissingDomainException;
import net.csgrebo.mccontrol.exception.MissingConfigurationException;
import net.csgrebo.mccontrol.exception.ServerAuthenticationException;
import net.csgrebo.mccontrol.util.ConfigurationUtils;
import picocli.CommandLine;
import picocli.CommandLine.IExitCodeExceptionMapper;

import java.io.IOException;
import java.util.List;

public class ApplicationExitCodeExceptionMapper implements IExitCodeExceptionMapper {

    private final CommandLine commandLine;

    public ApplicationExitCodeExceptionMapper(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    public int getExitCode(Throwable throwable) {
        return switch (throwable) {
            case MissingDomainException e -> {
                // TODO: Print Message about available domains
                switch (e.getReason()) {
                    case EMPTY_ARG -> {
                        try {
                            List<String> domains = ConfigurationUtils.loadDomainList(false);
                            if (domains.isEmpty()) {
                                commandLine.getOut().println("No Minecraft server domains are configured on this server!");
                            }
                            for (String domain : domains) {
                                commandLine.getOut().println("A server domain ID is required");
                                commandLine.getOut().println("Configured domains on this server are:");
                                commandLine.getOut().printf("  - %s%n", domain);
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    case NO_LIST_FOUND -> {
                        commandLine.getOut().println("No Minecraft server domain list file detected");
                    }
                    case MISSING_FILE -> {
                        String domain = e.getDomainName();
                        commandLine.getOut().printf("Minecraft server domain file for %s is missing.%n", domain);
                    }
                    case INVALID_CONFIGURATION -> commandLine.getOut().println(e.getMessage());
                }
                yield 25;
            }
            case MissingConfigurationException m -> 26;
            case ServerAuthenticationException s -> 27;
            case IOException i -> 28;
            case ImproperCommandException i -> 29;
            default -> 1;
        };
    }
}
