/*
    Copyright 2022-2026 csGrebo

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

package net.csgrebo.mccontrol.cli;

import net.csgrebo.mccontrol.cli.command.BaseCommand;
import net.csgrebo.mccontrol.cli.support.ApplicationExecutionStrategy;
import net.csgrebo.mccontrol.cli.support.ApplicationExitCodeExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

public class CLI {
    private static final Logger log = LoggerFactory.getLogger(CLI.class);

    private CommandLine commandLine;


    public void configure() {
        commandLine = new CommandLine(new BaseCommand());
        commandLine.setExitCodeExceptionMapper(new ApplicationExitCodeExceptionMapper(commandLine));
        commandLine.setExecutionStrategy(new ApplicationExecutionStrategy());
    }

    public int run(String[] args) {
        if (commandLine == null) {
            throw new IllegalStateException("Command Line not configured. Run configure() first");
        }
        return commandLine.execute(args);
    }

}
