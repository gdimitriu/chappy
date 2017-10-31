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

import javax.ws.rs.core.Response.Status;

import chappy.clients.rest.protocol.IRESTMessage;
import chappy.interfaces.rest.IRESTClient;
import chappy.interfaces.services.IChappyClient;

/**
 * @author Gabriel Dimitriu
 *
 */
public interface IChappyRESTClient extends IChappyClient, IRESTClient {

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getStatus()
	 */
	@Override
	public default String getStatus() {
		return ((IRESTMessage) getProtocol()).getStatus().getReasonPhrase();
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getStatusCode()
	 */
	@Override
	public default int getStatusCode() {
		return ((IRESTMessage) getProtocol()).getStatus().getStatusCode();
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getTransactionErrorMessage()
	 */
	@Override
	public default String getTransactionErrorMessage() {
		if (getProtocol() == null) {
			return Status.NO_CONTENT.getReasonPhrase();
		}
		return getProtocol().getReplyMessage();
	}
}
