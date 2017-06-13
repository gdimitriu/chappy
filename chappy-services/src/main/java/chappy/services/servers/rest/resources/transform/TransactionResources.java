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
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import chappy.interfaces.cookies.CookieTransactionsToken;
import chappy.interfaces.flows.IFlowRunner;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.transactions.ITransaction;
import chappy.providers.authentication.SystemPolicyProvider;
import chappy.providers.flow.runners.TransformersFlowRunnerProvider;
import chappy.providers.transaction.TransactionProviders;
import chappy.providers.transformers.custom.CustomTransformerStorageProvider;
import chappy.transaction.base.Transaction;
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
	 */
	@Path(IRestResourcesConstants.REST_LOGIN)
	@GET
	public Response login(@QueryParam("user") final String userName, @QueryParam("password") final String password, 
			@QueryParam("persist") final boolean persistence) {
		
		if (!SystemPolicyProvider.getInstance().getAuthenticationHandler().isAuthenticate(userName, password)) {
			return Response.status(Status.FORBIDDEN).build();
		}
		CookieTransactionsToken response = new CookieTransactionsToken();
		response.setUserName(userName);
		
		ITransaction transaction = new Transaction();
		
		boolean allowedPersistence = SystemPolicyProvider.getInstance().getAuthenticationHandler().isAllowedPersistence(userName);
		if (persistence != allowedPersistence) {
			if (persistence) {
				return Response.status(Status.FORBIDDEN).build();
			}
		}
		
		transaction.setPersistence(persistence);
		
		TransactionProviders.getInstance().putTransaction(response, transaction);
		
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    	String json;
		try {
			json = ow.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			return Response.status(Status.FORBIDDEN).build();
		}
    	byte[] base64json=Base64.getEncoder().encode(json.getBytes());
		NewCookie cookie = new NewCookie("userData", new String(base64json));
		return Response.ok().cookie(cookie).build();
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
	public Response logout(@QueryParam("getStatistics") final boolean getStatistics,
			@Context UriInfo uriInfo, @Context HttpHeaders hh) throws Exception {
		Map<String, Cookie> cookies = hh.getCookies();
		Cookie cookie = cookies.get("userData");
		ObjectReader or=new ObjectMapper().readerFor(CookieTransactionsToken.class);
    	CookieTransactionsToken received = new CookieTransactionsToken();
    	String str=new String(Base64.getDecoder().decode(cookie.getValue().getBytes()));
    	received=or.readValue(str);
    	
    	ITransaction transaction = TransactionProviders.getInstance().getTransaction(received);
    	List<String> listOfTransformers = transaction.getListOfCustomTansformers();
    	CustomTransformerStorageProvider.getInstance().removeTransformers(received.getUserName(), listOfTransformers);
    	
    	TransactionProviders.getInstance().removeTransaction(received);
    	
    	return Response.ok().build();
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
		Cookie cookie = cookies.get("userData");
		ObjectReader or=new ObjectMapper().readerFor(CookieTransactionsToken.class);
    	CookieTransactionsToken received = new CookieTransactionsToken();
    	String str=new String(Base64.getDecoder().decode(cookie.getValue().getBytes()));
    	received=or.readValue(str);
    	
		String transformerName = multipart.getField("name").getValue();
		byte[] transformerData = Base64.getDecoder().decode(multipart
				.getField("data").getValue());
		
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
		Cookie cookie = cookies.get("userData");
		ObjectReader or=new ObjectMapper().readerFor(CookieTransactionsToken.class);
    	CookieTransactionsToken received = new CookieTransactionsToken();
    	String str=new String(Base64.getDecoder().decode(cookie.getValue().getBytes()));
    	received=or.readValue(str);
    	
		InputStream inputValue = multipart.getField("data").getEntityAs(InputStream.class);
		InputStream configurationStream = null;
		String configuration = null;
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); 
		if (queryParams != null) {
			configuration = queryParams.getFirst("configuration");
		}
		if (configuration == null || "".equals(configuration)) {
			configurationStream = multipart.getField("configuration").getEntityAs(InputStream.class);
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
		
		IFlowRunner runner = TransformersFlowRunnerProvider.getInstance()
				.createFlowRunner("StaticFlow", configurationStream, multipart, queryParams);
		runner.createSteps(received.getUserName());
		runner.executeSteps(holder);
		
		ByteArrayInputStreamWrapper inputStream = holder.getInputStream();
		RestStreamingOutput stream = new RestStreamingOutput(inputStream.getBuffer(), 0, inputStream.size());
		return Response.ok().entity(stream).cookie(new NewCookie(cookie)).build();
	}
	
	/**
	 * get the list of added steps in this transaction.
	 * @param uriInfo
	 * @param hh
	 * @return list of added step in this transaction
	 */
	@Path(IRestResourcesConstants.REST_LIST)
	@GET
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response listSteps(@Context final UriInfo uriInfo, @Context final HttpHeaders hh) {
		return Response.ok().build();
	}
}
