package net.csgrebo.mccontrol.cli.command;

import net.csgrebo.mccontrol.cli.mixin.ConfigurationMixin;
import net.csgrebo.mccontrol.cli.mixin.LoggingMixin;
import net.csgrebo.mccontrol.model.McServerDomain;
import net.csgrebo.mccontrol.util.ConfigurationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

import java.util.concurrent.Callable;

@Command(name = "start", description = "Start provided Minecraft server")
public class StartServerCommand implements Callable<Integer> {
    private static final Logger log = LoggerFactory.getLogger(StartServerCommand.class);

    @Mixin
    private LoggingMixin loggingMixin;

    @Mixin
    private ConfigurationMixin configurationMixin;

    @Override
    public Integer call() throws Exception {
        McServerDomain domain = ConfigurationUtils.loadDomain(configurationMixin.getServerDomain());
        return new ProcessExecutor().directory(domain.domainPath().toFile())
                .commandSplit(domain.domainExecutable())
                .redirectOutput(Slf4jStream.of(log).asInfo())
                .redirectErrorStream(true)
                .executeNoTimeout()
                .getExitValue();
    }
}
