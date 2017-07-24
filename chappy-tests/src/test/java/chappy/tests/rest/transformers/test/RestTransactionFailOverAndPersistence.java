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

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import chappy.configurations.providers.SystemConfigurationProvider;
import chappy.configurations.system.SystemConfiguration;
import chappy.interfaces.rest.LocalDateTimeContextResolver;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.services.IServiceServer;
import chappy.interfaces.statisticslogs.StatisticLog;
import chappy.persistence.providers.CustomTransformerStorageProvider;
import chappy.services.servers.rest.ServerJetty;
import chappy.tests.utils.ClassUtils;
import chappy.utils.streams.StreamUtils;

/**
 * Test classes for fail-over and persistence.
 * @author Gabriel Dimitriu
 *
 */
public class RestTransactionFailOverAndPersistence {

	private static final String CUSTOM_TRANSFORMERS_DUMMY = "chappy.tests.rest.transformers.dummy";
	
	private IServiceServer server = null;

	private int port = 0;

	private URI baseUri;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		SystemConfigurationProvider.getInstance().readSystemConfiguration(
				getClass().getClassLoader().getResourceAsStream("systemTestConfiguration.xml"));
		SystemConfiguration configuration = SystemConfigurationProvider.getInstance().getSystemConfiguration()
				.getFirstConfiguration();
		port = Integer.parseInt(configuration.getProperty());
		baseUri = UriBuilder.fromUri("{arg}").build(new String[] { "http://localhost:" + port + "/" }, false);
		server = new ServerJetty(port);
		Thread thread = new Thread() {
			public void run() {
				try {
					server.startServer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		thread.start();
		CustomTransformerStorageProvider.getInstance().cleanRepository();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		server.stopServer();
	}
	
	/**
	 * 
	 */
	public RestTransactionFailOverAndPersistence() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("resource")
	@Test
	public void push3CustomTransformersByTransactionAndMakeTransformationGetStatisticsBeforeRestart() throws Exception {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class)
				.register(JacksonJaxbJsonProvider.class)
				.register(LocalDateTimeContextResolver.class);
		WebTarget target = client.target(baseUri);
		
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGIN)
				.queryParam("user", "gdimitriu")
				.queryParam("password", "password")
				.queryParam("persist", "true")
				.request().get();
		
		assertEquals("wrong authentication", response.getStatus(), Status.OK.getStatusCode());
		
		Map<String, NewCookie> cookies = response.getCookies();
		
		NewCookie cookie = cookies.get("userData");
		
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field("name", "PreProcessingStep")
				.field("data", new ClassUtils().getClassAsString("PreProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", response.getStatus(), Status.OK.getStatusCode());
		cookie = response.getCookies().get("userData");
		multipartEntity = new FormDataMultiPart()
				.field("name", "PostProcessingStep")
				.field("data", new ClassUtils().getClassAsString("PostProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", response.getStatus(), Status.OK.getStatusCode());
		cookie = response.getCookies().get("userData");
		//stop and restart the server
		tearDown();
		setUp();
		CustomTransformerStorageProvider.getInstance().loadPersistenceCustomTransformers();
		
		multipartEntity = new FormDataMultiPart()
				.field("name", "ProcessingStep")
				.field("data", new ClassUtils().getClassAsString("ProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", response.getStatus(), Status.OK.getStatusCode());
		cookie = response.getCookies().get("userData");
		multipartEntity = new FormDataMultiPart()
				.field("data", "blabla");
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
					.path(IRestResourcesConstants.REST_TRANSFORM).path(IRestResourcesConstants.REST_FLOW)
					.queryParam("configuration", StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"))
					.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummyStepsResponse.txt"),
						StreamUtils.toStringFromStream(inputStream));
		}
		cookie = response.getCookies().get("userData");		
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_STATISTICS)
				.request().cookie(cookie).get();
		
		if (response.getStatus() >= 0) {
			List<StatisticLog> actual = response.readEntity(new GenericType<List<StatisticLog>>(){}); 
			for (StatisticLog stat : actual) {
				System.out.println(stat.getStepName() + " started at " + stat.getStartTime().toString() + " and finished at " + stat.getStopTime().toString());
			}
		}
		
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_LOGOUT).request().cookie(cookie).get();
	}

}