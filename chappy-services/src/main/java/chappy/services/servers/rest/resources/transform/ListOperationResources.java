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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.providers.transformers.DefaultStepProvider;

/**
 * @author Gabriel Dimitriu
 *
 */
@Path(IRestPathConstants.PATH_TO_LIST_SYSTEM_STEPS)
public class ListOperationResources {

	@Context
	UriInfo uriInfo;
	
	/**
	 * 
	 */
	public ListOperationResources() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * list the system provided steps
	 * @param uriInfo
	 * @return list of provided steps
	 */
	@Path(IRestResourcesConstants.REST_DEFAULT)
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response listDefaultTransformers(@Context UriInfo uriInfo) {
		List<String> listOfSteps = DefaultStepProvider.getInstance().getDefaultSteps();
//		@SuppressWarnings("resource")
//		FormDataMultiPart multipartEntity = new FormDataMultiPart();
//		for (String step : listOfSteps) {
//			multipartEntity = multipartEntity.field("data", step);
//		}
//		return Response.ok().entity(multipartEntity).build();
		GenericEntity<List<String>> returnList = new GenericEntity<List<String>>(listOfSteps){};
		return Response.ok().entity(returnList).build();
	}
	
	/**
	 * list the all steps that exists in this system except the transactional ones 
	 * @param uriInfo
	 * @return list of all existing steps.
	 */
	@Path(IRestResourcesConstants.REST_ALL)
	@Produces(MediaType.MULTIPART_FORM_DATA)
	@GET
	public Response listAllTransformers(@Context UriInfo uriInfo) {
		return Response.ok().build();
	}
}
