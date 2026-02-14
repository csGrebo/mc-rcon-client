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

package net.csgrebo.mccontrol.cli.command;

import net.csgrebo.mccontrol.cli.mixin.ConfigurationMixin;
import net.csgrebo.mccontrol.cli.mixin.LoggingMixin;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

@Command(name = "mcserver", mixinStandardHelpOptions = true, subcommands = {
        CommandLine.HelpCommand.class,
        BackupServerCommand.class,
        DiscordMessageCommand.class,
        MessageServerCommand.class,
        StartServerCommand.class,
        StopServerCommand.class,
        TestCommand.class
})
public class BaseCommand {

    @Mixin
    private LoggingMixin loggingMixin;

    @Mixin
    private ConfigurationMixin configurationMixin;

    public LoggingMixin getLoggingMixin() {
        return loggingMixin;
    }

    public void setLoggingMixin(LoggingMixin loggingMixin) {
        this.loggingMixin = loggingMixin;
    }

    public ConfigurationMixin getConfigurationMixin() {
        return configurationMixin;
    }

    public void setConfigurationMixin(ConfigurationMixin configurationMixin) {
        this.configurationMixin = configurationMixin;
    }
}
