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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * exception for class intantion problem (not existing class).
 * This is tipical to forget to post new client classes.
 * @author Gabriel Dimitriu
 *
 */
public class NotExistingClassException extends Exception implements IChappyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -276710001522187247L;
	
	/** localized message */
	private String localizedMessage = null;

	/**
	 * 
	 */
	public NotExistingClassException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public NotExistingClassException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public NotExistingClassException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotExistingClassException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public NotExistingClassException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.exception.IChappyException#isWrapperFor()
	 */
	@Override
	public List<Class<?>> isWrapperFor() {
		List<Class<?>> returnList = new ArrayList<Class<?>>();
		returnList.add(InstantiationException.class);
		returnList.add(ClassNotFoundException.class);
		returnList.add(NoClassDefFoundError.class);
		return returnList;
	}

	/**
	 * convert to response the exception.
	 */
	@Override
	public Response toResponse() {
		String messageCause = getLocalizedMessage();
		if (messageCause == null && getCause() != null) {
			messageCause = getCause().toString();
		}
		if (localizedMessage != null) {
			messageCause = localizedMessage + ":" + messageCause;
		}
		return Response.status(Status.PRECONDITION_FAILED).entity(messageCause).type(MediaType.TEXT_PLAIN).build();
	}

	@Override
	public void setLocalizedMessage(final String message) {
		this.localizedMessage = message;
	}
}
