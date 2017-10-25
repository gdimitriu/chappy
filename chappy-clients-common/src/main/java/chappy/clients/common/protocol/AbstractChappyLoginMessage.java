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
 * Chappy login request protocol message abstract implementation for all services.
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractChappyLoginMessage {
	
	/** user name */
	private String userName = null;
	
	/** user password */
	private String password = null;
	
	/** user persistence required */
	private boolean persistence = false;
	
	/** cookie for the transaction */
	private IChappyCookie cookie = null;
	
	/** reply message from chappy */
	private String replyMessage = "";
	
	/** exception in case of internal server */ 
	private Exception exception = null;
	
	/**
	 * default constructor
	 */
	public AbstractChappyLoginMessage() {
		
	}
	/**
	 * normal constructor
	 */
	public AbstractChappyLoginMessage(final String userName, final String password) {
		this.userName = userName;
		this.password = password;
	}
	
	/**
	 * get the userName of the login user.
	 * @return userName of the login user.
	 */
	public String getUserName() {
		return this.userName;
	}
	
	/**
	 * get the password of the login user.
	 * @return password of the login user.
	 */
	public String getPassword() {
		return this.password;
	}
	

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * get the persistence flag;
	 * @return true if persistence is required.
	 */
	public boolean isPersistence() {
		return persistence;
	}
	
	
	/**
	 * set the required persistence.
	 * @param persistence true if persistence is required.
	 */
	public void setPersistence(final boolean persistence) {
		this.persistence = persistence;
	}
	
	/**
	 * get the reply message from the chappy.
	 * @return the replyMessage
	 */
	public String getReplyMessage() {
		return replyMessage;
	}
	
	/**
	 * set the reply message from chappy.
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
	 * set the cookie that correspond to this login
	 * @param cookie that correspond to this login.
	 */
	public void setCookie(final IChappyCookie cookie) {
		this.cookie = cookie;
	}
	
	/**
	 * get the cookie for this login.
	 * @return cookie for this login.
	 */
	public IChappyCookie getCookie() {
		return this.cookie;
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
