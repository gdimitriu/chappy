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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import com.fasterxml.jackson.core.JsonProcessingException;

import chappy.clients.common.protocol.AbstractChappyTransformFlowMessage;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.services.IChappyServiceNamesConstants;
import chappy.policy.cookies.CookieUtils;
import chappy.utils.streams.StreamUtils;

/**
 * @author Gabriel Dimitriu
 *
 */
public class RESTTransformFlowMessage extends AbstractChappyTransformFlowMessage implements IRESTMessage {

	/** status of the REST transaction */
	private StatusType status = null;
	
	/**
	 * 
	 */
	public RESTTransformFlowMessage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param input
	 * @param config
	 */
	public RESTTransformFlowMessage(final String input, final String config) {
		super(input, config);
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

	@Override
	public Invocation encodeInboundMessage(final WebTarget target) throws JsonProcessingException {
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart();
		for (String str : getInputs()) {
			multipartEntity = multipartEntity.field(IChappyServiceNamesConstants.INPUT_DATA, str);
		}
		Invocation builder =target.path(IRestPathConstants.PATH_TO_INTEGRATION).path(IRestResourcesConstants.REST_FLOW)
				.queryParam(IChappyServiceNamesConstants.CONFIGURATION, getConfiguration())
				.request(new String[] { MediaType.MULTIPART_FORM_DATA }).cookie(CookieUtils.encodeCookie(getCookie()))
				.buildPut(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		return builder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void decodeReplyMessage(final Response response) {
		setStatus(response.getStatusInfo());
		List<String> outputs = new ArrayList<>();
		try {
			InputStream inputStream = response.readEntity(InputStream.class);
			outputs.add(StreamUtils.toStringFromStream(inputStream));			
		} catch (ProcessingException e) {
			outputs = response.readEntity(new ArrayList<String>().getClass());
		}
		setOutputs(outputs);
	}

}
