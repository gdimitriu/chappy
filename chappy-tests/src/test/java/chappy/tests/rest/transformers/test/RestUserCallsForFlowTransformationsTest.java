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
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import chappy.configurations.system.SystemConfiguration;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.services.IServiceServer;
import chappy.providers.configurations.SystemConfigurationProvider;
import chappy.providers.transformers.custom.CustomTransformerStorageProvider;
import chappy.services.servers.rest.ServerJetty;
import chappy.tests.utils.ClassUtils;
import chappy.utils.streams.StreamUtils;

/**
 * @author Gabriel Dimitriu
 *
 */
public class RestUserCallsForFlowTransformationsTest {

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

	@SuppressWarnings("resource")
	@Test
	public void push3CustomTransformersByUserAndMakeTransformation() throws FileNotFoundException {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		FormDataMultiPart multipartEntity = new FormDataMultiPart().field("name", "PreProcessingStep").field("data",
				new ClassUtils().getClassAsString("PreProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		Response response = target.path(IRestPathConstants.PATH_TO_ADD_TRANSFORMER_TO_FLOW)
				.path(IRestResourcesConstants.REST_TRANSFORMER_BY_USER).queryParam("user", "gdimitriu")
				.request(new String[] { MediaType.MULTIPART_FORM_DATA })
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		multipartEntity = new FormDataMultiPart().field("name", "PostProcessingStep").field("data",
				new ClassUtils().getClassAsString("PostProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_ADD_TRANSFORMER_TO_FLOW)
				.path(IRestResourcesConstants.REST_TRANSFORMER_BY_USER).queryParam("user", "gdimitriu")
				.request(new String[] { MediaType.MULTIPART_FORM_DATA })
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		multipartEntity = new FormDataMultiPart().field("name", "ProcessingStep").field("data",
				new ClassUtils().getClassAsString("ProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_ADD_TRANSFORMER_TO_FLOW)
				.path(IRestResourcesConstants.REST_TRANSFORMER_BY_USER).queryParam("user", "gdimitriu")
				.request(new String[] { MediaType.MULTIPART_FORM_DATA })
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		multipartEntity = new FormDataMultiPart().field("data", "blabla");
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestPathConstants.PATH_TO_TRANSFORM_FLOW).queryParam("user", "gdimitriu")
				.queryParam("configuration", StreamUtils.getStringFromResource("dummySteps.xml"))
				.request(new String[] { MediaType.MULTIPART_FORM_DATA })
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResource("dummyStepsResponse.txt"),
					StreamUtils.toStringFromStream(inputStream));
		}
	}
}
