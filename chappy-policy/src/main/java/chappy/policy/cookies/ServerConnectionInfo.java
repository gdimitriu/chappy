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
package chappy.policy.cookies;

import java.io.Serializable;

/**
 * Connection information of the server.
 * @author Gabriel Dimitriu
 *
 */
public class ServerConnectionInfo implements Serializable {

	/**
	 * default serial version.
	 */
	private static final long serialVersionUID = 1L;

	/** type of the server like: rest, jms */
	private String type;
	
	/** name of the server eg: localhost or network name */
	private String serverName;
	
	/** the connection port of the server */
	private int serverPort;
	
	/**
	 * 
	 */
	public ServerConnectionInfo() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @param serverName the serverName to set
	 */
	public void setServerName(final String serverName) {
		this.serverName = serverName;
	}

	/**
	 * @return the port
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * @param port the port to set
	 */
	public void setServerPort(final int port) {
		this.serverPort = port;
	}

}
