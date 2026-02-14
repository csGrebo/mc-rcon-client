package net.csgrebo.mccontrol.cli.command;

import net.csgrebo.mccontrol.cli.mixin.ConfigurationMixin;
import net.csgrebo.mccontrol.cli.mixin.LoggingMixin;
import net.csgrebo.mccontrol.model.McServerDomain;
import net.csgrebo.mccontrol.util.ConfigurationUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Command(name = "discord-message", aliases = {"discord"}, description = "Send a message to the Discord channel configured in the domain")
public class DiscordMessageCommand implements Callable<Integer> {
    private static final Logger log = LoggerFactory.getLogger(DiscordMessageCommand.class);

    @Mixin
    private LoggingMixin loggingMixin;

    @Mixin
    private ConfigurationMixin configurationMixin;

    @Option(names = {"--message"}, description = "Message content", required = true)
    private String message;

    @Override
    public Integer call() throws Exception {
        log.info("Attempting to send message to discord");
        McServerDomain domain = ConfigurationUtils.loadDomain(configurationMixin.getServerDomain());
        if (StringUtils.isAnyBlank(domain.discordToken(), domain.discordChannel())) {
            throw new IllegalArgumentException("Discord information is not configured");
        }
        JDA jda = JDABuilder.createDefault(domain.discordToken()).build();
        jda.awaitReady();
        TextChannel textChannel = jda.getTextChannelById(domain.discordChannel());
        if (textChannel != null && textChannel.canTalk()) {
            textChannel.sendMessage(message).queue();
        } else {
            log.warn("Unable to send messages at this time");
        }
        jda.shutdown();
        if (!jda.awaitShutdown(10, TimeUnit.SECONDS)) {
            jda.shutdownNow();
            jda.awaitShutdown();
        }
        return 0;
    }
}
