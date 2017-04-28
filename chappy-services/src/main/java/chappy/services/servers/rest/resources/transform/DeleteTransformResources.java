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

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.providers.transformers.custom.CustomTransformerStorageProvider;

/**
 * resources for delete transformers
 * @author Gabriel Dimitriu
 *
 */
@Path(IRestPathConstants.PATH_TO_DELETE_TRANSFORMER_TO_FLOW)
public class DeleteTransformResources {

	@Context
	UriInfo uriInfo;
	/**
	 * 
	 */
	public DeleteTransformResources() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * delete transformer resources
	 * @param transformer
	 * @param uriInfo
	 * @return response
	 * @throws Exception
	 */
	@Path(IRestResourcesConstants.REST_TRANSFORMER)
	@DELETE
	public Response pushTransformer(final @QueryParam("transformer") String transformerName,
			@Context UriInfo uriInfo) throws Exception {
		boolean status = CustomTransformerStorageProvider.getInstance().removeTransformer(transformerName);
		if (status) {
			return Response.ok().build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	/**
	 * delete transformer resources for a specific user
	 * @param user name
	 * @param transformer transformer name
	 * @return response 
	 * @throws Exception
	 */
	@Path(IRestResourcesConstants.REST_TRANSFORMER_BY_USER)
	@DELETE
	public Response pushTransformer(@QueryParam("transformer") final String transformerName,
			@QueryParam("user") final String userName,
			@Context UriInfo uriInfo) throws Exception {
		boolean status = CustomTransformerStorageProvider.getInstance().removeTransformer(userName, transformerName);
		if (status) {
			return Response.ok().build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
}
