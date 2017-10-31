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

import chappy.clients.common.AbstractChappyAddTransformer;
import chappy.clients.rest.protocol.RESTAddTransformerMessage;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.rest.IRESTTransactionHolder;
import chappy.interfaces.transactions.IClientTransaction;

/**
 * Chappy add transformer request wrapper for REST.
 * @author Gabriel Dimitriu
 *
 */
public class ChappyRESTAddTransformer extends AbstractChappyAddTransformer implements IChappyRESTClient {

	/** client transaction */
	IRESTTransactionHolder clientTransaction = null;
	
	/** http response for REST client */
	private Response response = null;
	
	/**
	 * 
	 */
	public ChappyRESTAddTransformer(final String transformerName, final IClientTransaction client) {
		clientTransaction = (IRESTTransactionHolder) client;
		setProtocol(new RESTAddTransformerMessage(transformerName));
		getProtocol().setCookie(clientTransaction.getCookie());
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.rest.IRESTClient#send()
	 */
	@Override
	public void send() {
		RESTAddTransformerMessage addTransformer = (RESTAddTransformerMessage) getProtocol();
		try {
			response = addTransformer.encodeInboundMessage(clientTransaction.getRestTarget()).invoke();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			getProtocol().setException(e);
		}
		addTransformer.decodeReplyMessage(response);
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.rest.IRESTClient#closeAll()
	 */
	@Override
	public String closeAll() {
		clientTransaction.getRestClient().close();
		return "Chappy:= has been stopped ok.";
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.rest.IRESTClient#createTransactionHolder()
	 */
	@Override
	public IRESTTransactionHolder createTransactionHolder() {
		return clientTransaction;
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getCookie()
	 */
	@Override
	public IChappyCookie getCookie() {
		if (getProtocol() == null) {
			return null;
		}
		return clientTransaction.getCookie();
	}
}
