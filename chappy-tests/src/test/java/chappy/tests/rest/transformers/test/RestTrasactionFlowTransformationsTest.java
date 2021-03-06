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
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.services.IChappyServiceNamesConstants;
import chappy.interfaces.services.IServiceServer;
import chappy.persistence.providers.CustomTransformerStorageProvider;
import chappy.services.servers.rest.ServerJetty;
import chappy.tests.utils.ClassUtils;
import chappy.tests.utils.RestCallsUtils;
import chappy.tests.utils.TestUtils;
import chappy.utils.streams.StreamUtils;

/**
 * @author Gabriel Dimitriu
 *
 */
public class RestTrasactionFlowTransformationsTest {

	private static IServiceServer server = null;

	private static int port = 0;

	private static URI baseUri;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {

		SystemConfigurationProvider.getInstance().readSystemConfiguration(RestTrasactionFlowTransformationsTest.class.
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
		}
	}

	@After
	public void cleanUp() {
		CustomTransformerStorageProvider.getInstance().cleanRepository();
	}
	
	@SuppressWarnings("resource")
	@Test
	public void push3CustomTransformersByTransactionAndMakeTransformation() throws FileNotFoundException {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);

		Response response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGIN)
				.queryParam(IChappyServiceNamesConstants.LOGIN_USER, "gdimitriu")
				.queryParam(IChappyServiceNamesConstants.LOGIN_PASSWORD, "password").request().get();

		assertEquals("wrong authentication", Status.OK.getStatusCode(), response.getStatus());

		Map<String, NewCookie> cookies = response.getCookies();

		NewCookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);

		response = RestCallsUtils.addPrePostProcessingSteps(target, cookie);
		cookie = response.getCookies().get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		FormDataMultiPart multipartEntity = new FormDataMultiPart().field(IChappyServiceNamesConstants.INPUT_DATA, "blabla");
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_TRANSFORM)
				.path(IRestResourcesConstants.REST_FLOW)
				.queryParam(IChappyServiceNamesConstants.CONFIGURATION,
						StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"))
				.request(new String[] { MediaType.MULTIPART_FORM_DATA }).cookie(cookie)
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		cookie = response.getCookies().get(IChappyServiceNamesConstants.COOKIE_USER_DATA);

		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(
					StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummyStepsResponse.txt"),
					StreamUtils.toStringFromStream(inputStream));
		}

		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGOUT)
				.request().cookie(cookie).get();

	}

	@SuppressWarnings("resource")
	@Test
	public void pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs() throws FileNotFoundException {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);

		Response response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGIN)
				.queryParam(IChappyServiceNamesConstants.LOGIN_USER, "gdimitriu")
				.queryParam(IChappyServiceNamesConstants.LOGIN_PASSWORD, "password").request().get();

		assertEquals("wrong authentication", Status.OK.getStatusCode(), response.getStatus());

		Map<String, NewCookie> cookies = response.getCookies();

		NewCookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);

		FormDataMultiPart multipartEntity = new FormDataMultiPart().field(IChappyServiceNamesConstants.TRANSFORMER_NAME, "EnveloperStep")
				.field(IChappyServiceNamesConstants.TRANSFORMER_DATA,
				new ClassUtils().getClassAsString("EnveloperStep", RestCallsUtils.CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_ADD)
				.path(IRestResourcesConstants.REST_TRANSFORMER).request(new String[] { MediaType.MULTIPART_FORM_DATA })
				.cookie(cookie).post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", Status.OK.getStatusCode(), response.getStatus());
		cookie = response.getCookies().get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		multipartEntity = new FormDataMultiPart()
				.field(IChappyServiceNamesConstants.INPUT_DATA,
						StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/firstMessage.txt"))
				.field(IChappyServiceNamesConstants.INPUT_DATA,
						StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/secondMessage.txt"));
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestPathConstants.PATH_TO_INTEGRATION).path(IRestResourcesConstants.REST_FLOW)
				.queryParam(IChappyServiceNamesConstants.CONFIGURATION,
						StreamUtils.getStringFromResource(
								"transaction/dynamic/multipleinputoutput/basicEnveloperStep.xml"))
				.request(new String[] { MediaType.MULTIPART_FORM_DATA }).cookie(cookie)
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		cookie = response.getCookies().get(IChappyServiceNamesConstants.COOKIE_USER_DATA);

		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(
					StreamUtils
							.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
					StreamUtils.toStringFromStream(inputStream));
		}

		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGOUT)
				.request().cookie(cookie).get();

	}

	@SuppressWarnings("resource")
	@Test
	public void pushCustomSpliterEnvelopperByTransactionAndMakeIntegrationWithOneInput() throws FileNotFoundException {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);

		Response response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGIN)
				.queryParam(IChappyServiceNamesConstants.LOGIN_USER, "gdimitriu")
				.queryParam(IChappyServiceNamesConstants.LOGIN_PASSWORD, "password").request().get();

		assertEquals("wrong authentication", Status.OK.getStatusCode(), response.getStatus());

		Map<String, NewCookie> cookies = response.getCookies();

		NewCookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);

		FormDataMultiPart multipartEntity = new FormDataMultiPart().field(IChappyServiceNamesConstants.TRANSFORMER_NAME, "EnveloperStep")
				.field(IChappyServiceNamesConstants.TRANSFORMER_DATA,
				new ClassUtils().getClassAsString("EnveloperStep", RestCallsUtils.CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_ADD)
				.path(IRestResourcesConstants.REST_TRANSFORMER).request(new String[] { MediaType.MULTIPART_FORM_DATA })
				.cookie(cookie).post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", Status.OK.getStatusCode(), response.getStatus());
		cookie = response.getCookies().get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		multipartEntity = new FormDataMultiPart().field(IChappyServiceNamesConstants.TRANSFORMER_NAME, "SplitterStep")
				.field(IChappyServiceNamesConstants.TRANSFORMER_DATA,
				new ClassUtils().getClassAsString("SplitterStep", RestCallsUtils.CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_ADD)
				.path(IRestResourcesConstants.REST_TRANSFORMER).request(new String[] { MediaType.MULTIPART_FORM_DATA })
				.cookie(cookie).post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", Status.OK.getStatusCode(), response.getStatus());
		cookie = response.getCookies().get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		multipartEntity = new FormDataMultiPart().field(IChappyServiceNamesConstants.INPUT_DATA,
				StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"));
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestPathConstants.PATH_TO_INTEGRATION).path(IRestResourcesConstants.REST_FLOW)
				.queryParam(IChappyServiceNamesConstants.CONFIGURATION,
						StreamUtils.getStringFromResource(
								"transaction/dynamic/multipleinputoutput/basicSplitterEnveloperStep.xml"))
				.request(new String[] { MediaType.MULTIPART_FORM_DATA }).cookie(cookie)
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		cookie = response.getCookies().get(IChappyServiceNamesConstants.COOKIE_USER_DATA);

		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(
					StreamUtils
							.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
					StreamUtils.toStringFromStream(inputStream));
		}

		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGOUT)
				.request().cookie(cookie).get();

	}

	@SuppressWarnings("resource")
	@Test
	public void pushCustomSplitterByTransactionAndMakeIntegrationWitOneInputAndMutipleOutputs()
			throws FileNotFoundException {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);

		Response response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGIN)
				.queryParam(IChappyServiceNamesConstants.LOGIN_USER, "gdimitriu")
				.queryParam(IChappyServiceNamesConstants.LOGIN_PASSWORD, "password").request().get();

		assertEquals("wrong authentication", Status.OK.getStatusCode(), response.getStatus());

		Map<String, NewCookie> cookies = response.getCookies();

		NewCookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);

		FormDataMultiPart multipartEntity = new FormDataMultiPart().field(IChappyServiceNamesConstants.TRANSFORMER_NAME, "SplitterStep")
				.field(IChappyServiceNamesConstants.TRANSFORMER_DATA,
				new ClassUtils().getClassAsString("SplitterStep", RestCallsUtils.CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_ADD)
				.path(IRestResourcesConstants.REST_TRANSFORMER).request(new String[] { MediaType.MULTIPART_FORM_DATA })
				.cookie(cookie).post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", Status.OK.getStatusCode(), response.getStatus());
		cookie = response.getCookies().get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		multipartEntity = new FormDataMultiPart().field(IChappyServiceNamesConstants.INPUT_DATA,
				StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"));
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestPathConstants.PATH_TO_INTEGRATION).path(IRestResourcesConstants.REST_FLOW)
				.queryParam(IChappyServiceNamesConstants.CONFIGURATION,
						StreamUtils
								.getStringFromResource("transaction/dynamic/multipleinputoutput/basicSplitterStep.xml"))
				.request(new String[] { MediaType.MULTIPART_FORM_DATA }).cookie(cookie)
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		cookie = response.getCookies().get(IChappyServiceNamesConstants.COOKIE_USER_DATA);

		if (response.getStatus() >= 0) {
			@SuppressWarnings("unchecked")
			List<String> actual = response.readEntity(new ArrayList<String>().getClass());
			List<String> expected = new ArrayList<String>();
			expected.add(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/firstMEssage.txt"));
			expected.add(
					StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/secondMessage.txt"));
			TestUtils.compareTwoListOfStrings(expected, actual);

		}

		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGOUT)
				.request().cookie(cookie).get();

	}
}
