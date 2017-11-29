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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;

import chappy.clients.common.AbstractChappyTransformFlow;
import chappy.clients.common.transaction.ChappyClientTransactionHolder;
import chappy.clients.common.transaction.RESTTransactionHolder;
import chappy.clients.rest.protocol.RESTTransformFlowMessage;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.transactions.IClientTransaction;

/**
 * Chappy transform a flow  request client for REST.
 * @author Gabriel Dimitriu
 *
 */
public class ChappyRESTTransformFlow extends AbstractChappyTransformFlow implements IChappyRESTClient {

	/** client transaction */
	private ChappyClientTransactionHolder clientTransaction = new ChappyClientTransactionHolder();
	
	/** http response for REST client */
	private Response response = null;
	
	/**
	 * @param input string for the transformation
	 * @param configuration of the transformation flow
	 * @param client the chappy client transaction
	 */
	public ChappyRESTTransformFlow(final String input, final String configuration, final IClientTransaction client) {
		if (client instanceof ChappyClientTransactionHolder) {
			clientTransaction = (ChappyClientTransactionHolder) client;
		} else if (client instanceof RESTTransactionHolder) {
			clientTransaction.setRestTransaction((RESTTransactionHolder) client);
		}
		setProtocol(new RESTTransformFlowMessage(input, configuration, null));
		getProtocol().setCookie(clientTransaction.getCookie());
	}
	
	/**
	 * @param input string for the transformation
	 * @param configuration of the transformation flow
	 * @param client the chappy client transaction
	 */
	public ChappyRESTTransformFlow(final String input, final MediaType type, final String configuration,
			final IClientTransaction client) {
		if (client instanceof ChappyClientTransactionHolder) {
			clientTransaction = (ChappyClientTransactionHolder) client;
		} else if (client instanceof RESTTransactionHolder) {
			clientTransaction.setRestTransaction((RESTTransactionHolder) client);
		}
		setProtocol(new RESTTransformFlowMessage(input, type, configuration, null));
		getProtocol().setCookie(clientTransaction.getCookie());
	}
	

	/**
	 * @param input string for the transformation
	 * @param configuration of the transformation flow
	 * @param flowName the name of the flow
	 * @param client the chappy client transaction
	 */
	public ChappyRESTTransformFlow(final String input, final MediaType type, final String configuration, final String flowName,
			final IClientTransaction client) {
		if (client instanceof ChappyClientTransactionHolder) {
			clientTransaction = (ChappyClientTransactionHolder) client;
		} else if (client instanceof RESTTransactionHolder) {
			clientTransaction.setRestTransaction((RESTTransactionHolder) client);
		}
		setProtocol(new RESTTransformFlowMessage(input, type, configuration, flowName));
		getProtocol().setCookie(clientTransaction.getCookie());
	}
	
	/**
	 * @param client the chappy client transaction
	 */
	public ChappyRESTTransformFlow(final IClientTransaction client) {
		if (client instanceof ChappyClientTransactionHolder) {
			clientTransaction = (ChappyClientTransactionHolder) client;
		} else if (client instanceof RESTTransactionHolder) {
			clientTransaction.setRestTransaction((RESTTransactionHolder) client);
		}
		setProtocol(new RESTTransformFlowMessage());
		getProtocol().setCookie(clientTransaction.getCookie());
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.rest.IRESTClient#createTransactionHolder()
	 */
	@Override
	public ChappyClientTransactionHolder createTransactionHolder() {
		return clientTransaction;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.rest.IRESTClient#send()
	 */
	@Override
	public ChappyRESTTransformFlow send() {
		RESTTransformFlowMessage transformer = (RESTTransformFlowMessage) getProtocol();
		try {
			response = transformer.encodeInboundMessage(clientTransaction.getRestTarget()).invoke();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			getProtocol().setException(e);
		}
		transformer.decodeReplyMessage(response);
		return this;
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
