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
import javax.ws.rs.client.WebTarget;

import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.rest.IRESTTransactionHolder;

/**
 * @author Gabriel Dimitriu
 *
 */
public class RESTTransactionHolder implements IRESTTransactionHolder{

	/** cookie for the transactions */
	private IChappyCookie chappyCookie = null;
	
	/** rest client */
	private Client restClient = null; 
	
	/** base uri for the rest client */
	private URI baseUri = null;
	
	/** rest target for client */
	private WebTarget restTarget = null;
	/**
	 * 
	 */
	public RESTTransactionHolder() {
		// TODO Auto-generated constructor stub
	}

	public RESTTransactionHolder(final Client client, final URI baseUri, final WebTarget target, final IChappyCookie cookie) {
		this.chappyCookie = cookie;
		this.restClient = client;
		this.baseUri = baseUri;
		this.restTarget = target; 
	}

	@Override
	public IChappyCookie getCookie() {
		return this.chappyCookie;
	}

	/**
	 * @param chappyCookie the chappyCookie to set
	 */
	public void setCookie(IChappyCookie chappyCookie) {
		this.chappyCookie = chappyCookie;
	}

	/**
	 * @return the restClient
	 */
	@Override
	public Client getRestClient() {
		return restClient;
	}

	/**
	 * @param restClient the restClient to set
	 */
	public void setRestClient(Client restClient) {
		this.restClient = restClient;
	}

	/**
	 * @return the baseUri
	 */
	@Override
	public URI getBaseUri() {
		return baseUri;
	}

	/**
	 * @param baseUri the baseUri to set
	 */
	public void setBaseUri(URI baseUri) {
		this.baseUri = baseUri;
	}

	/**
	 * @return the restTarget
	 */
	@Override
	public WebTarget getRestTarget() {
		return restTarget;
	}

	/**
	 * @param restTarget the restTarget to set
	 */
	public void setRestTarget(WebTarget restTarget) {
		this.restTarget = restTarget;
	}
}
