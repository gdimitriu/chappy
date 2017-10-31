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
import chappy.clients.rest.protocol.RESTLogoutMessage;
import chappy.interfaces.rest.IRESTTransactionHolder;
import chappy.interfaces.transactions.IClientTransaction;

/**
 * Chappy logout request client for REST.
 * @author Gabriel Dimitriu
 *
 */
public class ChappyRESTLogout extends AbstractChappyClient implements IChappyRESTClient {

	/** client transaction */
	IRESTTransactionHolder clientTransaction = null;
	
	/** http response for REST client */
	private Response response = null;
	/**
	 * 
	 */
	public ChappyRESTLogout(final IClientTransaction client){
		clientTransaction = (IRESTTransactionHolder) client;
		setProtocol(new RESTLogoutMessage());
		getProtocol().setCookie(client.getCookie());
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.rest.IRESTClient#createTransactionHolder()
	 */
	@Override
	public IRESTTransactionHolder createTransactionHolder() {
		return clientTransaction;
	}

	@Override
	public void send() {
		RESTLogoutMessage logout = (RESTLogoutMessage) getProtocol();
		try {
			response = logout.encodeInboundMessage(clientTransaction.getRestTarget()).invoke();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			logout.setException(e);
		}
		logout.decodeReplyMessage(response);
	}

	@Override
	public String closeAll() {
		clientTransaction.getRestClient().close();
		return "Chappy:= has been stopped ok.";
	}
}