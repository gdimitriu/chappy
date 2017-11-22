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
package chappy.clients.rest;

import javax.ws.rs.core.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import chappy.clients.common.AbstractChappyClient;
import chappy.clients.common.transaction.ChappyClientTransactionHolder;
import chappy.clients.common.transaction.RESTTransactionHolder;
import chappy.clients.rest.protocol.IRESTMessage;
import chappy.clients.rest.protocol.RESTLoginMessage;

/**
 * Chappy login request client for REST.
 * @author Gabriel Dimitriu
 *
 */
public class ChappyRESTLogin extends AbstractChappyClient implements IChappyRESTClient{

	/** http response for REST client */
	private Response response = null;
	
	private ChappyClientTransactionHolder transaction = new ChappyClientTransactionHolder();
	
	/**
	 * base constructor. 
	 * @param userName the user
	 * @param passwd the code
	 */
	public ChappyRESTLogin(final String userName, final String passwd, final boolean persistence) {
		setProtocol(new RESTLoginMessage(userName, passwd));
		((RESTLoginMessage) getProtocol()).setPersistence(persistence);
		transaction.setRestTransaction(new RESTTransactionHolder(userName, passwd, persistence));
	}


	/* (non-Javadoc)
	 * @see chappy.interfaces.rest.IRESTClient#createTransactionHolder()
	 */
	@Override
	public ChappyClientTransactionHolder createTransactionHolder() {
		transaction.getRestTransaction().setCookie(getCookie());
		return transaction;
	}
	
	@Override
	public ChappyRESTLogin send() {
		try {
			response = ((IRESTMessage) getProtocol())
					.encodeInboundMessage(transaction.getRestTarget()).invoke();
		} catch (JsonProcessingException e) {
			//Nothing to do for login.
		}
		((IRESTMessage) getProtocol()).decodeReplyMessage(response);
		return this;
	}
	
	@Override
	public String closeAll() {
		transaction.getRestClient().close();
		return "Chappy:= has been stopped ok.";
	}


	/**
	 * create the connection to the server.
	 * @param serverName
	 * @param port
	 * @throws Exception 
	 */
	public void createConnectionToServer(final String serverName, final int port) throws Exception {
		transaction.createConnectionToServer(serverName, port);
	}

}
