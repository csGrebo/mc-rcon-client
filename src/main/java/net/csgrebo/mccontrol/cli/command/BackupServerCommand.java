package net.csgrebo.mccontrol.cli.command;

import io.github.compress4j.archivers.tar.TarGzArchiveCreator;
import net.csgrebo.mccontrol.cli.mixin.ConfigurationMixin;
import net.csgrebo.mccontrol.cli.mixin.LoggingMixin;
import net.csgrebo.mccontrol.model.McServerDomain;
import net.csgrebo.mccontrol.util.ConfigurationUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import nl.vv32.rcon.Rcon;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Command(name = "backup", description = "Perform a world backup")
public class BackupServerCommand implements Callable<Integer> {
    private static final Logger log = LoggerFactory.getLogger(BackupServerCommand.class);

    @Mixin
    private LoggingMixin loggingMixin;

    @Mixin
    private ConfigurationMixin configurationMixin;

    @Override
    public Integer call() throws Exception {
        McServerDomain domain = ConfigurationUtils.loadDomain(configurationMixin.getServerDomain());
        log.info("Performing a world backup for {}", domain.domainName());
        boolean discordEnabled = StringUtils.isNoneBlank(domain.discordToken(), domain.discordChannel());
        JDA discordClient;
        TextChannel textChannel = null;
        if (discordEnabled) {
            discordClient = JDABuilder.createDefault(domain.discordToken()).build().awaitReady();
            textChannel = discordClient.getTextChannelById(domain.discordChannel());
            if (textChannel != null && textChannel.canTalk()) {
                textChannel.sendMessage("Server maintenance for %s is commencing".formatted(configurationMixin.getServerDomain())).queue();
            } else {
                log.warn("Messaging is disabled");
                discordEnabled = false;
            }
        }
        try (Rcon rcon = Rcon.newBuilder()
                .withChannel(SocketChannel.open(new InetSocketAddress("localhost", domain.rconPort())))
                .withCharset(StandardCharsets.UTF_8)
                .build()) {
            rcon.tryAuthenticate(new String(Base64.getDecoder().decode(domain.rconPassword())));
            rcon.sendCommand("/say §5§nSystem Maintenance§r§f Commencing Maintenance - The server will halt in 5 minutes");
            try {
                TimeUnit.MINUTES.sleep(5L);
            } catch (InterruptedException e) {
                log.error("An interruption occurred!!!");
                Thread.currentThread().interrupt();
            }
            rcon.sendCommand("/say §5§nSystem Maintenance§r§f Commencing Maintenance - The server IS HALTING");
            rcon.sendCommand("/stop");
            if (Files.exists(domain.domainPath().resolve(domain.worldFolderName()))) {
                Path bkp = Path.of(System.getProperty("user.home"), "backup", "mc-world-%s.%s.tar.gz".formatted(configurationMixin.getServerDomain(), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
                if (Files.notExists(bkp.getParent())) {
                    Files.createDirectories(bkp.getParent());
                }
                Path tmpdir = Files.createTempDirectory("mc-%s".formatted(configurationMixin.getServerDomain()));
                tmpdir.toFile().deleteOnExit();
                PathUtils.copyDirectory(domain.domainPath().resolve(domain.worldFolderName()), tmpdir);
                try (TarGzArchiveCreator tarGzArchiveCreator = TarGzArchiveCreator.builder(bkp).build()) {
                    tarGzArchiveCreator.addDirectoryRecursively(tmpdir);
                }
            }
            int startCode = new ProcessExecutor().directory(domain.domainPath().toFile())
                    .commandSplit(domain.domainExecutable())
                    .redirectOutput(Slf4jStream.of(log).asInfo())
                    .redirectErrorStream(true)
                    .executeNoTimeout()
                    .getExitValue();
            if (startCode == 0) {
                boolean started = false;
                do {
                    try {
                        rcon.sendCommand("/random value 0..10");
                        started = true;
                    } catch (IOException e) {
                        log.warn("Server not started yet");
                    }
                } while (!started);
            } else {
                log.error("Unable to trigger server start. You should investigate");
                if (discordEnabled) {
                    textChannel.sendMessage("An error has occurred in restarting the server - %s".formatted(configurationMixin.getServerDomain())).queue();
                }
                return 1;
            }
        }
        if (discordEnabled) {
            textChannel.sendMessage("Server maintenance is complete").queue();
        }
        return 0;
    }
}
