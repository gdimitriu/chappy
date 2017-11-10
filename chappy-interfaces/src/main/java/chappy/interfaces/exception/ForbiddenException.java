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
package chappy.interfaces.exception;

/**
 * Forbidden Exception for chappy.
 * @author Gabriel Dimitriu
 *
 */
public class ForbiddenException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6638776595077106669L;
	
	private String chappyMessage = null; 

	/**
	 * 
	 */
	public ForbiddenException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message from chappy
	 * @param local (the localized local message eg: transformerName)
	 */
	public ForbiddenException(final String local, final String message) {
		super(message);
		this.chappyMessage = local;
	}
	
	/**
	 * @param message
	 */
	public ForbiddenException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ForbiddenException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ForbiddenException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return localized message
	 */
	public String getChapyMessage() {
		return this.chappyMessage;
	}
}
