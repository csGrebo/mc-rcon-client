package net.csgrebo.mccontrol.cli.command;

import net.csgrebo.mccontrol.cli.mixin.ConfigurationMixin;
import net.csgrebo.mccontrol.cli.mixin.LoggingMixin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

@Command(name = "test")
public class TestCommand implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(TestCommand.class);

    @Mixin
    private LoggingMixin loggingMixin;

    @Mixin
    private ConfigurationMixin configurationMixin;

    @Override
    public void run() {
        log.error("Startup complete (E)");
        log.error("Startup complete (W)");
        log.info("Startup complete (I)");
        log.debug("Startup complete (D)");
        log.trace("Startup complete (T)");
    }
}
