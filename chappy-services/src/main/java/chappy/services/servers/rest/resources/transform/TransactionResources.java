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
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.fasterxml.jackson.core.JsonProcessingException;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.exception.ForbiddenException;
import chappy.interfaces.flows.IFlowRunner;
import chappy.interfaces.flows.MultiDataQueryHolder;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.services.IChappyServiceNamesConstants;
import chappy.interfaces.statisticslogs.IStatistics;
import chappy.interfaces.statisticslogs.StatisticLog;
import chappy.interfaces.transactions.ITransaction;
import chappy.policy.cookies.CookieUtils;
import chappy.providers.flow.runners.TransformersFlowRunnerProvider;
import chappy.providers.services.RESTtoInternalWrapper;
import chappy.providers.transaction.StatisticsLogsProvider;
import chappy.providers.transaction.TransactionProviders;
import chappy.services.servers.common.TransactionOperations;
import chappy.utils.streams.StreamUtils;
import chappy.utils.streams.rest.RestStreamingOutput;
import chappy.utils.streams.wrappers.ByteArrayInputStreamWrapper;
import chappy.utils.streams.wrappers.ByteArrayOutputStreamWrapper;
import chappy.utils.streams.wrappers.StreamHolder;
import chappy.utils.streams.wrappers.WrapperUtils;

/**
 * transaction operations for users
 * @author Gabriel Dimitriu
 *
 */
@Path(IRestPathConstants.PATH_TO_TRANSACTION)
public class TransactionResources {
	
	@Context
	UriInfo uriInfo;

	/**
	 * 
	 */
	public TransactionResources() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * authenticate the user to the system.
	 * @param userName the user
	 * @param password password for the user in base64
	 * @param persist true if the user want's persistence
	 * @return http response plus cookie
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@Path(IRestResourcesConstants.REST_LOGIN)
	@GET
	public Response login(@QueryParam(IChappyServiceNamesConstants.LOGIN_USER) final String userName, @QueryParam(IChappyServiceNamesConstants.LOGIN_PASSWORD) final String password, 
			@QueryParam(IChappyServiceNamesConstants.PERSIST) final boolean persistence){
		
		IChappyCookie response = null;
			try {
				response = TransactionOperations.login(this.getClass(), userName, password, persistence, null);
			} catch (ForbiddenException e1) {
				e1.printStackTrace();
				Response.status(Status.FORBIDDEN).build();
			}
		
		if (response == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		try {
			return Response.ok().cookie(CookieUtils.encodeCookie(response)).build();
		} catch (JsonProcessingException e) {
			return Response.status(Status.FORBIDDEN).build();
		}
	}
	
	/**
	 * logout from the system.
	 * @param getStatistics return the statistics if the customer request.
	 * @param uriInfo
	 * @param hh
	 * @return
	 * @throws Exception
	 */
	@Path(IRestResourcesConstants.REST_LOGOUT)
	@GET
	public Response logout(@QueryParam(IChappyServiceNamesConstants.GET_STATISTICS) final boolean getStatistics,
			@Context UriInfo uriInfo, @Context HttpHeaders hh) throws Exception {
		Map<String, Cookie> cookies = hh.getCookies();
		Cookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		IChappyCookie received = CookieUtils.decodeCookie(cookie);
    	
		if (TransactionOperations.logout(received) != null) {
			return Response.ok().build();
		} else {
			return Response.status(Status.FORBIDDEN).build();
		}
	}
	
	/**
	 * add a transformer to the flow.
	 * @param multipart
	 * @param uriInfo
	 * @param hh
	 * @return response with same cookie
	 * @throws Exception
	 */
	@Path(IRestResourcesConstants.REST_ADD + "/" + IRestResourcesConstants.REST_TRANSFORMER)
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response addTransformer(final FormDataMultiPart multipart,
			@Context UriInfo uriInfo, @Context HttpHeaders hh) throws Exception {
		Map<String, Cookie> cookies = hh.getCookies();
		Cookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		IChappyCookie received = CookieUtils.decodeCookie(cookie);
    	
		String transformerName = multipart.getField(IChappyServiceNamesConstants.TRANSFORMER_NAME).getValue();
		byte[] transformerData = Base64.getDecoder().decode(multipart
				.getField(IChappyServiceNamesConstants.TRANSFORMER_DATA).getValue());
		
		ITransaction transaction = TransactionProviders.getInstance().getTransaction(received);
		transaction.addTransformer(received.getUserName(), transformerName, transformerData);		

		
		return Response.ok().cookie(new NewCookie(cookie)).build();
	}
	
	/**
	 * Transform a stream into an output stream using flow definition.
	 * @param multipart multipart input which contains xsl, data and configuration
	 * @param uriInfo contains query params for xsl or configuration
	 * @return http response
	 * @throws Exception 
	 */
	@Path(IRestResourcesConstants.REST_TRANSFORM + "/" + IRestResourcesConstants.REST_FLOW)
	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response processDataStreamFlow(final FormDataMultiPart multipart,
			@Context final UriInfo uriInfo, @Context final HttpHeaders hh) throws Exception {
		
		Map<String, Cookie> cookies = hh.getCookies();
		Cookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		IChappyCookie received = CookieUtils.decodeCookie(cookie);
    	
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
		runner.createSteps(received);
		runner.executeSteps(holder);
		
		ByteArrayInputStreamWrapper inputStream = holder.getInputStream();
		RestStreamingOutput stream = new RestStreamingOutput(inputStream.getBuffer(), 0, inputStream.size());
		return Response.ok().entity(stream).cookie(new NewCookie(cookie)).build();
	}
	
	/**
	 * request to run a flow with multiple input-output elements.
	 * @param multipart input data
	 * @param uriInfo
	 * @param hh response
	 * @return
	 * @throws Exception
	 */
	@Path(IRestResourcesConstants.REST_TRANSFORM + "/" + IRestResourcesConstants.REST_FLOW_MULTI)
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
		List<String> retList = new ArrayList<String>();
		for (StreamHolder holder : holders) {
			retList.add(StreamUtils.toStringFromStream(holder.getInputStream()));
		}
		GenericEntity<List<String>> returnList = new GenericEntity<List<String>>(retList){};
		return Response.ok().type(MediaType.APPLICATION_JSON).entity(returnList).cookie(new NewCookie(cookie)).build();
	}
	
