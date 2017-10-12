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
package chappy.policy.cookies;

import javax.xml.bind.annotation.XmlRootElement;

import chappy.interfaces.cookies.IChappyCookie;

/**
 * Base class for cookie.
 * @author Gabriel Dimitriu
 *
 */
@XmlRootElement
public abstract class CookieTransaction implements IChappyCookie {

	/**
	 * default but is overriden in the implementation class.
	 */
	private static final long serialVersionUID = 1L;

	/** name of the user */
	private String userName;
	
	/** transaction Id */
	private String transactionId;
	
	/**
	 * base cookie.
	 */
	public CookieTransaction() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.policy.cookies.IChappyCookie#getUserName()
	 */
	@Override
	public String getUserName() {
		return userName;
	}
	
	/* (non-Javadoc)
	 * @see chappy.policy.cookies.IChappyCookie#setUserName(java.lang.String)
	 */
	public void setUserName(String nameUser) {
		this.userName = nameUser;
	}

	/* (non-Javadoc)
	 * @see chappy.policy.cookies.IChappyCookie#getTransactionId()
	 */
	@Override
	public String getTransactionId() {
		return transactionId;
	}

	/* (non-Javadoc)
	 * @see chappy.policy.cookies.IChappyCookie#setTransactionId(java.lang.String)
	 */
	@Override
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/* (non-Javadoc)
	 * @see chappy.policy.cookies.IChappyCookie#generateStorageId()
	 */
	@Override
	public String generateStorageId() {
		return userName + ":" + transactionId;
	}
}
