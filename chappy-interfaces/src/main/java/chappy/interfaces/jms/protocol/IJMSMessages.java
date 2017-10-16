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
package chappy.interfaces.jms.protocol;

/**
 * @author Gabriel Dimitriu
 *
 */
public interface IJMSMessages {
	
	/** chappy request has been received OK */
	public static final String OK = "Chappy request had been received OK";
	
	/** reply not ready (chappy had not return the reply). */
	public static final String REPLY_NOT_READY = "Reply not ready from Chappy";
	
	/** communication error */
	public static final String COMMUNICATION_ERROR = "JMS Communication error";

	/** forbidden access or internal server error */
	public static final String FORBIDDEN = "Access forbidden or internal server error";

	/** internal server error for the server really internal problem */
	public static final String INTERNAL_SERVER_ERROR = "Internal server error";
}
