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
package chappy.interfaces.cookies;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Base class for cookie.
 * @author Gabriel Dimitriu
 *
 */
@XmlRootElement
public abstract class CookieTransaction {

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

	/**
	 * get the user name
	 * @return the user name
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * set the user Name.
	 * @param nameUser
	 */
	public void setUserName(String nameUser) {
		this.userName = nameUser;
	}

	/**
	 * get the transaction id.
	 * @return transaction id
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * set the transaction id.
	 * @param transactionId transaction id
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * generate the storage id.
	 * @return storage id for the hash.
	 */
	public String generateStorageId() {
		return userName + ":" + transactionId;
	}
}
