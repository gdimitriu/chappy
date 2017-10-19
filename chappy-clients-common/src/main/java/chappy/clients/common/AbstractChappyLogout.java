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

import chappy.clients.common.protocol.AbstractChappyLogoutMessage;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.services.IChappyClient;

/**
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractChappyLogout implements IChappyClient{
	
	/** internal handler for logout protocol */
	private AbstractChappyLogoutMessage logoutProtocol = null;

	/**
	 * 
	 */
	public AbstractChappyLogout() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the logoutProtocol
	 */
	public AbstractChappyLogoutMessage getLogoutProtocol() {
		return logoutProtocol;
	}

	/**
	 * @param logoutProtocol the logoutProtocol to set
	 */
	public void setLogoutProtocol(AbstractChappyLogoutMessage logoutProtocol) {
		this.logoutProtocol = logoutProtocol;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#hasException()
	 */
	@Override
	public boolean hasException() {
		if (logoutProtocol == null) {
			return false;
		}
		return logoutProtocol.hasException();
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getTransactionErrorMessage()
	 */
	@Override
	public String getTransactionErrorMessage() {
		if (logoutProtocol == null) {
			return IJMSStatus.REPLY_NOT_READY;
		}
		return logoutProtocol.getReplyMessage();
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getTransactionException()
	 */
	@Override
	public Exception getTransactionException() {
		if (logoutProtocol == null) {
			return null;
		}
		return logoutProtocol.getException();
	}
}