	/**
	 * get the list of added steps in this transaction.
	 * @param uriInfo
	 * @param hh
	 * @return list of added step in this transaction
	 */
	@Path(IRestResourcesConstants.REST_LIST)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listSteps(@Context final UriInfo uriInfo, @Context final HttpHeaders hh) throws Exception {
		Map<String, Cookie> cookies = hh.getCookies();
		Cookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		IChappyCookie receivedCookie = CookieUtils.decodeCookie(cookie);
		ITransaction transaction = TransactionProviders.getInstance().getTransaction(receivedCookie);
		List<String> listOfSteps = transaction.getListOfCustomTansformers();
		GenericEntity<List<String>> returnList = new GenericEntity<List<String>>(listOfSteps){};
		return Response.ok().entity(returnList).cookie(new NewCookie(cookie)).build();
	}

	/**
	 * get the list of statistics of this transaction.
	 * @param uriInfo
	 * @param hh
	 * @return list of statistics of this transaction
	 */
	@Path(IRestResourcesConstants.REST_STATISTICS)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatistics(@Context final UriInfo uriInfo, @Context final HttpHeaders hh) throws Exception {
		Map<String, Cookie> cookies = hh.getCookies();
		Cookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		IChappyCookie receivedCookie = CookieUtils.decodeCookie(cookie);
		IStatistics statistics = StatisticsLogsProvider.getInstance().getStatistics(receivedCookie);
		if (statistics != null) {
			List<StatisticLog> listOfStatistics = statistics.getAllStatistics();
			GenericEntity<List<StatisticLog>> returnList = new GenericEntity<List<StatisticLog>>(listOfStatistics){};
			return Response.ok().entity(returnList).cookie(new NewCookie(cookie)).build();			
		} else {
			return Response.ok().cookie(new NewCookie(cookie)).build();
		}

	}
	
