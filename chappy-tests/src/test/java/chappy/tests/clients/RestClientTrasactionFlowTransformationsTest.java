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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import chappy.clients.rest.ChappyRESTAddTransformer;
import chappy.clients.rest.ChappyRESTListTransformers;
import chappy.clients.rest.ChappyRESTLogin;
import chappy.clients.rest.ChappyRESTLogout;
import chappy.clients.rest.ChappyRESTTransformFlow;
import chappy.configurations.providers.SystemConfigurationProvider;
import chappy.configurations.system.SystemConfiguration;
import chappy.interfaces.rest.IRESTTransactionHolder;
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

	private IServiceServer server = null;

	private int port = 0;

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
		UriBuilder.fromUri("{arg}").build(new String[] { "http://localhost:" + port + "/" }, false);
		server = new ServerJetty(port);
		Thread thread = new Thread() {
			public void run() {
				try {
					server.startServer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fail("could not start server " + e.getLocalizedMessage());
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
	 * login and add custom transformers and list and validate them.
	 * @return chappy transaction holder
	 */
	private IRESTTransactionHolder chappyRESTLoginAddCustomTransformers(final List<String> addTransformers) {
		ChappyRESTLogin login = new ChappyRESTLogin("gdimitriu", "password", true);
		try {
			login.createConnectionToServer("localhost", port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		login.send();
		assertEquals("wrong authentication", login.getStatusCode(), Status.OK.getStatusCode());
		assertEquals("wrong user", "gdimitriu", login.getCookie().getUserName());
		IRESTTransactionHolder transaction = login.createTransactionHolder();
		
		// add transformers in transaction
		for (String transf : addTransformers) {
			ChappyRESTAddTransformer addTransformer = new ChappyRESTAddTransformer(transf, transaction);
			try {
				addTransformer.setTransformer(transf, RestCallsUtils.CUSTOM_TRANSFORMERS_DUMMY);
				addTransformer.send();
				assertEquals("add transformer " + transf + " exception", addTransformer.getStatusCode(),
						Status.OK.getStatusCode());
			} catch (IOException e) {
				e.printStackTrace();
				fail("exception occured at add transformer" + e.getLocalizedMessage());
			}
		}

		// list the added transformers
		ChappyRESTListTransformers listTransformers = new ChappyRESTListTransformers(transaction).send();
		assertEquals("internal error for list transformers", listTransformers.getStatusCode(),
				Status.OK.getStatusCode());
		List<String> transformers = listTransformers.getListOfTransformersName();
		TestUtils.compareTwoListWithoutOrder(addTransformers, transformers);
		return transaction;
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
	 * 	- login
	 * 	- add 3 transformer steps
	 *  - validate that they are on the server
	 *  - run a flow with those steps
	 *  - validate the return data
	 *  - logout
	 * @throws FileNotFoundException
	 */
	@Test
	public void push3CustomTransformersByTransactionAndMakeTransformation() throws FileNotFoundException {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("PreProcessingStep");
		addTransformers.add("ProcessingStep");
		addTransformers.add("PostProcessingStep");
		IRESTTransactionHolder transaction = chappyRESTLoginAddCustomTransformers(addTransformers);

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
		assertEquals("could not logout", logout.getStatusCode(), Status.OK.getStatusCode());
	}

	/**
	 * test chappy: 
	 * 	- login
	 * 	- add enveloper and splitter
	 *  - validate that they are on the server
	 *  - run a flow with those steps with one input as split-envelope
	 *  - validate the return data
	 *  - logout
	 * @throws FileNotFoundException
	 */
	@Test
	public void pushCustomSpliterEnvelopperByTransactionAndMakeIntegrationWithOneInput() throws FileNotFoundException {

		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("EnveloperStep");
		addTransformers.add("SplitterStep");
		IRESTTransactionHolder transaction = chappyRESTLoginAddCustomTransformers(addTransformers);

		ChappyRESTTransformFlow transformer = new ChappyRESTTransformFlow(
				StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
				StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicSplitterEnveloperStep.xml"),
				transaction).send();
		
		if (transformer.getStatusCode() >= 0) {
			List<String> actual = transformer.getOutputResultAsString();
			assertEquals(actual.size(), 1);
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
						actual.get(0));
		} else {
			fail("processing error on server");
		}
		ChappyRESTLogout logout = new ChappyRESTLogout(transaction).send();
		assertEquals("could not logout", logout.getStatusCode(), Status.OK.getStatusCode());
	}
	
	/**
	 * test chappy: 
	 * 	- login
	 * 	- add enveloper step
	 *  - validate that they are on the server
	 *  - run a flow with enveloper with two input messages
	 *  - validate the return data
	 *  - logout
	 * @throws FileNotFoundException
	 */
	@Test
	public void pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs() throws FileNotFoundException {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("EnveloperStep");
		IRESTTransactionHolder transaction = chappyRESTLoginAddCustomTransformers(addTransformers);

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
		assertEquals("could not logout", logout.getStatusCode(), Status.OK.getStatusCode());
	}
	
	/**
	 * test chappy: 
	 * 	- login
	 * 	- add splitter step
	 *  - validate that they are on the server
	 *  - run a flow with enveloper with one input messages
	 *  - validate the return data
	 *  - logout
	 * @throws FileNotFoundException
	 */
	@Test
	public void pushCustomSplitterByTransactionAndMakeIntegrationWitOneInputAndMutipleOutputs()
			throws FileNotFoundException {
		
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("SplitterStep");
		IRESTTransactionHolder transaction = chappyRESTLoginAddCustomTransformers(addTransformers);

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
		assertEquals("could not logout", logout.getStatusCode(), Status.OK.getStatusCode());		
	}
	
	/**
	 * test chappy: 
	 * 	- login
	 * 	- add 2 custom transformers
	 *  - validate that they are on the server
	 *  - fail-over on the server side
	 *  - add 1 custom transformer
	 *  - validat that all 3 custom transformers are on the server
	 *  - run the flow with one input message 
	 *  - validate the return data
	 *  - logout
	 * @throws FileNotFoundException
	 */
	@Test
	public void failOverAfterPut2CustomTransformersAddANewOneAndMakeTransformation() throws Exception {
		
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("PreProcessingStep");
		addTransformers.add("PostProcessingStep");
		IRESTTransactionHolder transaction = chappyRESTLoginAddCustomTransformers(addTransformers);

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
			assertEquals("add transformer " +  "ProcessingStep" + " exception", addTransformer.getStatusCode(),
					Status.OK.getStatusCode());
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
		assertEquals("could not logout", logout.getStatusCode(), Status.OK.getStatusCode());
	}
	
	/**
	 * test chappy exception: 
	 * 	- login
	 *  - run the flow with one input message (the transformer is missing)
	 *  - Chappy should return exception 412 with precondition failed.
	 *  - validate the return data
	 *  - logout
	 * 
	 */
	@Test
	public void exceptionMissingTransformerInTransactionException() {
		ChappyRESTLogin login = new ChappyRESTLogin("gdimitriu", "password", true);
		try {
			login.createConnectionToServer("localhost", port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		login.send();
		assertEquals("wrong authentication", login.getStatusCode(), Status.OK.getStatusCode());
		assertEquals("wrong user", "gdimitriu", login.getCookie().getUserName());
		IRESTTransactionHolder transaction = login.createTransactionHolder();
		
		ChappyRESTTransformFlow transformer = new ChappyRESTTransformFlow(
				StreamUtils.getStringFromResource("exceptions/missingtransformer.xml"),
				MediaType.APPLICATION_XML_TYPE,
				StreamUtils.getStringFromResource("exceptions/missingtransformer.xml"),
				transaction).send();
		
		assertEquals("status should be 412", Status.PRECONDITION_FAILED.getStatusCode(), transformer.getStatusCode());
		assertEquals(StreamUtils.getStringFromResource("exceptions/missingtransformer.out"),
				transformer.getTransactionErrorMessage());
		
		ChappyRESTLogout logout = new ChappyRESTLogout(transaction).send();
		assertEquals("could not logout", logout.getStatusCode(), Status.OK.getStatusCode());
	}
	
	/**
	 * test chappy exception: 
	 * 	- login
	 *  - run the flow with one input message (the configuration xml is wrong for one step not supported tag in configuration)
	 *  - Chappy should return exception 403 with forbidden.
	 *  - validate the return data
	 *  - logout
	 * 
	 */
	@Test
	public void exceptionXml2json2xmlStepsWithConfigurationWrongXMLConfiguration() {
		ChappyRESTLogin login = new ChappyRESTLogin("gdimitriu", "password", true);
		try {
			login.createConnectionToServer("localhost", port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		login.send();
		assertEquals("wrong authentication", login.getStatusCode(), Status.OK.getStatusCode());
		assertEquals("wrong user", "gdimitriu", login.getCookie().getUserName());
		IRESTTransactionHolder transaction = login.createTransactionHolder();
		
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
	 * 	- login
	 *  - run the flow with one input message (the configuration xml is wrong for one step parameters1 instead of parameters)
	 *  - Chappy should return exception 403 with forbidden.
	 *  - validate the return data
	 *  - logout
	 * 
	 */
	@Test
	public void exceptionXml2json2xmlStepsWrongXMLConfigurationTest() {
		ChappyRESTLogin login = new ChappyRESTLogin("gdimitriu", "password", true);
		try {
			login.createConnectionToServer("localhost", port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		login.send();
		assertEquals("wrong authentication", login.getStatusCode(), Status.OK.getStatusCode());
		assertEquals("wrong user", "gdimitriu", login.getCookie().getUserName());
		IRESTTransactionHolder transaction = login.createTransactionHolder();
		
		ChappyRESTTransformFlow transformer = new ChappyRESTTransformFlow(
				StreamUtils.getStringFromResource("exceptions/xml2json2xml.xml"),
				StreamUtils.getStringFromResource("exceptions/xml2json2xml.xml"),
				transaction).send();
		
		assertEquals("Status should be 403", Status.FORBIDDEN.getStatusCode(), transformer.getStatusCode());
		assertEquals(StreamUtils.getStringFromResource("exceptions/xml2json2xml.out"),
				transformer.getTransactionErrorMessage());
	}
}
