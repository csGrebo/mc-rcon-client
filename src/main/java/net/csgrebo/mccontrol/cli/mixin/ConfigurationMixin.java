package net.csgrebo.mccontrol.cli.mixin;

import net.csgrebo.mccontrol.cli.command.BaseCommand;
import net.csgrebo.mccontrol.exception.MissingDomainException;
import net.csgrebo.mccontrol.exception.MissingDomainException.Reason;
import net.csgrebo.mccontrol.util.ConfigurationUtils;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Spec.Target;

import java.io.IOException;
import java.util.List;

public class ConfigurationMixin {

    @Spec(Target.MIXEE)
    CommandSpec mixee;

    private String serverDomain;

    @Option(names = {"-d", "--domain"}, description = {"Server domain name"})
    public void setServerDomain(String domain) {
        getTopLevelConfigurationMixin(mixee).serverDomain = domain;
    }

    public String getServerDomain() {
        return getTopLevelConfigurationMixin(mixee).serverDomain;
    }

    private static ConfigurationMixin getTopLevelConfigurationMixin(CommandSpec spec) {
        return ((BaseCommand) spec.root().userObject()).getConfigurationMixin();
    }
}
