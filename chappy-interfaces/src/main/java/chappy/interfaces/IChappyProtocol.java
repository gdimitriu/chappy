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
package chappy.interfaces;

import chappy.interfaces.cookies.IChappyCookie;

/**
 * Chappy message protocol interface.
 * @author Gabriel Dimitriu
 *
 */
public interface IChappyProtocol {

	/**
	 * get the reply message from the chappy.
	 * @return the replyMessage
	 */
	String getReplyMessage();

	/**
	 * set the reply message from chappy.
	 * @param replyMessage the replyMessage to set
	 */
	void setReplyMessage(String replyMessage);

	/**
	 * @return the exception
	 */
	Exception getException();

	/**
	 * @param exception the exception to set
	 */
	void setException(Exception exception);

	/**
	 * set the cookie that correspond to this login
	 * @param cookie that correspond to this login.
	 */
	void setCookie(IChappyCookie cookie);

	/**
	 * get the cookie for this login.
	 * @return cookie for this login.
	 */
	IChappyCookie getCookie();

	/**
	 * query if it has exception coming from chappy.
	 * @return true if has exception from chappy.
	 */
	boolean hasException();

}