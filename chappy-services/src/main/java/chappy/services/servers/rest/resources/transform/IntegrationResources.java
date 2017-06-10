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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import chappy.interfaces.cookies.CookieTransactionsToken;
import chappy.interfaces.flows.IFlowRunner;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.providers.flow.runners.TransformersFlowRunnerProvider;
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
		Cookie cookie = cookies.get("userData");
		ObjectReader or=new ObjectMapper().readerFor(CookieTransactionsToken.class);
    	CookieTransactionsToken received = new CookieTransactionsToken();
    	String str=new String(Base64.getDecoder().decode(cookie.getValue().getBytes()));
    	received=or.readValue(str);
		
    	List<FormDataBodyPart> bodyParts = multipart.getFields("data");
    	List<InputStream> inputValues = new ArrayList<InputStream>();
    	for (FormDataBodyPart bodyPart : bodyParts) {
    		inputValues.add(bodyPart.getEntityAs(InputStream.class));
    	}
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
		
		
		IFlowRunner runner = TransformersFlowRunnerProvider.getInstance()
				.createFlowRunner("StaticFlow", configurationStream, multipart, queryParams);
		runner.createSteps(received.getUserName());
		runner.executeSteps(holders);
		if (holders.size() > 1) {
			FormDataMultiPart multipartEntity = new FormDataMultiPart();
			for (StreamHolder holder : holders) {
				multipartEntity = multipartEntity.field("data",
						StreamUtils.toStringFromStream(holder.getInputStream()));
			}
			return Response.ok().entity(multipartEntity).cookie(new NewCookie(cookie)).build();
		}
		
		ByteArrayInputStreamWrapper inputStream = holders.get(0).getInputStream();
		RestStreamingOutput stream = new RestStreamingOutput(inputStream.getBuffer(), 0, inputStream.size());
		return Response.ok().entity(stream).cookie(new NewCookie(cookie)).build();
	}

}
