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
package chappy.clients.common.transaction;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.rest.IRESTTransactionHolder;
import chappy.interfaces.rest.LocalDateTimeContextResolver;
import chappy.providers.cookie.CookieFactory;

/**
 * @author Gabriel Dimitriu
 *
 */
public class RESTTransactionHolder implements IRESTTransactionHolder{

	/** cookie for the transactions */
	private IChappyCookie currentCookie = null;
	
	/** rest client */
	private Client currentClient = null; 
	
	/** base uri for the rest client */
	private URI currentBaseUri = null;
	
	/** rest target for client */
	private WebTarget currentTarget = null;
	/**
	 * 
	 */
	public RESTTransactionHolder() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param client
	 * @param baseUri
	 * @param target
	 * @param cookie
	 */
	public RESTTransactionHolder(final Client client, final URI baseUri, final WebTarget target, final IChappyCookie cookie) {
		this.currentCookie = cookie;
		this.currentClient = client;
		this.currentBaseUri = baseUri;
		this.currentTarget = target; 
	}

	/**
	 * constructor to create a REST transaction from other protocol.
	 * @param cookie the cookie which contains all info needed
	 */
	public RESTTransactionHolder(final IChappyCookie cookie) {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @param userName
	 * @param passwd
	 * @param persistence
	 */
	public RESTTransactionHolder(final String userName, final String passwd, final boolean persistence) {
		currentCookie = CookieFactory.getFactory().newCookie(userName, passwd, persistence);
	}

	@Override
	public IChappyCookie getCookie() {
		return this.currentCookie;
	}

	/**
	 * @param chappyCookie the chappyCookie to set
	 */
	public void setCookie(IChappyCookie chappyCookie) {
		this.currentCookie = chappyCookie;
	}

	/**
	 * @return the restClient
	 */
	@Override
	public Client getRestClient() {
		return currentClient;
	}

	/**
	 * @param restClient the restClient to set
	 */
	public void setRestClient(Client restClient) {
		this.currentClient = restClient;
	}

	/**
	 * @return the baseUri
	 */
	@Override
	public URI getBaseUri() {
		return currentBaseUri;
	}

	/**
	 * @param baseUri the baseUri to set
	 */
	public void setBaseUri(URI baseUri) {
		this.currentBaseUri = baseUri;
	}

	/**
	 * @return the restTarget
	 */
	@Override
	public WebTarget getRestTarget() {
		return currentTarget;
	}

	/**
	 * @param restTarget the restTarget to set
	 */
	public void setRestTarget(WebTarget restTarget) {
		this.currentTarget = restTarget;
	}
	
	/**
	 * @param serverName
	 * @param port
	 * @throws Exception 
	 */
	@Override
	public void createConnectionToServer(final String serverName, final int port) throws Exception {
		currentClient = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class)
				.register(JacksonJaxbJsonProvider.class)
				.register(LocalDateTimeContextResolver.class);
		currentBaseUri = UriBuilder.fromUri("{arg}").build(new String[] { "http://" + serverName + ":" + port + "/" }, false);
		currentTarget = currentClient.target(currentBaseUri);
	}
}
