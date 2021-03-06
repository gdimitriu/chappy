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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import chappy.configurations.providers.SystemConfigurationProvider;
import chappy.configurations.system.SystemConfiguration;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.services.IChappyServiceNamesConstants;
import chappy.interfaces.services.IServiceServer;
import chappy.persistence.providers.CustomTransformerStorageProvider;
import chappy.services.servers.rest.ServerJetty;
import chappy.utils.streams.StreamUtils;

/**
 * @author Gabriel Dimitriu
 *
 */
public class RestCallsForDigesterTransformationsTest {

	private static IServiceServer server = null;

	private static int port = 0;

	private static URI baseUri;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		SystemConfigurationProvider.getInstance().readSystemConfiguration(RestCallsForDigesterTransformationsTest.class.
				getClassLoader().getResourceAsStream("systemTestConfiguration.xml"));
		SystemConfiguration configuration = SystemConfigurationProvider.getInstance().getSystemConfiguration()
				.getFirstConfiguration();
		port = Integer.parseInt(configuration.getProperty());
		baseUri = UriBuilder.fromUri("{arg}").build(new String[] { "http://localhost:" + port + "/" }, false);
		server = new ServerJetty(port);
		server.startServer();
		CustomTransformerStorageProvider.getInstance().cleanRepository();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDown() throws Exception {
		if (server != null) {
			server.stopServer();
			server = null;
		}
	}
	
	@After
	public void cleanUp() {
		CustomTransformerStorageProvider.getInstance().cleanRepository();
	}
	

	@Test
	public void xml2json2xmlStepsTest() {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart().field(IChappyServiceNamesConstants.INPUT_DATA,
				getClass().getClassLoader().getResourceAsStream("xml2json2xml.xml"), MediaType.APPLICATION_XML_TYPE)
				.field(IChappyServiceNamesConstants.CONFIGURATION, StreamUtils.getStringFromResource("xml2json2xml.xml"),
						MediaType.APPLICATION_XML_TYPE);
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSFORM_DIGESTER)
				.request(new String[] { MediaType.MULTIPART_FORM_DATA })
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
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart().field(IChappyServiceNamesConstants.INPUT_DATA,
				getClass().getClassLoader().getResourceAsStream("xml2json2xml.xml"), MediaType.APPLICATION_XML_TYPE);
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSFORM_DIGESTER)
				.queryParam(IChappyServiceNamesConstants.CONFIGURATION, StreamUtils.getStringFromResource("xml2json2xmlwithconfigurations.xml"))
				.request(new String[] { MediaType.MULTIPART_FORM_DATA })
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
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field(IChappyServiceNamesConstants.INPUT_DATA, getClass().getClassLoader().getResourceAsStream("processingInput.xml"),
						MediaType.APPLICATION_XML_TYPE)
				.field(IChappyServiceNamesConstants.CONFIGURATION, StreamUtils.getStringFromResource("processingOneStepXsl.xml"),
						MediaType.APPLICATION_XML_TYPE)
				.field("processingMap.xsl", getClass().getClassLoader().getResourceAsStream("processingMap.xsl"),
						MediaType.APPLICATION_XML_TYPE);
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSFORM_DIGESTER)
				.request(new String[] { MediaType.MULTIPART_FORM_DATA })
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
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field(IChappyServiceNamesConstants.INPUT_DATA, getClass().getClassLoader().getResourceAsStream("processingInput.xml"),
						MediaType.APPLICATION_XML_TYPE)
				.field(IChappyServiceNamesConstants.CONFIGURATION, StreamUtils.getStringFromResource("processingTwoStepsXsl.xml"),
						MediaType.APPLICATION_XML_TYPE)
				.field("processingMap.xsl", getClass().getClassLoader().getResourceAsStream("processingMap.xsl"),
						MediaType.APPLICATION_XML_TYPE)
				.field("processingMap1.xsl", getClass().getClassLoader().getResourceAsStream("processingMap1.xsl"),
						MediaType.APPLICATION_XML_TYPE);
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSFORM_DIGESTER)
				.request(new String[] { MediaType.MULTIPART_FORM_DATA })
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
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field(IChappyServiceNamesConstants.INPUT_DATA, getClass().getClassLoader().getResourceAsStream("processingInput.xml"),
						MediaType.APPLICATION_XML_TYPE)
				.field(IChappyServiceNamesConstants.CONFIGURATION, StreamUtils.getStringFromResource("processingOneStepXslParameters.xml"),
						MediaType.APPLICATION_XML_TYPE)
				.field("processingMapParameters.xsl",
						getClass().getClassLoader().getResourceAsStream("processingMapParameters.xsl"),
						MediaType.APPLICATION_XML_TYPE);
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSFORM_DIGESTER).queryParam("param1", "buru")
				.queryParam("param2", "-1000").request(new String[] { MediaType.MULTIPART_FORM_DATA })
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResourceWithoutSpaces("processingOutputParameters.xml"),
					StreamUtils.toStringFromStream(inputStream));
		}
	}

}
