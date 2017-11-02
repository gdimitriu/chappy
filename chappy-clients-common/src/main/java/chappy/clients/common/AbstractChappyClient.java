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
package chappy.clients.common;

import chappy.clients.common.protocol.AbstractChappyProtocolMessage;
import chappy.interfaces.IChappyProtocol;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.services.IChappyClient;

/**
 * Chappy login request wrapper, abstract implementation for all services.
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractChappyClient implements IChappyClient {

	/** abstract handler for login protocol */
	private AbstractChappyProtocolMessage protocol = null;
	
	/**
	 * 
	 */
	public AbstractChappyClient(){
	}

	/**
	 * @return the protocol
	 */
	@Override
	public AbstractChappyProtocolMessage getProtocol() {
		return (AbstractChappyProtocolMessage) protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	@Override
	public void setProtocol(final IChappyProtocol protocol) {
		this.protocol = (AbstractChappyProtocolMessage) protocol;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#getCookie()
	 */
	@Override
	public IChappyCookie getCookie() {
		if (protocol == null) {
			return null;
		}
		return protocol.getCookie();
	}
	
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#hasException()
	 */
	@Override
	public boolean hasException() {
		if (protocol == null) {
			return false;
		}
		return protocol.hasException();
	}
		
	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getTransactionException()
	 */
	@Override
	public Exception getTransactionException() {
		if (protocol == null) {
			return null;
		}
		return protocol.getException();		
	}
	
}
