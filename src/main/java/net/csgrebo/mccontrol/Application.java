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

package net.csgrebo.mccontrol;

import net.csgrebo.mccontrol.cli.CLI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.configure();
        int ecode = cli.run(args);
        System.exit(ecode);
    }

}
