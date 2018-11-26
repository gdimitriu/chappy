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
package chappy.tests.clients;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import chappy.clients.common.transaction.ChappyClientTransactionHolder;
import chappy.clients.rest.ChappyRESTAddTransformer;
import chappy.clients.rest.ChappyRESTListTransformers;
import chappy.clients.rest.ChappyRESTLogout;
import chappy.clients.rest.ChappyRESTRunExistingFlow;
import chappy.clients.rest.ChappyRESTTransformFlow;
import chappy.configurations.providers.SystemConfigurationProvider;
import chappy.configurations.system.SystemConfiguration;
import chappy.interfaces.services.IServiceServer;
import chappy.persistence.providers.CustomTransformerStorageProvider;
import chappy.providers.transaction.TransactionProviders;
import chappy.services.servers.rest.ServerJetty;
import chappy.tests.rest.transformers.test.RestCallsUtils;
import chappy.tests.utils.TestUtils;
import chappy.utils.streams.StreamUtils;

/**
 * Functional tests for Chappy clients with REST protocol.
 * @author Gabriel Dimitriu
 *
 */
public class RestClientTrasactionFlowTransformationsTest {

	private static IServiceServer server = null;

	private static int port = 0;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {

		SystemConfigurationProvider.getInstance().readSystemConfiguration(
				RestClientTrasactionFlowTransformationsTest.class.getClassLoader().getResourceAsStream("systemTestConfiguration.xml"));
		SystemConfiguration configuration = SystemConfigurationProvider.getInstance().getSystemConfiguration()
				.getFirstConfiguration();
		port = Integer.parseInt(configuration.getProperty());
		UriBuilder.fromUri("{arg}").build(new String[] { "http://localhost:" + port + "/" }, false);
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
	
	/*
	 *---------------------------------------------------------------------------------
	 *
	 *From here there are the functional tests which shoul be the same on all protocols. 
	 * 
	 * --------------------------------------------------------------------------------
	 */
	
	/**
	 * test chappy: 
	 * 	- login in chappy using REST
	 * 	- add 3 transformer steps and validate using REST
	 *  - run a flow with those steps using REST
	 *  - validate the return data
	 *  - logout from chappy using REST
	 * @throws FileNotFoundException
	 */
	@Test
	public void push3CustomTransformersByTransactionAndMakeTransformation() throws FileNotFoundException {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("PreProcessingStep");
		addTransformers.add("ProcessingStep");
		addTransformers.add("PostProcessingStep");
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(port);
		
		RESTUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);

		ChappyRESTTransformFlow transformer = new ChappyRESTTransformFlow(
				"blabla",
				StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"),
				transaction).send();
		
		if (transformer.getStatusCode() >= 0) {
			List<String> actual = transformer.getOutputResultAsString();
			assertEquals(1, actual.size());
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummyStepsResponse.txt"),
						actual.get(0));
		} else {
			fail("processing error on server");
		}
		ChappyRESTLogout logout = new ChappyRESTLogout(transaction).send();
		assertEquals("could not logout", Status.OK.getStatusCode(), logout.getStatusCode());
	}

	/**
	 * test chappy: 
	 * 	- login in chappy using REST
	 * 	- add enveloper and splitter and validate using REST
	 *  - run a flow with those steps with one input as split-envelope using REST
	 *  - validate the return data
	 *  - logout from chappy using REST
	 * @throws FileNotFoundException
	 */
	@Test
	public void pushCustomSpliterEnvelopperByTransactionAndMakeIntegrationWithOneInput() throws FileNotFoundException {

		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("EnveloperStep");
		addTransformers.add("SplitterStep");
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(port);
		
		RESTUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);

		ChappyRESTTransformFlow transformer = new ChappyRESTTransformFlow(
				StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
				StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicSplitterEnveloperStep.xml"),
				transaction).send();
		
		if (transformer.getStatusCode() >= 0) {
			List<String> actual = transformer.getOutputResultAsString();
			assertEquals(1, actual.size());
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
						actual.get(0));
		} else {
			fail("processing error on server");
		}
		
		ChappyRESTLogout logout = new ChappyRESTLogout(transaction).send();
		assertEquals("could not logout", Status.OK.getStatusCode(), logout.getStatusCode());
	}
	
	/**
	 * test chappy: 
	 * 	- login in chappy using REST
	 * 	- add enveloper step and validate using REST
	 *  - run a flow with enveloper with two input messages using REST
	 *  - validate the return data
	 *  - logout from chappy using REST
	 * @throws FileNotFoundException
	 */
	@Test
	public void pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs() throws FileNotFoundException {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("EnveloperStep");
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(port);
		
		RESTUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);

		ChappyRESTTransformFlow transformer = new ChappyRESTTransformFlow(transaction);
		transformer.addStringConfiguration(
				StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicEnveloperStep.xml"));
		List<String> inputs = new ArrayList<>();
		inputs.add(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/firstMessage.txt"));
		inputs.add(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/secondMessage.txt"));
		transformer.addListOfInputs(inputs);
		transformer.send();
		
		if (transformer.getStatusCode() >= 0) {
			List<String> actual = transformer.getOutputResultAsString();
			assertEquals(1, actual.size());
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
						actual.get(0));
		} else {
			fail("processing error on server");
		}
		ChappyRESTLogout logout = new ChappyRESTLogout(transaction).send();
		assertEquals("could not logout", Status.OK.getStatusCode(), logout.getStatusCode());
	}
	
	/**
	 * test chappy: 
	 * 	- login in chappy using REST
	 * 	- add splitter step and validate using REST
	 *  - run a flow with enveloper with one input messages using REST
	 *  - validate the return data
	 *  - logout from chappy using REST
	 * @throws FileNotFoundException
	 */
	@Test
	public void pushCustomSplitterByTransactionAndMakeIntegrationWitOneInputAndMutipleOutputs()
			throws FileNotFoundException {
		
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("SplitterStep");
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(port);
		
		RESTUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);

		ChappyRESTTransformFlow transformer = new ChappyRESTTransformFlow(
				StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
				StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicSplitterStep.xml"),
				transaction).send();
		
		if (transformer.getStatusCode() >= 0) {
			List<String> actual = transformer.getOutputResultAsString();
			assertEquals(2, actual.size());
			List<String> expected = new ArrayList<>();
			expected.add(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/firstMEssage.txt"));
			expected.add(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/secondMessage.txt"));
			TestUtils.compareTwoListOfStrings(expected, actual);
		} else {
			fail("processing error on server");
		}
		
		ChappyRESTLogout logout = new ChappyRESTLogout(transaction).send();
		assertEquals("could not logout", Status.OK.getStatusCode(), logout.getStatusCode());		
	}
	
	/**
	 * test chappy: 
	 * 	- login in chappy using REST
	 * 	- add 2 custom transformers and validate using REST
	 *  - fail-over the REST server
	 *  - add 1 custom transformer using REST
	 *  - validate that all 3 custom transformers are on the server using REST
	 *  - run the flow with one input message using REST
	 *  - validate the return data
	 *  - logout from chappy using REST
	 * @throws FileNotFoundException
	 */
	@Test
	public void failOverAfterPut2CustomTransformersAddANewOneAndMakeTransformation() throws Exception {
		
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("PreProcessingStep");
		addTransformers.add("PostProcessingStep");
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(port);
		
		RESTUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);

		//stop and restart the server
		tearDown();
		setUp();
		CustomTransformerStorageProvider.getInstance().loadPersistenceCustomTransformers();
		TransactionProviders.getInstance().loadPersisted();
		
		//fail-over ended
		// add transformers in transaction
		ChappyRESTAddTransformer addTransformer = new ChappyRESTAddTransformer("ProcessingStep", transaction);
		try {
			addTransformer.setTransformer("ProcessingStep", RestCallsUtils.CUSTOM_TRANSFORMERS_DUMMY);
			addTransformer.send();
			assertEquals("add transformer " +  "ProcessingStep" + " exception", Status.OK.getStatusCode(), addTransformer.getStatusCode());
			addTransformers.add("ProcessingStep");
		} catch (IOException e) {
			e.printStackTrace();
			fail("exception occured at add transformer" + e.getLocalizedMessage());
		}
		
		// list the added transformers
		ChappyRESTListTransformers listTransformers = new ChappyRESTListTransformers(transaction).send();
		assertEquals("internal error for list transformers", listTransformers.getStatusCode(),
				Status.OK.getStatusCode());
		List<String> transformers = listTransformers.getListOfTransformersName();
		TestUtils.compareTwoListWithoutOrder(addTransformers, transformers);
		
		ChappyRESTTransformFlow transformer = new ChappyRESTTransformFlow(
				"blabla",
				StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"),
				transaction).send();
		
		if (transformer.getStatusCode() >= 0) {
			List<String> actual = transformer.getOutputResultAsString();
			assertEquals(actual.size(), 1);
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummyStepsResponse.txt"),
						actual.get(0));
		} else {
			fail("processing error on server");
		}
		
		ChappyRESTLogout logout = new ChappyRESTLogout(transaction).send();
		assertEquals("could not logout", Status.OK.getStatusCode(), logout.getStatusCode());
	}
	
	/**
	 * test chappy exception: 
	 * 	- login in chappy using REST
	 *  - run the flow with one input message (the transformer is missing) using REST
	 *		(Chappy should return exception 412 with precondition failed.)
	 *  - validate the return data
	 *  - logout from chappy using REST
	 * 
	 */
	@Test
	public void exceptionMissingTransformerInTransactionException() {
		
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(port);
		
		ChappyRESTTransformFlow transformer = new ChappyRESTTransformFlow(
				StreamUtils.getStringFromResource("exceptions/missingtransformer.xml"),
				MediaType.APPLICATION_XML_TYPE,
				StreamUtils.getStringFromResource("exceptions/missingtransformer.xml"),
				transaction).send();
		
		assertEquals("status should be 412", Status.PRECONDITION_FAILED.getStatusCode(), transformer.getStatusCode());
		assertEquals(StreamUtils.getStringFromResource("exceptions/missingtransformer.out"),
				transformer.getTransactionErrorMessage());
		
		ChappyRESTLogout logout = new ChappyRESTLogout(transaction).send();
		assertEquals("could not logout", Status.OK.getStatusCode(), logout.getStatusCode());
	}
	
	/**
	 * test chappy exception: 
	 * 	- login in chappy using REST
	 *  - run the flow with one input message (the configuration xml is wrong for one step not supported tag in configuration) using REST
	 *		(Chappy should return exception 403 with forbidden.)
	 *  - validate the return data
	 *  - logout from chappy using REST
	 * 
	 */
	@Test
	public void exceptionXml2json2xmlStepsWithConfigurationWrongXMLConfiguration() {
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(port);
		
		ChappyRESTTransformFlow transformer = new ChappyRESTTransformFlow(
				StreamUtils.getStringFromResource("xml2json2xml.xml"),
				StreamUtils.getStringFromResource("exceptions/xml2json2xmlwithconfigurations.xml"),
				transaction).send();
				
		assertEquals("Status should be 403", Status.FORBIDDEN.getStatusCode(), transformer.getStatusCode());
		assertEquals(StreamUtils.getStringFromResource("exceptions/xml2json2xmlwithconfiguration.out"),
				transformer.getTransactionErrorMessage());
	}
	
	/**
	 * test chappy exception: 
	 * 	- login in chappy using REST
	 *  - run the flow with one input message (the configuration xml is wrong for one step parameters1 instead of parameters) using REST
	 *		(Chappy should return exception 403 with forbidden.)
	 *  - validate the return data
	 *  - logout from chappy using REST
	 * 
	 */
	@Test
	public void exceptionXml2json2xmlStepsWrongXMLConfigurationTest() {
		
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(port);
		
		ChappyRESTTransformFlow transformer = new ChappyRESTTransformFlow(
				StreamUtils.getStringFromResource("exceptions/xml2json2xml.xml"),
				StreamUtils.getStringFromResource("exceptions/xml2json2xml.xml"),
				transaction).send();
		
		assertEquals("Status should be 403", Status.FORBIDDEN.getStatusCode(), transformer.getStatusCode());
		assertEquals(StreamUtils.getStringFromResource("exceptions/xml2json2xml.out"),
				transformer.getTransactionErrorMessage());
	}
	
	
	/**
	 * test chappy: 
	 * 	- login in chappy using REST
	 * 	- add 3 transformer steps and validate using REST
	 *  - add the flow using REST
	 *  - run a flow with those steps using REST
	 *  - validate the return data
	 *  - logout from chappy using REST
	 * @throws FileNotFoundException
	 * @throws JsonProcessingException 
	 */
	@Test
	public void push3CustomTransformersByTransactionPushFlowAndMakeTransformation() throws FileNotFoundException {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("PreProcessingStep");
		addTransformers.add("ProcessingStep");
		addTransformers.add("PostProcessingStep");
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(port);
		
		RESTUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
		
		RESTUtilsRequests.chappyAddFlow("first_Flow",
				StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"), transaction);

		ChappyRESTRunExistingFlow transformer = new ChappyRESTRunExistingFlow(
				"blabla",
				"first_Flow",
				transaction).send();
		
		if (transformer.getStatusCode() >= 0) {
			List<String> actual = transformer.getOutputResultAsString();
			assertEquals(actual.size(), 1);
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummyStepsResponse.txt"),
						actual.get(0));
		} else {
			fail("processing error on server");
		}
		
		ChappyRESTLogout logout = new ChappyRESTLogout(transaction).send();
		assertEquals("could not logout", Status.OK.getStatusCode(), logout.getStatusCode());
	}
}
