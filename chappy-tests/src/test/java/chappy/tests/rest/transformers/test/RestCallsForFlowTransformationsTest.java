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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import chappy.configurations.system.SystemConfiguration;
import chappy.configurations.system.SystemConfigurations;
import chappy.interfaces.services.IServiceServer;
import chappy.services.servers.rest.ServerJetty;
import chappy.utils.streams.StreamUtils;


/**
 * @author Gabriel Dimitriu
 *
 */
public class RestCallsForFlowTransformationsTest {
	
	private static final String CUSTOM_TRANSFORMERS_DUMMY = "chappy.tests.rest.transformers.dummy";

	private static final String FLOW_ONE_STEP = "flow";

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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		server.stopServer();
	}

	@Test
	public void xml2json2xmlStepsTest() {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
	     .field("data", getClass().getClassLoader().getResourceAsStream("xml2json2xml.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("configuration", StreamUtils.getStringFromResource("xml2json2xml.xml"),
	    		MediaType.APPLICATION_XML_TYPE);
		Response response = target.path("rest").path("transform").path(FLOW_ONE_STEP)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResourceWithoutSpaces("xml2json2xml.xml"),
					StreamUtils.toStringFromStream(inputStream));
		}
	}
	
	@Test
	public void xml2json2xmlStepsWithConfigurationTest() {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
	     .field("data", getClass().getClassLoader().getResourceAsStream("xml2json2xml.xml"),
	    		MediaType.APPLICATION_XML_TYPE);
		Response response = target.path("rest").path("transform").path(FLOW_ONE_STEP)
				.queryParam("configuration", StreamUtils.getStringFromResource("xml2json2xmlwithconfigurations.xml"))
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResourceWithoutSpaces("xml2json2xmlwithconfigurationResult.xml"),
					StreamUtils.toStringFromStream(inputStream));
		}
	}
	
	@Test
	public void xml2xmlXsltOneStepTest() {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
	     .field("data", getClass().getClassLoader().getResourceAsStream("processingInput.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("configuration", StreamUtils.getStringFromResource("processingOneStepXsl.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("processingMap.xsl", getClass().getClassLoader().getResourceAsStream("processingMap.xsl"),
	    		MediaType.APPLICATION_XML_TYPE);
		Response response = target.path("rest").path("transform").path(FLOW_ONE_STEP)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResourceWithoutSpaces("processingOutput.xml"),
					StreamUtils.toStringFromStream(inputStream));
		}
	}
	
	@Test
	public void xml2xml2xmlXsltOneStepTest() {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
	     .field("data", getClass().getClassLoader().getResourceAsStream("processingInput.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("configuration", StreamUtils.getStringFromResource("processingTwoStepsXsl.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("processingMap.xsl", getClass().getClassLoader().getResourceAsStream("processingMap.xsl"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("processingMap1.xsl", getClass().getClassLoader().getResourceAsStream("processingMap1.xsl"),
	    		MediaType.APPLICATION_XML_TYPE);
		Response response = target.path("rest").path("transform").path(FLOW_ONE_STEP)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResourceWithoutSpaces("processingInput.xml"),
					StreamUtils.toStringFromStream(inputStream));
		}
	}

	@Test
	public void xml2xmlXsltOneStepWParametersTest() {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
	     .field("data", getClass().getClassLoader().getResourceAsStream("processingInput.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("configuration", StreamUtils.getStringFromResource("processingOneStepXslParameters.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("processingMapParameters.xsl", getClass().getClassLoader().getResourceAsStream("processingMapParameters.xsl"),
	    		MediaType.APPLICATION_XML_TYPE);
		Response response = target.path("rest").path("transform").path(FLOW_ONE_STEP)
				.queryParam("param1", "buru")
				.queryParam("param2", "-1000")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResourceWithoutSpaces("processingOutputParameters.xml"),
					StreamUtils.toStringFromStream(inputStream));
		}
	}

	@Test
	public void push3CustomTransformersAndMakeTransformation() throws FileNotFoundException {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field("name", "PreProcessingStep")
	     .field("data", new ClassUtils().getClassAsString("PreProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		Response response = target.path("rest").path("add").path("flow").path("transformer")
				.queryParam("user", "gdimitriu")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		multipartEntity = new FormDataMultiPart()
				.field("name", "PostProcessingStep")
	     .field("data", new ClassUtils().getClassAsString("PostProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path("rest").path("add").path("flow").path("transformer")
				.queryParam("user", "gdimitriu")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		multipartEntity = new FormDataMultiPart()
				.field("name", "ProcessingStep")
	     .field("data", new ClassUtils().getClassAsString("ProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path("rest").path("add").path("flow").path("transformer")
				.queryParam("user", "gdimitriu")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		multipartEntity = new FormDataMultiPart()
				.field("data", "blabla");
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path("rest").path("transform").path("flow")
					.queryParam("configuration", StreamUtils.getStringFromResource("dummySteps.xml"))
					.request(new String[]{MediaType.MULTIPART_FORM_DATA})
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResource("dummyStepsResponse.txt"),
						StreamUtils.toStringFromStream(inputStream));
		}
	}
	

}
