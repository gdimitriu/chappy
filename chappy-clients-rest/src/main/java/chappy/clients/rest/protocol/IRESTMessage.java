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
package chappy.clients.rest.protocol;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import com.fasterxml.jackson.core.JsonProcessingException;
/**
 * 
 * @author Gabriel Dimitriu
 *
 */
public interface IRESTMessage {

	/**
	 * @param target
	 * @return
	 */
	Invocation encodeInboundMessage(final WebTarget target) throws JsonProcessingException;

	/**
	 * @param response
	 */
	void decodeReplyMessage(final Response response);

	/**
	 * @return the status
	 */
	StatusType getStatus();

	/**
	 * @param status the status to set
	 */
	void setStatus(final StatusType status);

}