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
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import chappy.configurations.system.SystemConfiguration;
import chappy.configurations.system.SystemConfigurations;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.services.IServiceServer;
import chappy.providers.transformers.custom.CustomTransformerStorageProvider;
import chappy.services.servers.rest.ServerJetty;
import chappy.tests.utils.TestUtils;
import chappy.utils.streams.StreamUtils;


/**
 * @author Gabriel Dimitriu
 *
 */
public class RestTrasactionFlowTransformationsTest {
	
	private static final String CUSTOM_TRANSFORMERS_DUMMY = "chappy.tests.rest.transformers.dummy";

	private IServiceServer server = null;
	
	private int port = 0;

	private URI baseUri;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		JAXBContext context = JAXBContext.newInstance(SystemConfigurations.class);
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(new StreamSource(
				getClass().getClassLoader().getResourceAsStream("SystemConfiguration.xsd")));
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		SystemConfiguration configuration = ((SystemConfigurations) unmarshaller
				.unmarshal(getClass().getClassLoader().getResourceAsStream("systemTestConfiguration.xml")))
				.getFirstConfiguration();
		port = Integer.parseInt(configuration.getProperty());
		baseUri = UriBuilder.fromUri("{arg}").build(new String[]{"http://localhost:"+ port + "/"},false);
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

	@SuppressWarnings("resource")
	@Test
	public void push3CustomTransformersByTransactionAndMakeTransformation() throws FileNotFoundException {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGIN)
				.queryParam("user", "gdimitriu")
				.queryParam("password", "password")
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
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
					.path(IRestResourcesConstants.REST_TRANSFORM).path(IRestResourcesConstants.REST_FLOW)
					.queryParam("configuration", StreamUtils.getStringFromResource("dummySteps.xml"))
					.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResource("dummyStepsResponse.txt"),
						StreamUtils.toStringFromStream(inputStream));
		}
		
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_LOGOUT).request().cookie(cookie).get();
				
	}
	
	@SuppressWarnings("resource")
	@Test
	public void pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs() throws FileNotFoundException {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGIN)
				.queryParam("user", "gdimitriu")
				.queryParam("password", "password")
				.request().get();
		
		assertEquals("wrong authentication", response.getStatus(), Status.OK.getStatusCode());
		
		Map<String, NewCookie> cookies = response.getCookies();
		
		NewCookie cookie = cookies.get("userData");
		
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field("name", "EnveloperStep")
				.field("data", new ClassUtils().getClassAsString("EnveloperStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", response.getStatus(), Status.OK.getStatusCode());
		cookie = response.getCookies().get("userData");
		multipartEntity = new FormDataMultiPart()
				.field("data", StreamUtils.getStringFromResource("firstMessage.txt"))
				.field("data", StreamUtils.getStringFromResource("secondMessage.txt"));
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestPathConstants.PATH_TO_INTEGRATION)
					.path(IRestResourcesConstants.REST_FLOW)
					.queryParam("configuration", StreamUtils.getStringFromResource("basicEnveloperStep.xml"))
					.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResource("enveloperStepResponse.txt"),
						StreamUtils.toStringFromStream(inputStream));
		}
		
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_LOGOUT).request().cookie(cookie).get();
				
	}
	
	@SuppressWarnings("resource")
	@Test
	public void pushCustomSpliterEnvelopperByTransactionAndMakeIntegrationWithOneInput() throws FileNotFoundException {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGIN)
				.queryParam("user", "gdimitriu")
				.queryParam("password", "password")
				.request().get();
		
		assertEquals("wrong authentication", response.getStatus(), Status.OK.getStatusCode());
		
		Map<String, NewCookie> cookies = response.getCookies();
		
		NewCookie cookie = cookies.get("userData");
		
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field("name", "EnveloperStep")
				.field("data", new ClassUtils().getClassAsString("EnveloperStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", response.getStatus(), Status.OK.getStatusCode());
		cookie = response.getCookies().get("userData");
		multipartEntity = new FormDataMultiPart()
				.field("name", "SplitterStep")
				.field("data", new ClassUtils().getClassAsString("SplitterStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", response.getStatus(), Status.OK.getStatusCode());
		cookie = response.getCookies().get("userData");
		multipartEntity = new FormDataMultiPart()
				.field("data", StreamUtils.getStringFromResource("enveloperStepResponse.txt"));
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestPathConstants.PATH_TO_INTEGRATION)
					.path(IRestResourcesConstants.REST_FLOW)
					.queryParam("configuration", StreamUtils.getStringFromResource("basicSplitterEnveloperStep.xml"))
					.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResource("enveloperStepResponse.txt"),
						StreamUtils.toStringFromStream(inputStream));
		}
		
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_LOGOUT).request().cookie(cookie).get();
				
	}
	
	@SuppressWarnings("resource")
	@Test
	public void pushCustomSplitterByTransactionAndMakeIntegrationWitOneInputAndMutipleOutputs() throws FileNotFoundException {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGIN)
				.queryParam("user", "gdimitriu")
				.queryParam("password", "password")
				.request().get();
		
		assertEquals("wrong authentication", response.getStatus(), Status.OK.getStatusCode());
		
		Map<String, NewCookie> cookies = response.getCookies();
		
		NewCookie cookie = cookies.get("userData");
		
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field("name", "SplitterStep")
				.field("data", new ClassUtils().getClassAsString("SplitterStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", response.getStatus(), Status.OK.getStatusCode());
		cookie = response.getCookies().get("userData");
		multipartEntity = new FormDataMultiPart()
				.field("data", StreamUtils.getStringFromResource("enveloperStepResponse.txt"));
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestPathConstants.PATH_TO_INTEGRATION)
					.path(IRestResourcesConstants.REST_FLOW)
					.queryParam("configuration", StreamUtils.getStringFromResource("basicSplitterStep.xml"))
					.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			multipartEntity = response.readEntity(FormDataMultiPart.class);
			List<FormDataBodyPart> bodyParts = multipartEntity.getFields("data");
	    	List<InputStream> actual = new ArrayList<InputStream>();
	    	for (FormDataBodyPart bodyPart : bodyParts) {
	    		actual.add(bodyPart.getEntityAs(InputStream.class));
	    	}
	    	List<InputStream> expected = new ArrayList<InputStream>();
	    	expected.add(StreamUtils.toStreamFromResource("firstMEssage.txt"));
	    	expected.add(StreamUtils.toStreamFromResource("secondMessage.txt"));
	    	TestUtils.compareTwoListOfStreams(expected, actual);
			
		}
		
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_LOGOUT).request().cookie(cookie).get();
				
	}
}
