package net.csgrebo.mccontrol.util;

import net.csgrebo.mccontrol.exception.MissingDomainException;
import net.csgrebo.mccontrol.exception.MissingDomainException.Reason;
import net.csgrebo.mccontrol.model.McServerDomain;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

public abstract class ConfigurationUtils {
    private static final Logger log = LoggerFactory.getLogger(ConfigurationUtils.class);

    private ConfigurationUtils() {
        throw new IllegalStateException("Utility Class");
    }

    public static List<String> loadDomainList(boolean strict) throws IOException {
        String[] searchPaths = {
                "/apps/etc/SSI",
                "/etc/SSI",
                "${sys:user.home}/.ssi",
        };
        Iterator<String> pathIterator = Arrays.asList(searchPaths).iterator();
        StringSubstitutor substitutor = StringSubstitutor.createInterpolator();
        Path domainListFile = null;
        while (domainListFile == null && pathIterator.hasNext()) {
            String searchPath = pathIterator.next();
            Path searchDir = Path.of(substitutor.replace(searchPath));
            if (Files.exists(searchDir) && Files.isDirectory(searchDir)) {
                try (Stream<Path> fileList = Files.list(searchDir)) {
                    domainListFile = fileList.filter(Files::isRegularFile).filter(f -> f.getFileName().toString().equals("mc-servers.list")).findFirst().orElse(null);
                }
            }
        }
        if (domainListFile == null) {
            if (strict) throw new MissingDomainException(Reason.NO_LIST_FOUND);
            else return Collections.emptyList();
        }
        try (Stream<String> lines = Files.lines(domainListFile)) {
            return lines.filter(StringUtils::isNotBlank).toList();
        }
    }

    public static Path loadDomainConfiguration(String domain) throws IOException {
        String[] searchPaths = {
                "/apps/etc/SSI",
                "/etc/SSI",
                "${sys:user.home}/.ssi",
        };
        if (StringUtils.isBlank(domain)) {
            throw new MissingDomainException(Reason.EMPTY_ARG);
        }
        String ssiFileName = "host.%s.mcserver.%s".formatted(PosixUtils.getHostname(), domain);
        Iterator<String> pathIterator = Arrays.asList(searchPaths).iterator();
        StringSubstitutor substitutor = StringSubstitutor.createInterpolator();
        Path ssiFile = null;
        while (ssiFile == null && pathIterator.hasNext()) {
            String searchPath = pathIterator.next();
            Path searchDir = Path.of(substitutor.replace(searchPath));
            if (Files.exists(searchDir) && Files.isDirectory(searchDir)) {
                try (Stream<Path> fileList = Files.list(searchDir)) {
                    ssiFile = fileList.filter(Files::isRegularFile).filter(f -> f.getFileName().toString().equals(ssiFileName)).findFirst().orElse(null);
                }
            }
        }
        if (ssiFile == null) {
            throw new MissingDomainException(Reason.MISSING_FILE, domain);
        }
        return ssiFile;
    }

    public static McServerDomain loadDomain(String domain) throws IOException {
        log.info("Loading server configuration");
        Path domainFile = loadDomainConfiguration(domain);
        Properties properties = new Properties();
        try (InputStream is = Files.newInputStream(domainFile)) {
            properties.load(is);
        }
        String domainName = properties.getProperty("SERVER_NAME");
        log.debug("Server Domain: {}", domainName);
        String domainPath = properties.getProperty("SERVER_HOME");
        log.debug("Server Home: {}", domainPath);
        String domainPort = properties.getProperty("SERVER_PORT");
        log.debug("Server Port: {}", domainPort);
        String domainPass = properties.getProperty("SERVER_PASS");
        log.debug("Server Pass: {}", domainPass);
        String domainExec = properties.getProperty("SERVER_EXEC");
        log.debug("Server Exec: {}", domainExec);
        String domainWorld = properties.getProperty("SERVER_WORLD_FILE");
        log.debug("Server World: {}", domainWorld);
        String discordToken = properties.getProperty("DISCORD_TOKEN");
        log.debug("Discord Token: {}", discordToken);
        String discordChannel = properties.getProperty("DISCORD_CHANNEL");
        log.debug("Discord Channel: {}", discordChannel);
        if (StringUtils.isAnyBlank(domainName, domainPath, domainPort, domainPass, domainExec)) {
            throw new MissingDomainException(Reason.INVALID_CONFIGURATION, domain);
        }
        Path serverHome = Path.of(domainPath);
        if (Files.notExists(serverHome) || !Files.isDirectory(serverHome)) {
            throw new MissingDomainException(Reason.INVALID_CONFIGURATION, domain);
        }
        int serverPort;
        try {
            serverPort = Integer.parseInt(domainPort);
        } catch (NumberFormatException e) {
            throw new MissingDomainException(Reason.INVALID_CONFIGURATION, domain);
        }
        return new McServerDomain(domainName, serverHome, serverPort, domainPass, domainExec, domainWorld, discordToken, discordChannel);
    }
}
