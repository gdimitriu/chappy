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
package chappy.tests.manual.rest.transformers.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import chappy.clients.rest.ChappyRESTAddTransformer;
import chappy.clients.rest.ChappyRESTListTransformers;
import chappy.clients.rest.ChappyRESTLogin;
import chappy.clients.rest.ChappyRESTLogout;
import chappy.clients.rest.ChappyRESTTransformFlow;
import chappy.configurations.system.SystemConfiguration;
import chappy.configurations.system.SystemConfigurations;
import chappy.interfaces.rest.IRESTTransactionHolder;
import chappy.interfaces.rest.LocalDateTimeContextResolver;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.interfaces.services.IChappyServiceNamesConstants;
import chappy.interfaces.statisticslogs.StatisticLog;
import chappy.persistence.providers.CustomTransformerStorageProvider;
import chappy.providers.transaction.TransactionProviders;
import chappy.tests.utils.ClassUtils;
import chappy.tests.utils.TestUtils;
import chappy.utils.streams.StreamUtils;
/**
 * This is test class for REST server with digeser.
 * @author Gabriel Dimitriu
 *
 */
public class ProcessingRestTestManual {

	@SuppressWarnings("unused")
	private static final String DIGESTER_ONE_STEP = "digesterFlow";
	private static final String CUSTOM_TRANSFORMERS_DUMMY = "chappy.tests.rest.transformers.dummy";
	private int port = 0;

	private URI baseUri;

	/**
	 * @throws JAXBException 
	 * @throws SAXException 
	 * 
	 */
	public ProcessingRestTestManual() throws JAXBException, SAXException {
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
		baseUri  = UriBuilder.fromUri("{arg}").build(new String[]{"http://localhost:"+ port + "/"},false);
	}

