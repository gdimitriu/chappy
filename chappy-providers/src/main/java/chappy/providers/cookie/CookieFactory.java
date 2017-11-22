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
package chappy.providers.cookie;

import chappy.interfaces.cookies.IChappyCookie;
import chappy.policy.cookies.CookieTransactionsToken;


/**
 * Cookie factory.
 * @author Gabriel Dimitriu
 *
 */
public class CookieFactory {

	private static CookieFactory factory = new CookieFactory();
	/**
	 * constructor.
	 */
	private CookieFactory() {
		
	}
	
	/**
	 * get the singleton factory.
	 * @return the factory
	 */
	public static CookieFactory getFactory() {
		return factory;
	}
	
	/**
	 * newCookie request.
	 * @param requester
	 * @return cookie instance.
	 */
	public IChappyCookie newCookie(final Class<?> requester, final String userName, final String passwd) {
		
		if (requester == null) {
			//special case for not transactional operations. 
		}
		
		CookieTransactionsToken cookie = new CookieTransactionsToken(userName, passwd);
		return cookie;
	}
	
	/**
	 * newCookie request.
	 * @param requester
	 * @return cookie instance.
	 */
	public IChappyCookie newCookie(final String userName, final String passwd, final boolean persistence) {
		CookieTransactionsToken cookie = new CookieTransactionsToken(userName, passwd, persistence);
		return cookie;
	}
}
