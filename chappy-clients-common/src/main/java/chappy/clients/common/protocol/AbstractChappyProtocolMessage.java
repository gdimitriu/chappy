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
package chappy.clients.common.protocol;

import chappy.interfaces.IChappyProtocol;
import chappy.interfaces.cookies.IChappyCookie;

/**
 * Abstract class for message protocol.
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractChappyProtocolMessage implements IChappyProtocol{

	/** cookie for the transaction */
	private IChappyCookie cookie = null;
		
	/** reply message from chappy */
	private String replyMessage = "";
	
	/** exception in case of internal server */ 
	private Exception exception = null;
	
	/**
	 * 
	 */
	public AbstractChappyProtocolMessage() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.clients.common.protocol.IChappyProtocol#getReplyMessage()
	 */
	@Override
	public String getReplyMessage() {
		return replyMessage;
	}
	
	/* (non-Javadoc)
	 * @see chappy.clients.common.protocol.IChappyProtocol#setReplyMessage(java.lang.String)
	 */
	@Override
	public void setReplyMessage(final String replyMessage) {
		this.replyMessage = replyMessage;
	}
	
	/* (non-Javadoc)
	 * @see chappy.clients.common.protocol.IChappyProtocol#getException()
	 */
	@Override
	public Exception getException() {
		return exception;
	}

	/* (non-Javadoc)
	 * @see chappy.clients.common.protocol.IChappyProtocol#setException(java.lang.Exception)
	 */
	@Override
	public void setException(final Exception exception) {
		this.exception = exception;
	}

	/* (non-Javadoc)
	 * @see chappy.clients.common.protocol.IChappyProtocol#setCookie(chappy.interfaces.cookies.IChappyCookie)
	 */
	@Override
	public void setCookie(final IChappyCookie cookie) {
		this.cookie = cookie;
	}
	
	/* (non-Javadoc)
	 * @see chappy.clients.common.protocol.IChappyProtocol#getCookie()
	 */
	@Override
	public IChappyCookie getCookie() {
		return this.cookie;
	}
	
	/* (non-Javadoc)
	 * @see chappy.clients.common.protocol.IChappyProtocol#hasException()
	 */
	@Override
	public boolean hasException() {
		if (exception != null) {
			return true;			
		} else {
			return false;
		}
	}
}
