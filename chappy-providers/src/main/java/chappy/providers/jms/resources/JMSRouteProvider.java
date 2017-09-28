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
package chappy.providers.jms.resources;

import java.util.HashMap;
import java.util.Map;

import chappy.interfaces.jms.resources.IJMSCommands;
import chappy.interfaces.jms.resources.IQueueNameConstants;

/**
 * @author Gabriel Dimitriu
 *
 */
public class JMSRouteProvider {
	
	private static JMSRouteProvider singleton = new JMSRouteProvider();
	
	private Map<String, String> routes = null;

	/**
	 * 
	 */
	private JMSRouteProvider() {
		routes = new HashMap<String, String>();
		routes.put(IJMSCommands.LOGIN, IQueueNameConstants.AUTHENTICATION);
		routes.put(IJMSCommands.LOGOUT, IQueueNameConstants.AUTHENTICATION);
	}
	
	public static JMSRouteProvider getInstance() {
		return singleton;
	}

	public String getRouteQueueName(final String inputCommandName) {
		if (routes.containsKey(inputCommandName)) {
			return routes.get(inputCommandName);
		}
		return inputCommandName;
	}
}
