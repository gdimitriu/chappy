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

import java.io.IOException;

import chappy.clients.common.protocol.AbstractChappyAddTransformerMessage;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.services.IChappyClient;

/**
 * Chappy add transformer request wrapper abstract implementation for all services.
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractChappyAddTransformer implements IChappyClient {

	private AbstractChappyAddTransformerMessage protocol = null;
	/**
	 * 
	 */
	public AbstractChappyAddTransformer() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getCookie()
	 */
	@Override
	public IChappyCookie getCookie() {
		if (protocol == null) {
			return null;
		}
		return protocol.getCookie();
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#hasException()
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

	/**
	 * @return the protocol
	 */
	public AbstractChappyAddTransformerMessage getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(final AbstractChappyAddTransformerMessage protocol) {
		this.protocol = protocol;
	}
	
	/**
	 * set Transformer code to be send to chappy.
	 * @param transformerClassName
	 * @param packageName
	 * @throws IOException
	 */
	public void setTransformer(final String transformerClassName, final String packageName) throws IOException {
		this.protocol.setTransformerFromClassPath(transformerClassName, packageName);
	}

}