	@SuppressWarnings("resource")
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
					.queryParam("configuration", StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"))
					.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummyStepsResponse.txt"),
						StreamUtils.toStringFromStream(inputStream));
		}
	}
	
	
	@SuppressWarnings("resource")
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
				.field("data", StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/firstMessage.txt"))
				.field("data", StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/secondMessage.txt"));
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestPathConstants.PATH_TO_INTEGRATION)
					.path(IRestResourcesConstants.REST_FLOW)
					.queryParam("configuration", StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicEnveloperStep.xml"))
					.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
						StreamUtils.toStringFromStream(inputStream));
		}
		
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_LOGOUT).request().cookie(cookie).get();
				
	}
	
	@SuppressWarnings("resource")
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
				.field("data", StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"));
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestPathConstants.PATH_TO_INTEGRATION)
					.path(IRestResourcesConstants.REST_FLOW)
					.queryParam("configuration", StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicSplitterEnveloperStep.xml"))
					.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
						StreamUtils.toStringFromStream(inputStream));
		}
		
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_LOGOUT).request().cookie(cookie).get();
				
	}
	
	@SuppressWarnings("resource")
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
				.field("data", StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"));
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestPathConstants.PATH_TO_INTEGRATION)
					.path(IRestResourcesConstants.REST_FLOW)
					.queryParam("configuration", StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicSplitterStep.xml"))
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
	    	expected.add(StreamUtils.toStreamFromResource("transaction/dynamic/multipleinputoutput/firstMEssage.txt"));
	    	expected.add(StreamUtils.toStreamFromResource("transaction/dynamic/multipleinputoutput/secondMessage.txt"));
	    	TestUtils.compareTwoListOfStreams(expected, actual);
			
		}
		
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_LOGOUT).request().cookie(cookie).get();
				
	}
	
	
	public void getTheListOfDefaultTransformers() throws FileNotFoundException {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		
		Response response = null;
		
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestResourcesConstants.BASE_REST)
					.path(IRestResourcesConstants.REST_LIST)
					.path(IRestResourcesConstants.REST_DEFAULT)
					.request().get();
		if (response.getStatus() >= 0) {
			@SuppressWarnings("unchecked")
			List<String> actual = response.readEntity(new ArrayList<String>().getClass());
	    	List<String> expected = new ArrayList<String>();
	    	expected.add("XslStep");
	    	expected.add("Json2XmlStep");
	    	expected.add("Xml2JsonStep");
	    	TestUtils.compareTwoListWithoutOrder(expected, actual);
		}
	}
	
	private static final String CONFIGURATION_AUTOPRIMITIVE = "<?xml version=\"1.0\"?><configuration><autoPrimitive>false</autoPrimitive><autoArray>false</autoArray></configuration>";

	public void xml2jsonStepTest() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(baseUri).register(MultiPartFeature.class);
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSFORM_STAXON).queryParam("mode", "xml2json")
				.queryParam("configuration", CONFIGURATION_AUTOPRIMITIVE).request(MediaType.APPLICATION_XML)
				.put(Entity.entity(getClass().getClassLoader().getResourceAsStream("xml2json2xml.xml"),
						MediaType.APPLICATION_XML));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResourceWithoutSpaces("xml2json2xml.json"),
					StreamUtils.toStringFromStream(inputStream));
		}
	}
	
	public void xml2xmlXsltOneStepWParametersTest() {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field("data", getClass().getClassLoader().getResourceAsStream("processingInput.xml"),
						MediaType.APPLICATION_XML_TYPE)
				.field("configuration", StreamUtils.getStringFromResource("processingOneStepXslParameters.xml"),
						MediaType.APPLICATION_XML_TYPE)
				.field("processingMapParameters.xsl",
						getClass().getClassLoader().getResourceAsStream("processingMapParameters.xsl"),
						MediaType.APPLICATION_XML_TYPE);
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSFORM_FLOW).queryParam("param1", "buru")
				.queryParam("param2", "-1000").request(new String[] { MediaType.MULTIPART_FORM_DATA })
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResourceWithoutSpaces("processingOutputParameters.xml"),
					StreamUtils.toStringFromStream(inputStream));
		}
	}
	
	@SuppressWarnings("resource")
	public void push3CustomTransformersByTransactionAndMakeTransformationGetStatistics() throws FileNotFoundException {
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
	
	public void clientLoginLogout() {
		ChappyRESTLogin login = new ChappyRESTLogin("gdimitriu", "password", true);
		try {
			login.createConnectionToServer("localhost", port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		login.send();
		assertEquals("wrong authentication", Status.OK.getStatusCode(), login.getStatusCode());
		System.out.println(login.getCookie().getUserName());
		System.out.println(login.getCookie().getTransactionId());
		IRESTTransactionHolder transaction = login.createTransactionHolder();
		
		ChappyRESTAddTransformer addTransformer = new ChappyRESTAddTransformer("SplitterStep", transaction);
		try {
			addTransformer.setTransformer("SplitterStep", CUSTOM_TRANSFORMERS_DUMMY);
			addTransformer.send();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addTransformer = new ChappyRESTAddTransformer("EnveloperStep", transaction);
		try {
			addTransformer.setTransformer("EnveloperStep", CUSTOM_TRANSFORMERS_DUMMY);
			addTransformer.send();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ChappyRESTListTransformers listTransformer = new ChappyRESTListTransformers(transaction);
		listTransformer.send();
		List<String> transformers = listTransformer.getListOfTransformersName();
		for(String str : transformers) {
			System.out.println(str);
		}
		
		ChappyRESTTransformFlow transformer = new ChappyRESTTransformFlow(
				StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
				StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicSplitterEnveloperStep.xml"),
				transaction);
		transformer.send();
		if (transformer.getStatusCode() >= 0) {
			List<String> list = transformer.getOutputResultAsString();
			assertEquals(list.size(), 1);
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
						list.get(0));
		} else {
			fail("processing error on server");
		}
		ChappyRESTLogout logout = new ChappyRESTLogout(transaction);
		logout.send();
		assertEquals("could not logout", logout.getStatusCode(), Status.OK.getStatusCode());
		System.out.println(logout.getStatus());
	}
	
	@SuppressWarnings("resource")
	public void cachingTests() throws FileNotFoundException {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class)
				.register(JacksonJaxbJsonProvider.class)
				.register(LocalDateTimeContextResolver.class);
		WebTarget target = client.target(baseUri);
		
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGIN)
				.queryParam(IChappyServiceNamesConstants.LOGIN_USER, "gdimitriu")
				.queryParam(IChappyServiceNamesConstants.LOGIN_PASSWORD, "password")
				.queryParam(IChappyServiceNamesConstants.PERSIST, "true")
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
				.field("configuration", StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"));
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
					.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_FLOW)
					.queryParam(IChappyServiceNamesConstants.CHAPPY_FLOW_NAME, "first_Flow")
					.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		System.out.println(response.toString());
	}
	
	public void push3CustomTransformersByTransactionAndMakeTransformationGetStatistics_NW() throws Exception {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class)
				.register(JacksonJaxbJsonProvider.class)
				.register(LocalDateTimeContextResolver.class);
		WebTarget target = client.target(baseUri);
		
		Response response = target.path(IRestPathConstants.PATH_TO_TRANSACTION).path(IRestResourcesConstants.REST_LOGIN)
				.queryParam(IChappyServiceNamesConstants.LOGIN_USER, "gdimitriu")
				.queryParam(IChappyServiceNamesConstants.LOGIN_PASSWORD, "password")
				.queryParam(IChappyServiceNamesConstants.PERSIST, "true")
				.request().get();
		
		assertEquals("wrong authentication", Status.OK.getStatusCode(), response.getStatus());
		
		Map<String, NewCookie> cookies = response.getCookies();
		
		NewCookie cookie = cookies.get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field(IChappyServiceNamesConstants.TRANSFORMER_NAME, "PreProcessingStep")
				.field(IChappyServiceNamesConstants.TRANSFORMER_DATA, new ClassUtils()
						.getClassAsString("PreProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", Status.OK.getStatusCode(), response.getStatus());
		cookie = response.getCookies().get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		multipartEntity = new FormDataMultiPart()
				.field(IChappyServiceNamesConstants.TRANSFORMER_NAME, "PostProcessingStep")
				.field(IChappyServiceNamesConstants.TRANSFORMER_DATA, new ClassUtils()
						.getClassAsString("PostProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", Status.OK.getStatusCode(), response.getStatus());
		cookie = response.getCookies().get(IChappyServiceNamesConstants.COOKIE_USER_DATA);

		multipartEntity = new FormDataMultiPart()
				.field(IChappyServiceNamesConstants.TRANSFORMER_NAME, "ProcessingStep")
				.field(IChappyServiceNamesConstants.TRANSFORMER_DATA, new ClassUtils()
						.getClassAsString("ProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_ADD).path(IRestResourcesConstants.REST_TRANSFORMER)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		assertEquals("could not add transformer", Status.OK.getStatusCode(), response.getStatus());
		cookie = response.getCookies().get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		multipartEntity = new FormDataMultiPart()
				.field(IChappyServiceNamesConstants.INPUT_DATA, "blabla");
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
					.path(IRestResourcesConstants.REST_TRANSFORM).path(IRestResourcesConstants.REST_FLOW)
					.queryParam(IChappyServiceNamesConstants.CONFIGURATION, StreamUtils
							.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"))
					.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummyStepsResponse.txt"),
						StreamUtils.toStringFromStream(inputStream));
		}
		cookie = response.getCookies().get(IChappyServiceNamesConstants.COOKIE_USER_DATA);
		
		response = target.path(IRestPathConstants.PATH_TO_TRANSACTION)
				.path(IRestResourcesConstants.REST_LOGOUT).request().cookie(cookie).get();
	}
	
	public static void main(String[] args) throws JAXBException, SAXException, FileNotFoundException {
		ProcessingRestTestManual test = new ProcessingRestTestManual();
		//test.push3CustomTransformersByTransactionAndMakeTransformationGetStatistics();
		System.out.println("finished");
		//test.cachingTests();
		//test.pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs();
		//test.xml2xmlXsltOneStepWParametersTest();
		test.clientLoginLogout();
	}

	

}
