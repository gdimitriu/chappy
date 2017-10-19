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

import chappy.interfaces.cookies.IChappyCookie;

/**
 * Abstract implementation of the logout message for chappy.
 * @author Gabriel Dimitriu
 *
 */
public class AbstractChappyLogoutMessage {

	/** cookie for the transaction */
	private IChappyCookie cookie = null;
	
	/** reply message from chappy */
	private String replyMessage = "";
	
	/** exception in case of internal server */ 
	private Exception exception = null;
	
	/**
	 * default constructor
	 */
	public AbstractChappyLogoutMessage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the cookie
	 */
	public IChappyCookie getCookie() {
		return cookie;
	}

	/**
	 * @param cookie the cookie to set
	 */
	public void setCookie(final IChappyCookie cookie) {
		this.cookie = cookie;
	}

	/**
	 * @return the replyMessage
	 */
	public String getReplyMessage() {
		return replyMessage;
	}

	/**
	 * @param replyMessage the replyMessage to set
	 */
	public void setReplyMessage(final String replyMessage) {
		this.replyMessage = replyMessage;
	}
	
	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(final Exception exception) {
		this.exception = exception;
	}
	
	/**
	 * query if it has exception coming from chappy.
	 * @return true if has exception from chappy.
	 */
	public boolean hasException() {
		if (exception != null) {
			return true;			
		} else {
			return false;
		}
	}
}
