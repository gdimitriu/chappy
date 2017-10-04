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
package chappy.services.servers.rest.cookies;

import java.io.IOException;
import java.util.Base64;

import javax.ws.rs.core.Cookie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import chappy.clients.cookies.CookieTransactionsToken;

/**
 * @author Gabriel Dimitriu
 *
 */
public final class CookieUtils {

	private CookieUtils() {
		
	}
	
	/** decode the cookie.
	 * @param cookie the cookie
	 * @return CookieTransactionToken which was decoded from the cookie.
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public static CookieTransactionsToken decodeCookie(Cookie cookie) throws IOException, JsonProcessingException {
		ObjectReader or = new ObjectMapper().readerFor(CookieTransactionsToken.class);
    	CookieTransactionsToken received = new CookieTransactionsToken();
    	String str=new String(Base64.getDecoder().decode(cookie.getValue().getBytes()));
    	received=or.readValue(str);
		return received;
	}
}
