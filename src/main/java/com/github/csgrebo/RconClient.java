/*
    Copyright 2022 csGrebo

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.csgrebo;

import nl.vv32.rcon.Rcon;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Minecraft Remote CONtrol Client.
 * <p>Command line client for Minecraft's Remote CONtrol Protocol. This is to ease automation of a Minecraft server
 * instance.</p>
 *
 * @since 1.0.0
 * @author csGrebo
 */
@Command(name = "rcon", description = "Execute an RCON command against the configured Minecraft Server instance",
        exitCodeList = {"0 - Successful Send", "1 - Communication Error", "2 - Authentication Failure"})
public class RconClient implements Callable<Integer> {

    @Option(names = {"--port", "-p"}, description = "Enabled RCON Port", required = true)
    private int port;

    @Option(names = {"-id", "--passphrase"}, description = "Configured RCON Passphrase", required = true,
            interactive = true, arity = "0..1")
    private String passphrase;

    @Parameters
    private List<String> commands;

    /**
     * Callable invocation method.
     * <p>Executes the submitted RCON command against a local Minecraft server listening on the provided port</p>
     *
     * @return Program Exit Code
     */
    @Override
    public Integer call() {
        String fullCommand = String.join(" ", commands);
        System.out.println("Trying to send command: " + fullCommand);
        try (Rcon rcon = Rcon.newBuilder()
                .withChannel(SocketChannel.open(new InetSocketAddress("localhost", port)))
                .withCharset(StandardCharsets.UTF_8)
                .build()) {
            if (rcon.authenticate(new String(Base64.getDecoder().decode(passphrase)))) {
                System.out.println(rcon.sendCommand(fullCommand));
            } else {
                System.err.println("Authentication failed");
                return 2;
            }
        } catch (IOException e) {
            System.err.println("Error executing command - " + e.getLocalizedMessage());
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    /**
     * Application Main Method.
     *
     * @param args Program Execution Arguments
     */
    public static void main(String[] args) {
        System.exit(new CommandLine(new RconClient()).execute(args));
    }
}