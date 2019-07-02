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

import javax.jdo.annotations.NotPersistent;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Cookie for transactions.
 * @author Gabriel Dimitriu
 *
 */

@XmlRootElement
public class CookieTransactionsToken extends CookieTransaction{

	/**
	 * 
	 */
	@NotPersistent
	private static final long serialVersionUID = 1L;

	/**
	 * cookie transaction token constructor
	 * @param userName 
	 */
	public CookieTransactionsToken(final String userName) {
		setCredentials(userName, null);
	}
	
	/**
	 * cookie transaction token constructor
	 * @param userName
	 * @param passwd of the user
	 */
	public CookieTransactionsToken(final String userName, final String passwd) {
		setCredentials(userName, passwd);
	}
	
	/**
	 * cookie transaction token constructor
	 * @param userName
	 * @param passwd of the user
	 * @param persistence 
	 */
	public CookieTransactionsToken(final String userName, final String passwd, final boolean persistence) {
		setCredentials(userName, passwd);
		setPersistence(persistence);
	}
	
	/**
	 * default constructor.
	 */
	public CookieTransactionsToken() {
		
	}
}
