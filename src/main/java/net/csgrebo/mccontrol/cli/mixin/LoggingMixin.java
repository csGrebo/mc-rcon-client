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

package net.csgrebo.mccontrol.cli.mixin;

import ch.qos.logback.classic.Level;
import net.csgrebo.mccontrol.cli.command.BaseCommand;
import net.csgrebo.mccontrol.config.TylerConfigurator;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Spec.Target;

public class LoggingMixin {

    @Spec(Target.MIXEE)
    private CommandSpec mixee;

    private boolean[] verbosity = new boolean[0];

    @Option(names = {"-v", "--verbose"}, description = {"Verbosity", "Add multiple verbose options to increase"})
    public void setVerbosity(boolean[] verbosity) {
        getTopLevelLoggingMixin(mixee).verbosity = verbosity;
    }

    public boolean[] getVerbosity() {
        return getTopLevelLoggingMixin(mixee).verbosity;
    }

    private static LoggingMixin getTopLevelLoggingMixin(CommandSpec spec) {
        return ((BaseCommand) spec.root().userObject()).getLoggingMixin();
    }

    public static void configure(ParseResult parseResult) {
        getTopLevelLoggingMixin(parseResult.commandSpec()).configureLoggers();
    }

    private void configureLoggers() {
        Level level = getTopLevelLoggingMixin(mixee).calculateLogLevel();
        TylerConfigurator.getInstance().updateLogLevel(level);
    }

    private Level calculateLogLevel() {
        return switch (getVerbosity().length) {
            case 0 -> Level.WARN;
            case 1 -> Level.INFO;
            case 2 -> Level.DEBUG;
            default -> Level.TRACE;
        };
    }
}