	/**
	 * Add a flow to the cache of transaction by name.
	 * @param multipart multipart input which contains xsl and configuration
	 * @param uriInfo contains query params for xsl or configuration
	 * @return http response
	 * @throws Exception 
	 */
	@Path(IRestResourcesConstants.REST_ADD + "/" + IRestResourcesConstants.REST_FLOW)
	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response addFlow(final FormDataMultiPart multipart,
			@Context final UriInfo uriInfo, @Context final HttpHeaders hh) throws Exception {
		
		Map<String, Cookie> cookies = hh.getCookies();
		Cookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		IChappyCookie received = CookieUtils.decodeCookie(cookie);

		InputStream configurationStream = null;
		String configuration = null;
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); 
		if (queryParams != null) {
			configuration = queryParams.getFirst(IChappyServiceNamesConstants.CONFIGURATION);
		} else {
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		if (!queryParams.containsKey(IChappyServiceNamesConstants.CHAPPY_FLOW_NAME)) {
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		if (configuration == null || "".equals(configuration)) {
			configurationStream = multipart.getField(IChappyServiceNamesConstants.CONFIGURATION).getEntityAs(InputStream.class);
		} else {
			configurationStream = new ByteArrayInputStream(configuration.getBytes());
		}
		MultiDataQueryHolder multiData = RESTtoInternalWrapper.RESTtoInternal(multipart, queryParams);
		IFlowRunner runner = TransformersFlowRunnerProvider.getInstance()
				.createFlowRunner(IChappyServiceNamesConstants.STATIC_FLOW, configurationStream, multiData);

		runner.createSteps(received);
		
		String flowName = queryParams.getFirst(IChappyServiceNamesConstants.CHAPPY_FLOW_NAME);
		ITransaction transaction = TransactionProviders.getInstance().getTransaction(received);
		transaction.putFlowRunner(flowName, runner);

		return Response.ok().cookie(new NewCookie(cookie)).build();
	}
	
	/**
	 * Transform a stream into an output stream using flow definition with multiple inputs.
	 * @param multipart multipart input which contains xsl, data and configuration
	 * @param uriInfo contains query params for xsl or configuration
	 * @return http response
	 * @throws Exception 
	 */
	@Path(IRestResourcesConstants.REST_FLOW)
	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response processDataStreamExistingFlow(final FormDataMultiPart multipart,
			@Context final UriInfo uriInfo, @Context final HttpHeaders hh) throws Exception {
		
		Map<String, Cookie> cookies = hh.getCookies();
		Cookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		IChappyCookie received = CookieUtils.decodeCookie(cookie);
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		if (!queryParams.containsKey(IChappyServiceNamesConstants.CHAPPY_FLOW_NAME)) {
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
    	
		InputStream inputValue = multipart.getField(IChappyServiceNamesConstants.INPUT_DATA).getEntityAs(InputStream.class);
		
		ByteArrayOutputStreamWrapper bos = WrapperUtils.fromInputStreamToOutputWrapper(inputValue);
		
		StreamHolder holder = new StreamHolder(new ByteArrayInputStreamWrapper(bos.getBuffer(), 0, bos.size()));
		try {
			bos.close();
		} catch (IOException e) {
		}
		
		bos = null;
		MultiDataQueryHolder multiData = RESTtoInternalWrapper.RESTtoInternal(multipart, queryParams);
		String flowName = queryParams.getFirst(IChappyServiceNamesConstants.CHAPPY_FLOW_NAME);
		ITransaction transaction = TransactionProviders.getInstance().getTransaction(received);
		transaction.getFlowRunner(flowName).executeSteps(holder, multiData);
		
		ByteArrayInputStreamWrapper inputStream = holder.getInputStream();
		RestStreamingOutput stream = new RestStreamingOutput(inputStream.getBuffer(), 0, inputStream.size());
		return Response.ok().entity(stream).cookie(new NewCookie(cookie)).build();
	}
	
	/**
	 * request to run a flow with multiple input-output elements.
	 * @param multipart input data
	 * @param uriInfo
	 * @param hh response
	 * @return
	 * @throws Exception
	 */
	@Path(IRestResourcesConstants.REST_FLOW_MULTI)
	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response runExistingFlowIntegration(final FormDataMultiPart multipart,
			@Context final UriInfo uriInfo, @Context final HttpHeaders hh) throws Exception {
		
		Map<String, Cookie> cookies = hh.getCookies();
		Cookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		
		IChappyCookie received = CookieUtils.decodeCookie(cookie);

		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		if (!queryParams.containsKey(IChappyServiceNamesConstants.CHAPPY_FLOW_NAME)) {
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		
    	List<FormDataBodyPart> bodyParts = multipart.getFields(IChappyServiceNamesConstants.INPUT_DATA);
    	List<InputStream> inputValues = new ArrayList<InputStream>();
    	for (FormDataBodyPart bodyPart : bodyParts) {
    		inputValues.add(bodyPart.getEntityAs(InputStream.class));
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

		String flowName = queryParams.getFirst(IChappyServiceNamesConstants.CHAPPY_FLOW_NAME);
		ITransaction transaction = TransactionProviders.getInstance().getTransaction(received);
		transaction.getFlowRunner(flowName).executeSteps(holders, multiData);
		
		List<String> retList = new ArrayList<String>();
		for (StreamHolder holder : holders) {
			retList.add(StreamUtils.toStringFromStream(holder.getInputStream()));
		}
		GenericEntity<List<String>> returnList = new GenericEntity<List<String>>(retList){};
		return Response.ok().type(MediaType.APPLICATION_JSON).entity(returnList).cookie(new NewCookie(cookie)).build();
	}
}
