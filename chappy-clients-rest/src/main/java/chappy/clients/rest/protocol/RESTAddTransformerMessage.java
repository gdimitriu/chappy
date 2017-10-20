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

import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.fasterxml.jackson.core.JsonProcessingException;

import chappy.clients.common.protocol.AbstractChappyAddTransformerMessage;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.services.IChappyServiceNamesConstants;
import chappy.policy.cookies.CookieUtils;

/**
 * @author Gabriel Dimitriu
 *
 */
public class RESTAddTransformerMessage extends AbstractChappyAddTransformerMessage implements IRESTMessage {

	/** status of the REST transaction */
	private StatusType status = null;
	
	/**
	 * @param transformerName
	 */
	public RESTAddTransformerMessage(final String transformerName) {
		super(transformerName);
	}
	
	/**
	 * constructor for the clients which wish auto-init.
	 * @param transformerName
	 * @param transformerClassName
	 * @param packageName
	 * @throws IOException
	 */
	public RESTAddTransformerMessage(final String transformerName, final String transformerClassName, final String packageName) throws IOException {
		super(transformerName);
		setTransformerFromClassPath(transformerClassName, packageName);
	}

	/**
	 * @return the status
	 */
	public StatusType getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(final StatusType status) {
		this.status = status;
	}

	@Override
	public Invocation encodeInboundMessage(WebTarget target) throws JsonProcessingException {
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field(IChappyServiceNamesConstants.TRANSFORMER_NAME, getTransformerName())
				.field(IChappyServiceNamesConstants.TRANSFORMER_DATA, getTransformerData());
		Invocation builder = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(CookieUtils.encodeCookie(getCookie()))
				.buildPost(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		return builder;
	}

	@Override
	public void decodeReplyMessage(Response response) {
		// TODO Auto-generated method stub
		
	}
}
