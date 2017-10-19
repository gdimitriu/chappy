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

import chappy.clients.common.protocol.AbstractChappyLoginMessage;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.jms.protocol.IJMSMessages;
import chappy.interfaces.services.IChappyClient;

/**
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractChappyLogin implements IChappyClient {

	/** abstract handler for login protocol */
	private AbstractChappyLoginMessage loginProtocol = null;
	
	/**
	 * 
	 */
	public AbstractChappyLogin(){
	}

	/**
	 * @return the loginProtocol
	 */
	public AbstractChappyLoginMessage getLoginProtocol() {
		return loginProtocol;
	}

	/**
	 * @param loginProtocol the loginProtocol to set
	 */
	public void setLoginProtocol(final AbstractChappyLoginMessage loginProtocol) {
		this.loginProtocol = loginProtocol;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#getCookie()
	 */
	@Override
	public IChappyCookie getCookie() {
		if (loginProtocol == null) {
			return null;
		}
		return loginProtocol.getCookie();
	}
	
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#hasException()
	 */
	@Override
	public boolean hasException() {
		if (loginProtocol == null) {
			return false;
		}
		return loginProtocol.hasException();
	}
	
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#getTransactionErrorMessage()
	 */
	@Override
	public String getTransactionErrorMessage() {
		if (loginProtocol == null) {
			return IJMSMessages.REPLY_NOT_READY;
		}
		return loginProtocol.getReplyMessage();
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getTransactionException()
	 */
	@Override
	public Exception getTransactionException() {
		if (loginProtocol == null) {
			return null;
		}
		return loginProtocol.getException();		
	}
	
}
