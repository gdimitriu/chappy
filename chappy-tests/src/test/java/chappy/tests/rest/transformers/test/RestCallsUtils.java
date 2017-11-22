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
package chappy.tests.rest.transformers.test;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.services.IChappyServiceNamesConstants;
import chappy.tests.utils.ClassUtils;

/**
 * @author Gabriel Dimitriu
 *
 */
public final class RestCallsUtils {

	static final public String CUSTOM_TRANSFORMERS_DUMMY = "chappy.tests.rest.transformers.dummy";

	private RestCallsUtils() {
		
	}
	
	/**
	 * @param target
	 * @param cookie
	 * @return
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("resource")
	public static Response addPrePostProcessingSteps(WebTarget target, NewCookie cookie) throws FileNotFoundException {
		Response response;
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field(IChappyServiceNamesConstants.TRANSFORMER_NAME, "PreProcessingStep")
				.field(IChappyServiceNamesConstants.TRANSFORMER_DATA, new ClassUtils()
						.getClassAsString("PreProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", Status.OK.getStatusCode(), response.getStatus());
		cookie = response.getCookies().get("userData");
		multipartEntity = new FormDataMultiPart()
				.field(IChappyServiceNamesConstants.TRANSFORMER_NAME, "PostProcessingStep")
				.field(IChappyServiceNamesConstants.TRANSFORMER_DATA, new ClassUtils()
						.getClassAsString("PostProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", Status.OK.getStatusCode(), response.getStatus());
		cookie = response.getCookies().get("userData");
		multipartEntity = new FormDataMultiPart()
				.field(IChappyServiceNamesConstants.TRANSFORMER_NAME, "ProcessingStep")
				.field(IChappyServiceNamesConstants.TRANSFORMER_DATA, new ClassUtils()
						.getClassAsString("ProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", Status.OK.getStatusCode(), response.getStatus());
		return response;
	}
}
