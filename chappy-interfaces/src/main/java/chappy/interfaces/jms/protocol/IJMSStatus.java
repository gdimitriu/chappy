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
public interface IJMSStatus {

	/** ok status */
	public static final String OK = "OK";
	
	/** forbidden access status */
	public static final String FORBIDDEN = "Forbidden";
	
	/** reply not ready from chappy */
	public static final String REPLY_NOT_READY = "Reply not ready";
	
	/** internal server error or communication error */
	public static final String COMMUNICATION_SERVER_ERROR = "Communication Server error";
	
	/** precondition failed for the processing of flow */
	public static final String PRECONDITION_FAILED = "Precondition failed";
}
