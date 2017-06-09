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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;
import org.xml.sax.SAXException;

import chappy.configurations.system.SystemConfiguration;
import chappy.configurations.system.SystemConfigurations;
import chappy.interfaces.rest.resources.IRestPathConstants;
import chappy.interfaces.rest.resources.IRestResourcesConstants;
import chappy.tests.rest.transformers.test.ClassUtils;
import chappy.utils.streams.StreamUtils;
/**
 * This is test class for REST server with digeser.
 * @author Gabriel Dimitriu
 *
 */
public class ProcessingRestTestManual {

	private static final String DIGESTER_ONE_STEP = "digesterFlow";

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
	
	/** send the test message to the REST with digester server */
	public void sendDigester() {
		System.out.println("digester IO");
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
	    .field("data",getClass().getClassLoader().getResourceAsStream("xml2json2xml.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	    .field("configuration", StreamUtils.getStringFromResource("xml2json2xml.xml"),
	    		MediaType.APPLICATION_XML_TYPE);
		Response response = target.path("rest").path("transform").path("flow")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			System.out.println(StreamUtils.toStringFromStream(response.readEntity(InputStream.class)));
		}
	}
	
	public void sendOnlyRequest() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(baseUri).register(MultiPartFeature.class);
		Response response = target.path("rest").path("transform").path("staxon")
				.queryParam("mode", "xml2json")
				.queryParam("configuration", "<?xml version=\"1.0\"?><configuration><array>true</array><autoPrimitive>false</autoPrimitive></configuration>")
				.request(MediaType.APPLICATION_XML)
				.put(Entity.entity(getClass().getClassLoader().getResourceAsStream("xml2json2xml.xml"),
						MediaType.APPLICATION_XML));
		if (response.getStatus() >= 0) {
			System.out.println(StreamUtils.toStringFromStream(response.readEntity(InputStream.class)));
		}
	}

	
	public void xml2xmlXsltOneStepTest() {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
	    .field("data", getClass().getClassLoader().getResourceAsStream("processingInput.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	    .field("configuration", StreamUtils.getStringFromResource("processingOneStepXsl.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("processingMap.xsl", getClass().getClassLoader().getResourceAsStream("processingMap.xsl"),
	    		MediaType.APPLICATION_XML_TYPE);
		Response response = target.path("rest").path("transform").path(DIGESTER_ONE_STEP)
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			System.out.println(StreamUtils.toStringFromStream(response.readEntity(InputStream.class)));
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
	     .field("processingMapParameters.xsl", getClass().getClassLoader().getResourceAsStream("processingMapParameters.xsl"),
	    		MediaType.APPLICATION_XML_TYPE);
		Response response = target.path("rest").path("transform").path(DIGESTER_ONE_STEP)
				.queryParam("param1", "buru")
				.queryParam("param2", "-1000")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			System.out.println(StreamUtils.toStringFromStream(response.readEntity(InputStream.class)));
		}
	}
	
	public void xml2json2xmlStepsFlowWithConfigurationTest() {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
	     .field("data", getClass().getClassLoader().getResourceAsStream("xml2json2xml.xml"),
	    		MediaType.APPLICATION_XML_TYPE);
		Response response = target.path("rest").path("transform").path("flow")
				.queryParam("configuration", StreamUtils.getStringFromResource("xml2json2xmlwithconfigurations.xml"))
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			System.out.println(StreamUtils.toStringFromStream(response.readEntity(InputStream.class)));
		}
	}
	
	public void xml2xml2xmlXsltFlowOneStepTest() {
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class).register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
	     .field("data", getClass().getClassLoader().getResourceAsStream("processingInput.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("configuration", StreamUtils.getStringFromResource("processingTwoStepsXsl.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("processingMap.xsl", getClass().getClassLoader().getResourceAsStream("processingMap.xsl"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("processingMap1.xsl", getClass().getClassLoader().getResourceAsStream("processingMap1.xsl"),
	    		MediaType.APPLICATION_XML_TYPE);
		Response response = target.path("rest").path("transform").path("flow")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			System.out.println(StreamUtils.toStringFromStream(response.readEntity(InputStream.class)));
		}
	}
	
	public void xml2json2xmlFlowStepsTest() {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
	     .field("data", getClass().getClassLoader().getResourceAsStream("xml2json2xml.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("configuration", StreamUtils.getStringFromResource("xml2json2xml.xml"),
	    		MediaType.APPLICATION_XML_TYPE);
		Response response = target.path("rest").path("transform").path("flow")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			System.out.println(StreamUtils.toStringFromStream(response.readEntity(InputStream.class)));
		}
	}
	private static final String CUSTOM_TRANSFORMERS_DUMMY = "chappy.tests.rest.transformers.dummy";
	@SuppressWarnings("resource")
	public void pushTransformerAndUseIt() throws FileNotFoundException {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field("name", "PreProcessingStep")
	     .field("data", new ClassUtils().getClassAsString("PreProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		Response response = target.path("rest").path("add").path("flow").path("transformer")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		System.out.println(response.getStatus());
		multipartEntity = new FormDataMultiPart()
				.field("name", "PostProcessingStep")
	     .field("data", new ClassUtils().getClassAsString("PostProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path("rest").path("add").path("flow").path("transformer")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		System.out.println(response.getStatus());
		multipartEntity = new FormDataMultiPart()
				.field("name", "ProcessingStep")
	     .field("data", new ClassUtils().getClassAsString("ProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path("rest").path("add").path("flow").path("transformer")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		System.out.println(response.getStatus());
		multipartEntity = new FormDataMultiPart()
				.field("data", "blabla");
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path("rest").path("transform").path("flow")
					.queryParam("configuration", StreamUtils.getStringFromResource("dummySteps.xml"))
					.request(new String[]{MediaType.MULTIPART_FORM_DATA})
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			System.out.println(StreamUtils.getStringFromResource("dummyStepsResponse.txt") + " \nvs\n" +
						StreamUtils.toStringFromStream(inputStream));
		}
	}
	
	@SuppressWarnings("resource")
	public void push3CustomTransformersByUserAndMakeTransformation() throws FileNotFoundException {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
				.field("name", "PreProcessingStep")
				.field("data", new ClassUtils().getClassAsString("PreProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		Response response = target.path("rest").path("add").path("flow").path("transformerByUser")
				.queryParam("user", "gdimitriu")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		multipartEntity = new FormDataMultiPart()
				.field("name", "PostProcessingStep")
				.field("data", new ClassUtils().getClassAsString("PostProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path("rest").path("add").path("flow").path("transformerByUser")
				.queryParam("user", "gdimitriu")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		multipartEntity = new FormDataMultiPart()
				.field("name", "ProcessingStep")
				.field("data", new ClassUtils().getClassAsString("ProcessingStep", CUSTOM_TRANSFORMERS_DUMMY));
		response = target.path("rest").path("add").path("flow").path("transformerByUser")
				.queryParam("user", "gdimitriu")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		multipartEntity = new FormDataMultiPart()
				.field("data", "blabla");
		target = client.target(baseUri).register(MultiPartFeature.class);
		response = target.path("rest").path("transform").path("flow")
					.queryParam("user", "gdimitriu")
					.queryParam("configuration", StreamUtils.getStringFromResource("dummySteps.xml"))
					.request(new String[]{MediaType.MULTIPART_FORM_DATA})
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			System.out.println(StreamUtils.getStringFromResource("dummyStepsResponse.txt") + " \nvs\n" +
						StreamUtils.toStringFromStream(inputStream));
		}
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
					.queryParam("configuration", StreamUtils.getStringFromResource("dummySteps.xml"))
					.request(new String[]{MediaType.MULTIPART_FORM_DATA}).cookie(cookie)
					.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			InputStream inputStream = response.readEntity(InputStream.class);
			assertEquals(StreamUtils.getStringFromResource("dummyStepsResponse.txt"),
						StreamUtils.toStringFromStream(inputStream));
		}
	}
	
	public void xml2json2xmlFlowStepsWrongTest() {
		Client client = ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.register(MultiPartWriter.class);
		WebTarget target = client.target(baseUri);
		@SuppressWarnings("resource")
		FormDataMultiPart multipartEntity = new FormDataMultiPart()
	     .field("data", getClass().getClassLoader().getResourceAsStream("exceptions/xml2json2xml.xml"),
	    		MediaType.APPLICATION_XML_TYPE)
	     .field("configuration", StreamUtils.getStringFromResource("exceptions/xml2json2xml.xml"),
	    		MediaType.APPLICATION_XML_TYPE);
		Response response = target.path("rest").path("transform").path("flow")
				.request(new String[]{MediaType.MULTIPART_FORM_DATA})
				.put(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
		if (response.getStatus() >= 0) {
			System.out.println(StreamUtils.toStringFromStream(response.readEntity(InputStream.class)));
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
	
	public static void main(String[] args) throws JAXBException, SAXException, FileNotFoundException {
		ProcessingRestTestManual test = new ProcessingRestTestManual();
		//test.push3CustomTransformersByTransactionAndMakeTransformation();
		test.pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs();
	}

}
