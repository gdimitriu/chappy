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
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
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
public class RestCallsForStaxonTransformationsTest {
	
	private static final String CONFIGURATION_AUTOPRIMITIVE = "<?xml version=\"1.0\"?><configuration><autoPrimitive>false</autoPrimitive><autoArray>false</autoArray></configuration>";

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
	public void xml2jsonStepTest() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(baseUri).register(MultiPartFeature.class);
		Response response = target.path("rest").path("transform").path("staxon")
				.queryParam("mode", "xml2json")
				.queryParam("configuration", CONFIGURATION_AUTOPRIMITIVE)
				.request(MediaType.APPLICATION_XML)
				.put(Entity.entity(getClass().getClassLoader().getResourceAsStream("xml2json2xml.xml"),
						MediaType.APPLICATION_XML));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResourceWithoutSpaces("xml2json2xml.json"),
					StreamUtils.toStringFromStream(inputStream));
		}
	}
	
	@Test
	public void json2xmlStepTest() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(baseUri).register(MultiPartFeature.class);
		Response response = target.path("rest").path("transform").path("staxon")
				.queryParam("mode", "json2xml")
				.queryParam("configuration", CONFIGURATION_AUTOPRIMITIVE)
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(getClass().getClassLoader().getResourceAsStream("xml2json2xml.json"),
						MediaType.APPLICATION_JSON));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResourceWithoutSpaces("xml2json2xml.xml"),
					StreamUtils.toStringFromStream(inputStream));
		}
	}

}