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
package chappy.services.servers.rest.resources.transform;

import java.util.Base64;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.services.IChappyServiceNamesConstants;
import chappy.persistence.providers.CustomTransformerStorageProvider;

/**
 * @author Gabriel Dimitriu
 *
 */
@Path(IRestPathConstants.PATH_TO_ADD_TRANSFORMER_TO_FLOW)
public class AddTransformResources {

	@Context
	UriInfo uriInfo;
	/**
	 * 
	 */
	public AddTransformResources() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * post transformer resources
	 * @param multipart
	 * @param uriInfo
	 * @return response with cookie used for the put operations.
	 * @throws Exception
	 */
	@Path(IRestResourcesConstants.REST_TRANSFORMER)
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response pushTransformer(final FormDataMultiPart multipart,
			@Context UriInfo uriInfo) throws Exception {
		String transformerName = multipart.getField(IChappyServiceNamesConstants.TRANSFORMER_NAME).getValue();
		byte[] transformerData = Base64.getDecoder().decode(multipart
				.getField(IChappyServiceNamesConstants.TRANSFORMER_DATA).getValue());
		CustomTransformerStorageProvider.getInstance().pushNewTransformer(transformerName, transformerData);
		return Response.ok().build();
	}
	
	/**
	 * post transformer resources for a specific user
	 * @param multipart
	 * @param user
	 * @param uriInfo
	 * @return response with cookie used for the put operations.
	 * @throws Exception
	 */
	@Path(IRestResourcesConstants.REST_TRANSFORMER_BY_USER)
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response pushTransformer(final FormDataMultiPart multipart,
			@QueryParam(IChappyServiceNamesConstants.LOGIN_USER) final String userName,
			@Context UriInfo uriInfo) throws Exception {
		String transformerName = multipart.getField(IChappyServiceNamesConstants.TRANSFORMER_NAME).getValue();
		byte[] transformerData = Base64.getDecoder().decode(multipart
				.getField(IChappyServiceNamesConstants.TRANSFORMER_DATA).getValue());
		CustomTransformerStorageProvider.getInstance().pushNewUserTransformer(userName, transformerName, transformerData);
		return Response.ok().build();
	}
}
