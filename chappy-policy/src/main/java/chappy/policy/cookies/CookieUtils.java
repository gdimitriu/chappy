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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.Collectors;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.services.IChappyServiceNamesConstants;

/**
 * @author Gabriel Dimitriu
 *
 */
public final class CookieUtils {
	
	static private String localServerName;
	
	static {
		localServerName = null;
		try {
			try {
				URL url = new URL("http://169.254.169.254/latest/meta-data/public-ipv4");
				//should be only one line
				localServerName = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream())).lines().collect(Collectors.joining(""));
			} catch (Exception e) {
				System.out.println("Chappy is not on AWS so change to normal sever:" + e.getLocalizedMessage());
				Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
				boolean found = false;
				for (NetworkInterface netint : Collections.list(nets)) {
					Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
					for (InetAddress inetAddress : Collections.list(inetAddresses)) {
						if (!inetAddress.getHostAddress().equals("127.0.0.1")
								&& !inetAddress.getHostAddress().equals("0.0.0.0")) {
							if (inetAddress instanceof Inet6Address)
								continue;
							localServerName = inetAddress.getHostAddress();
							found = true;
							break;
						}
					}
					if (found == true) {
						break;
					}
				}
			}
		} catch (Exception e) {
			try {
				localServerName = InetAddress.getLocalHost().getCanonicalHostName();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				localServerName = "localhost";
			}

		}
	}

	private CookieUtils() {
		
	}	
	
	public static String getServerName() {
		return localServerName;
	}
	/** decode the cookie.
	 * @param cookie the cookie
	 * @return CookieTransactionToken which was decoded from the cookie.
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public static IChappyCookie decodeCookie(final Cookie cookie) throws IOException, JsonProcessingException {
		ObjectReader or = new ObjectMapper().readerFor(CookieTransactionsToken.class);
    	CookieTransactionsToken received = new CookieTransactionsToken();
    	String str=new String(Base64.getDecoder().decode(cookie.getValue().getBytes()));
    	received=or.readValue(str);
		return received;
	}
	
	/**
	 * encode the cookie
	 * @param cookie coming from chappy
	 * @return encoded chappy REST cookie
	 * @throws JsonProcessingException
	 */
	public static NewCookie encodeCookie(final IChappyCookie cookie) throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    	String json = ow.writeValueAsString(cookie);
    	byte[] base64json=Base64.getEncoder().encode(json.getBytes());
    	return new NewCookie(IChappyServiceNamesConstants.COOKIE_USER_DATA, new String(base64json));
	}

}
