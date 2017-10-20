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

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import chappy.clients.common.AbstractChappyLogin;
import chappy.clients.common.transaction.RESTTransactionHolder;
import chappy.clients.rest.protocol.IRESTMessage;
import chappy.clients.rest.protocol.RESTLoginMessage;
import chappy.interfaces.rest.IRESTClient;
import chappy.interfaces.rest.IRESTTransactionHolder;
import chappy.interfaces.rest.LocalDateTimeContextResolver;

/**
 * @author Gabriel Dimitriu
 *
 */
public class ChappyRESTLogin extends AbstractChappyLogin implements IRESTClient{

	/** http REST client */
	private Client client = null;
	
	/** base URI for chappy */
	private URI baseUri = null;
	
	/** web target for REST client */
	private WebTarget target = null;
	
	/** http response for REST client */
	private Response response = null;
	
	/**
	 * base constructor. 
	 * @param userName the user
	 * @param passwd the code
	 */
	public ChappyRESTLogin(final String userName, final String passwd, final boolean persistence) {
		setProtocol(new RESTLoginMessage(userName, passwd));
		getProtocol().setPersistence(persistence);
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getStatus()
	 */
	@Override
	public String getStatus() {
		return ((IRESTMessage) getProtocol()).getStatus().getReasonPhrase();
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getStatusCode()
	 */
	@Override
	public int getStatusCode() {
		return ((IRESTMessage) getProtocol()).getStatus().getStatusCode();
	}

	
	/**
	 * @param serverName
	 * @param port
	 * @throws Exception 
	 */
	public void createConnectionToServer(final String serverName, final int port) throws Exception {
		client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class)
				.register(JacksonJaxbJsonProvider.class)
				.register(LocalDateTimeContextResolver.class);;
		baseUri = UriBuilder.fromUri("{arg}").build(new String[] { "http://" + serverName + ":" + port + "/" }, false);
		target = client.target(baseUri);
	}

	@Override
	public void send() {
		try {
			response = ((IRESTMessage) getProtocol()).encodeInboundMessage(target).invoke();
		} catch (JsonProcessingException e) {
			//Nothing to do for login.
		}
		((IRESTMessage) getProtocol()).decodeReplyMessage(response);
	}
	
	@Override
	public String closeAll() {
		client.close();
		return "Chappy:= has been stopped ok.";
	}

	@Override
	public IRESTTransactionHolder createTransactionHolder() {
		return new RESTTransactionHolder(client, baseUri, target, getCookie());
	}

	@Override
	public String getTransactionErrorMessage() {
		if (getProtocol() == null) {
			return Status.NO_CONTENT.getReasonPhrase();
		}
		return getProtocol().getReplyMessage();
	}

}
