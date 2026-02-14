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
import picocli.CommandLine.Option;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Callable;

@Command(name = "message", description = "Send a message on the server")
public class MessageServerCommand implements Callable<Integer> {
    private static final Logger log = LoggerFactory.getLogger(MessageServerCommand.class);

    @Mixin
    private LoggingMixin loggingMixin;

    @Mixin
    private ConfigurationMixin configurationMixin;

    @Option(names = {"--message"}, description = "Message content", required = true)
    private String message;

    @Override
    public Integer call() throws Exception {
        McServerDomain domain = ConfigurationUtils.loadDomain(configurationMixin.getServerDomain());
        log.info("Sending message: {}", message);
        String command = "/say %s".formatted(message);
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
