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
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;

import org.xml.sax.SAXException;

/**
 * Wrapper for JAXBException, SAXEception and UnmarshalException
 * @author Gabriel Dimitriu
 *
 */
public class MalformedXSDXMLException extends Exception implements IChappyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3268268614332665721L;
	
	/**
	 * standard constructor for init.
	 */
	public MalformedXSDXMLException() {
		
	}

	/**
	 * @param message
	 */
	public MalformedXSDXMLException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param exception
	 */
	public MalformedXSDXMLException(Throwable exception) {
		super(exception);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param exception
	 */
	public MalformedXSDXMLException(String message, Throwable exception) {
		super(message, exception);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Class<?>> isWrapperFor() {
		List<Class<?>> returnList = new ArrayList<Class<?>>();
		returnList.add(JAXBException.class);
		returnList.add(SAXException.class);
		returnList.add(UnmarshalException.class);
		return returnList;
	}
	
	@Override
	public Response toResponse() {
		String messageCause = getLocalizedMessage();
		if (messageCause == null && getCause() != null) {
			messageCause = getCause().toString();
		}
		return Response.status(Status.FORBIDDEN).entity(messageCause).type(MediaType.TEXT_PLAIN).build();
	}

	/* there is no localized message for malformed xsd */
	@Override
	public void setLocalizedMessage(String message) {
		// TODO Auto-generated method stub
	}

	/**
	 * get the message cause of this exception.
	 * @return message cause
	 */
	public String getMessageCause() {
		String messageCause = getLocalizedMessage();
		if (messageCause == null && getCause() != null) {
			messageCause = getCause().toString();
		}
		return messageCause;
	}
}
