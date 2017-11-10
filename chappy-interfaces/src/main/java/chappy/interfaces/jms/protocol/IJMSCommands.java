/**
    Copyright (c) 2017 Gabriel Dimitriu All rights reserved.
	DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

    This file is part of chappy project.

    Chappy is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Chappy is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Chappy.  If not, see <http://www.gnu.org/licenses/>.
 */
package chappy.interfaces.jms.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * JMS Commands that will be routed.
 * @author Gabriel Dimitriu
 *
 */
public interface IJMSCommands {

	String COMMAND_PROPERTY = "command";
	String LOGIN = "login";
	String LOGOUT = "logout";
	String FLOW = "flow";
	String ADD_TRANSFORMER = "add_transformer";
	
	public static List<String> getAllCommands() {
		List<String >commands = new ArrayList<String>();
		commands.add(LOGIN);
		commands.add(LOGOUT);
		commands.add(FLOW);
		commands.add(ADD_TRANSFORMER);
		return commands;
	}
}
