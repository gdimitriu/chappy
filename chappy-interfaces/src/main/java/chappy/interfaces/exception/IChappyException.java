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

import java.util.List;

import javax.ws.rs.core.Response;

/**
 * Exception base class for exception mapping.
 * @author Gabriel Dimitriu
 *
 */
public interface IChappyException {

	/**
	 * convert the exception to http response
	 * @return Response to be send to the customer.
	 */
	default public Response toResponse() {
		return Response.ok().build();
	}

	/**
	 * get the wrappers which this implemented
	 * @return array of classes.
	 */
	public List<Class<?>> isWrapperFor();
	
	
	/**
	 * set the localized message for this exception.
	 * @param messagess
	 */
	public void setLocalizedMessage(final String message);
}
