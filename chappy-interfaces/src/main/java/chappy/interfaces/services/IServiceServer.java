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
package chappy.interfaces.services;
/**
 * This is the interface for the servers that could start and stop.
 * @author Gabriel Dimitriu
 *
 */
public interface IServiceServer {

	/**
	 * stop the REST server.
	 * @throws Exception
	 */
	void stopServer() throws Exception;

	/**
	 * Start the REST server.
	 * @throws Exception
	 */
	void startServer() throws Exception;
	
	/**
	 * configure the server.
	 * @param configuration
	 */
	void configure(final Object configuration);

	/**
	 * set the name of the service.
	 * @param name the name of the server
	 */
	void setName(final String key);
	
	/**
	 * get the service name.
	 * @return service name
	 */
	String getName();

	/**
	 * get the server host name (default is localhost).
	 * @return server host.
	 */
	String getServerHost();
}