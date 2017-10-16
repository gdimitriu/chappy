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

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import chappy.interfaces.flows.IFlowRunner;
import chappy.interfaces.flows.MultiDataQueryHolder;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.services.IChappyServiceNamesConstants;
import chappy.providers.flow.runners.TransformersFlowRunnerProvider;
import chappy.providers.services.RESTtoInternalWrapper;
import chappy.utils.streams.rest.RestStreamingOutput;
import chappy.utils.streams.wrappers.ByteArrayInputStreamWrapper;
import chappy.utils.streams.wrappers.ByteArrayOutputStreamWrapper;
import chappy.utils.streams.wrappers.StreamHolder;
import chappy.utils.streams.wrappers.WrapperUtils;


/**
 * This hold the transformation entry point (the resource for the jersey REST server).
 * @author Gabriel Dimitriu
 *
 */
@Path(IRestPathConstants.PATH_TO_TRANSFORM)
public class TransformResources {

	@Context
	UriInfo uriInfo;
	
	/**
	 * 
	 */
	public TransformResources() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Transform a stream into an output stream using flow definition.
	 * @param multipart multipart input which contains xsl, data and configuration
	 * @param uriInfo contains query params for xsl or configuration
	 * @return http response
	 * @throws Exception 
	 */
	@Path(IRestResourcesConstants.REST_FLOW)
	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response processDataStreamFlow(final FormDataMultiPart multipart,
			@QueryParam(IChappyServiceNamesConstants.LOGIN_USER) final String userName,
			@Context UriInfo uriInfo) throws Exception {
		InputStream inputValue = multipart.getField(IChappyServiceNamesConstants.INPUT_DATA).getEntityAs(InputStream.class);
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
		ByteArrayOutputStreamWrapper bos = WrapperUtils.fromInputStreamToOutputWrapper(inputValue);
		
		StreamHolder holder = new StreamHolder(new ByteArrayInputStreamWrapper(bos.getBuffer(), 0, bos.size()));
		try {
			bos.close();
		} catch (IOException e) {
		}
		
		bos = null;
		
		MultiDataQueryHolder multiData = RESTtoInternalWrapper.RESTtoInternal(multipart, queryParams);
		
		IFlowRunner runner = TransformersFlowRunnerProvider.getInstance()
				.createFlowRunner(IChappyServiceNamesConstants.STATIC_FLOW, configurationStream, multiData);
		runner.createSteps(userName);
		runner.executeSteps(holder);
		
		ByteArrayInputStreamWrapper inputStream = holder.getInputStream();
		RestStreamingOutput stream = new RestStreamingOutput(inputStream.getBuffer(), 0, inputStream.size());
		return Response.ok().entity(stream).build();
	}

	/**
	 * Transform a stream into an output stream using digester in one step.
	 * @param multipart multipart input which contains xsl, data and configuration
	 * @param uriInfo contains query params for xsl or configuration
	 * @return http response
	 * @throws Exception 
	 */
	@Path(IRestResourcesConstants.REST_DIGESTER_FLOW)
	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response processDataStreamDigesterOneStep(final FormDataMultiPart multipart,
			@Context UriInfo uriInfo) throws Exception {
		
		InputStream inputValue = multipart.getField(IChappyServiceNamesConstants.INPUT_DATA).getEntityAs(InputStream.class);
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
		ByteArrayOutputStreamWrapper bos = WrapperUtils.fromInputStreamToOutputWrapper(inputValue);
		
		StreamHolder holder = new StreamHolder(new ByteArrayInputStreamWrapper(bos.getBuffer(), 0, bos.size()));
		try {
			bos.close();
		} catch (IOException e) {
		}
		
		bos = null;
		
		MultiDataQueryHolder multiData = RESTtoInternalWrapper.RESTtoInternal(multipart, queryParams);
		
		IFlowRunner runner = TransformersFlowRunnerProvider.getInstance()
				.createFlowRunner(IChappyServiceNamesConstants.DIGESTER_FLOW, configurationStream, multiData);
		runner.createSteps();
		StreamHolder output = runner.executeSteps(holder);
		ByteArrayInputStreamWrapper inputStream = output.getInputStream();
		RestStreamingOutput stream = new RestStreamingOutput(inputStream.getBuffer(), 0, inputStream.size());
		return Response.ok().entity(stream).build();
	}
	
	/**
	 * Transform a stream into an output stream using staxon.
	 * @param inputValue
	 * @param configuration
	 * @return http response
	 * @throws Exception 
	 */
	@Path(IRestResourcesConstants.REST_TRANSFORMER_STAXON)
	@PUT
	@Consumes({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON , MediaType.TEXT_XML})
	@Produces({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON , MediaType.TEXT_XML})
	public Response processDataStream(final InputStream inputValue,
			@QueryParam(IChappyServiceNamesConstants.MODE) final String mode,
			@QueryParam(IChappyServiceNamesConstants.CONFIGURATION) final String configuration) throws Exception {
		
		ByteArrayOutputStreamWrapper bos = WrapperUtils.fromInputStreamToOutputWrapper(inputValue);
		
		StreamHolder holder = new StreamHolder(new ByteArrayInputStreamWrapper(bos.getBuffer(), 0, bos.size()));
		try {
			bos.close();
		} catch (IOException e) {
		}
		
		bos = null;
		
		IFlowRunner runner = TransformersFlowRunnerProvider.getInstance()
				.createFlowRunner(IChappyServiceNamesConstants.STAXON_SIMPLE_FLOW, null, null);
		runner.configure(mode, configuration);
		StreamHolder result = runner.executeSteps(holder);
		if (result == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		ByteArrayInputStreamWrapper inputStream = holder.getInputStream();
		RestStreamingOutput stream = new RestStreamingOutput(inputStream.getBuffer(), 0, inputStream.size());
		return Response.ok().entity(stream).build();
	}
}
