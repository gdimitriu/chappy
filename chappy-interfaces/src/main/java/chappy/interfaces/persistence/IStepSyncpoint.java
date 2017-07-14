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
package chappy.interfaces.persistence;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import chappy.interfaces.cookies.CookieTransaction;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * Interface for the sync-point persistence.
 * This is used to save the state of the steps for fail-over.
 * Fail-over on rest is not yet implemented.
 * @author Gabriel Dimitriu
 *
 */
public interface IStepSyncpoint {

	/**
	 * set the step name.
	 * @param name of the step
	 */
	void setStepName(final String name);
	
	/**
	 * get the step name.
	 * @return step name
	 */
	String getStepName();
		
	/**
	 * set the cookie.
	 * @param cookie of this syncpoint
	 */
	void setCookie(final CookieTransaction cookie);
	
	/**
	 * get the cookie.
	 * @return cookie of this syncpoint
	 */
	CookieTransaction getCookie();
	
	/**
	 * set the serialized configuration of the step.
	 * @param configuration
	 */
	void setSerializedConfiguration(final String configuration);
	
	/**
	 * get the serialized configuration of the step
	 * @return serialized step configuration.
	 */
	String getSerializedConfiguration();
	
	/**
	 * set the input messages of the step
	 * @param messages list of streamholders
	 * @param multipart multipart
	 * @param queryParams query
	 */
	void setInput(final List<StreamHolder> messages, final FormDataMultiPart multipart,
			final MultivaluedMap<String, String> queryParams);
	
	
	/**
	 * get the input messages.
	 * @return input messages
	 */
	List<StreamHolder> getInputMessages();
	
	/**
	 * get the input FormDataMultiPart.
	 * @return FormDataMultiPart of input message
	 */
	FormDataMultiPart getInputMultiPart();
	
	/**
	 * get the query params of the input messages.
	 * @return query params of input messages
	 */
	MultivaluedMap<String, String> getQueryParams();
	
}
