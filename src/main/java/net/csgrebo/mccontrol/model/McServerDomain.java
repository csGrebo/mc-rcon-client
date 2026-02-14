package net.csgrebo.mccontrol.model;

import java.nio.file.Path;

public record McServerDomain(String domainName, Path domainPath, Integer rconPort, String rconPassword,
                             String domainExecutable, String worldFolderName, String discordToken, String discordChannel) {
}
