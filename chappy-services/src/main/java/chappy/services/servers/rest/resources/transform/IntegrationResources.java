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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.flows.IFlowRunner;
import chappy.interfaces.flows.MultiDataQueryHolder;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.services.IChappyServiceNamesConstants;
import chappy.policy.cookies.CookieUtils;
import chappy.providers.flow.runners.TransformersFlowRunnerProvider;
import chappy.providers.services.RESTtoInternalWrapper;
import chappy.utils.streams.StreamUtils;
import chappy.utils.streams.rest.RestStreamingOutput;
import chappy.utils.streams.wrappers.ByteArrayInputStreamWrapper;
import chappy.utils.streams.wrappers.ByteArrayOutputStreamWrapper;
import chappy.utils.streams.wrappers.StreamHolder;
import chappy.utils.streams.wrappers.WrapperUtils;

/**
 * Class which contains the resources need by integration broker requests. 
 * @author Gabriel Dimitriu
 *
 */
@Path(IRestPathConstants.PATH_TO_INTEGRATION)
public class IntegrationResources {

	/**
	 * 
	 */
	public IntegrationResources() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * request to run a integration flow with multiple input-output elements.
	 * @param multipart input data
	 * @param uriInfo
	 * @param hh response
	 * @return
	 * @throws Exception
	 */
	@Path(IRestResourcesConstants.REST_FLOW)
	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response runFlowIntegration(final FormDataMultiPart multipart,
			@Context final UriInfo uriInfo, @Context final HttpHeaders hh) throws Exception {
		
		Map<String, Cookie> cookies = hh.getCookies();
		Cookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		
		IChappyCookie received = CookieUtils.decodeCookie(cookie);
		
    	List<FormDataBodyPart> bodyParts = multipart.getFields(IChappyServiceNamesConstants.INPUT_DATA);
    	List<InputStream> inputValues = new ArrayList<InputStream>();
    	for (FormDataBodyPart bodyPart : bodyParts) {
    		inputValues.add(bodyPart.getEntityAs(InputStream.class));
    	}
		InputStream configurationStream = null;
		String configuration = null;
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); 
		if (queryParams != null) {
			configuration = queryParams.getFirst(IChappyServiceNamesConstants.CONFIGURATION);
		}
		if (configuration == null || "".equals(configuration)) {
			configurationStream = multipart.getField(IChappyServiceNamesConstants.CONFIGURATION).getEntityAs(InputStream.class);
		} else {
			configurationStream = new ByteArrayInputStream(configuration.getBytes());
		}
		
		/* create the list of input stream holders */
		List<StreamHolder> holders = new ArrayList<StreamHolder>();
		for (InputStream input : inputValues) {
			ByteArrayOutputStreamWrapper bos = WrapperUtils.fromInputStreamToOutputWrapper(input);
		
			holders.add(new StreamHolder(new ByteArrayInputStreamWrapper(bos.getBuffer(), 0, bos.size())));
			try {
				bos.close();
			} catch (IOException e) {
			}
		}
		
		MultiDataQueryHolder multiData = RESTtoInternalWrapper.RESTtoInternal(multipart, queryParams);
		
		IFlowRunner runner = TransformersFlowRunnerProvider.getInstance()
				.createFlowRunner(IChappyServiceNamesConstants.STATIC_FLOW, configurationStream, multiData);
		runner.createSteps(received);
		runner.executeSteps(holders);
		if (holders.size() > 1) {
			List<String> retList = new ArrayList<String>();
			for (StreamHolder holder : holders) {
				retList.add(StreamUtils.toStringFromStream(holder.getInputStream()));
			}
			GenericEntity<List<String>> returnList = new GenericEntity<List<String>>(retList){};
			return Response.ok().type(MediaType.APPLICATION_JSON).entity(returnList).cookie(new NewCookie(cookie)).build();
		}
		
		ByteArrayInputStreamWrapper inputStream = holders.get(0).getInputStream();
		RestStreamingOutput stream = new RestStreamingOutput(inputStream.getBuffer(), 0, inputStream.size());
		return Response.ok().entity(stream).cookie(new NewCookie(cookie)).build();
	}

}
