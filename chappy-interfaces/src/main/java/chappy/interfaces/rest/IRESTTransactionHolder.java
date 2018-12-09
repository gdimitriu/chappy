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
package chappy.interfaces.rest;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import chappy.interfaces.transactions.IClientTransaction;

/**
 * Interface for REST client transaction.
 * @author Gabriel Dimitriu
 *
 */
public interface IRESTTransactionHolder extends IClientTransaction {

	/**
	 * @return the restClient
	 */
	public Client getRestClient();
	
	/**
	 * @return the baseUri
	 */
	public URI getBaseUri();
	
	/**
	 * @return the restTarget
	 */
	public WebTarget getRestTarget();
	
	/**
	 * create the connection to server.
	 * @param serverName
	 * @param port
	 * @throws Exception
	 */
	public void createConnectionToServer(final String serverName, final int port) throws Exception;

}
