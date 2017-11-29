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

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import com.fasterxml.jackson.core.JsonProcessingException;

import chappy.clients.common.protocol.AbstractChappyAddFlowMessage;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.services.IChappyServiceNamesConstants;
import chappy.policy.cookies.CookieUtils;

/**
 * Chappy add flow request protocol message implementation for REST.
 * @author Gabriel Dimitriu
 *
 */
public class RESTAddFlowMessage extends AbstractChappyAddFlowMessage implements IRESTMessage {

	/** status of the REST transaction */
	private StatusType status = null;
	
	/**
	 * 
	 */
	public RESTAddFlowMessage() {
		// TODO Auto-generated constructor stub
	}
	
	public RESTAddFlowMessage(final String flowName, final String flowConfiguration) {
		super(flowName, flowConfiguration);
	}
	
	/* (non-Javadoc)
	 * @see chappy.clients.rest.protocol.IRESTMessage#getStatus()
	 */
	@Override
	public StatusType getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see chappy.clients.rest.protocol.IRESTMessage#setStatus(javax.ws.rs.core.Response.StatusType)
	 */
	@Override
	public void setStatus(final StatusType status) {
		this.status = status;
	}
	
	/* (non-Javadoc)
	 * @see chappy.clients.rest.protocol.IRESTMessage#encodeInboundMessage(javax.ws.rs.client.WebTarget)
	 */
	@Override
	public Invocation encodeInboundMessage(final WebTarget target) throws JsonProcessingException {		
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field(IChappyServiceNamesConstants.CONFIGURATION, getFlowDefinition());
		Invocation builder  = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
					.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_FLOW)
					.queryParam(IChappyServiceNamesConstants.CHAPPY_FLOW_NAME, getFlowName())
					.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(CookieUtils.encodeCookie(getCookie()))
					.buildPut(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		
		return builder;
	}

	/* (non-Javadoc)
	 * @see chappy.clients.rest.protocol.IRESTMessage#decodeReplyMessage(javax.ws.rs.core.Response)
	 */
	@Override
	public void decodeReplyMessage(final Response response) {
		setStatus(response.getStatusInfo());
		//TODO: No response yet, it should be the package name or something like this.
	}
}
