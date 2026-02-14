package net.csgrebo.mccontrol.cli.command;

import net.csgrebo.mccontrol.cli.mixin.ConfigurationMixin;
import net.csgrebo.mccontrol.cli.mixin.LoggingMixin;
import net.csgrebo.mccontrol.exception.ServerAuthenticationException;
import net.csgrebo.mccontrol.model.McServerDomain;
import net.csgrebo.mccontrol.util.ConfigurationUtils;
import nl.vv32.rcon.Rcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Callable;

@Command(name = "stop", description = "Stop provided Minecraft server")
public class StopServerCommand implements Callable<Integer> {
    private static final Logger log = LoggerFactory.getLogger(StopServerCommand.class);

    @Mixin
    private LoggingMixin loggingMixin;

    @Mixin
    private ConfigurationMixin configurationMixin;

    @Override
    public Integer call() throws Exception {
        McServerDomain domain = ConfigurationUtils.loadDomain(configurationMixin.getServerDomain());
        String command = "/stop";
        log.info("Stopping server: {}", domain.domainName());
        try (Rcon rcon = Rcon.newBuilder()
                .withChannel(SocketChannel.open(new InetSocketAddress("localhost", domain.rconPort())))
                .withCharset(StandardCharsets.UTF_8)
                .build()) {
            if (rcon.authenticate(new String(Base64.getDecoder().decode(domain.rconPassword())))) {
                String output = rcon.sendCommand(command);
                log.info("RCON Output: {}", output);
            } else {
                throw new ServerAuthenticationException("Authentication failed");
            }
        } catch (IOException e) {
            log.error("Error executing command: {}", e.getLocalizedMessage(), e);
            throw e;
        }
        return 0;
    }
}
