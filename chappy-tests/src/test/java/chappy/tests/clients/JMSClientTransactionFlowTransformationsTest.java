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

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import chappy.clients.jms.ChappyJMSAddTransformer;
import chappy.clients.jms.ChappyJMSListTransformers;
import chappy.clients.jms.ChappyJMSTransformFlow;
import chappy.configurations.providers.SystemConfigurationProvider;
import chappy.configurations.system.SystemConfigurations;
import chappy.interfaces.jms.IJMSTransactionHolder;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.services.IServiceServer;
import chappy.persistence.providers.CustomTransformerStorageProvider;
import chappy.policy.provider.JMSRuntimeResourceProvider;
import chappy.providers.transaction.TransactionProviders;
import chappy.services.servers.jms.ServerJMS;
import chappy.services.servers.jms.resources.TransactionRouter;
import chappy.services.servers.jms.resources.tranform.AddTransformer;
import chappy.services.servers.jms.resources.tranform.Authentication;
import chappy.services.servers.jms.resources.tranform.ListTransformers;
import chappy.services.servers.jms.resources.tranform.TransformFlow;
import chappy.tests.rest.transformers.test.RestCallsUtils;
import chappy.tests.utils.TestUtils;
import chappy.utils.streams.StreamUtils;

/**
 * Functional tests for Chappy clients with JMS protocol.
 * @author Gabriel Dimitriu
 *
 */
public class JMSClientTransactionFlowTransformationsTest {

	private IServiceServer server = null;
	
	/** server port for connection */
	private int serverPort = 61616;
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		SystemConfigurationProvider.getInstance().readSystemConfiguration(
				getClass().getClassLoader().getResourceAsStream("systemTestConfiguration.xml"));
		SystemConfigurations configuration = SystemConfigurationProvider.getInstance().getSystemConfiguration();
		server = new ServerJMS();
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new TransactionRouter());
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new Authentication());
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new AddTransformer());
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new TransformFlow());
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new ListTransformers());
		server.configure(configuration);
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
	
	/*
	 *---------------------------------------------------------------------------------
	 *
	 *Small tests for special request on JMS (first testing) 
	 * 
	 * --------------------------------------------------------------------------------
	 */
	/**
	 * chappy test:
	 * 	- login in chappy using jms
	 * 	- logout from chappy using jms
	 */
	@Test
	public void jmsChappyLoginLogout() {
		try {
			IJMSTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void jmsChappyAddTransformer() {
		try {
			IJMSTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
			List<String> addTransformers = new ArrayList<>();
			addTransformers.add("PreProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	/*
	 *---------------------------------------------------------------------------------
	 *
	 *From here there are the functional tests which should be the same on all protocols. 
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
		try {
			IJMSTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
			JMSUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			ChappyJMSTransformFlow transformer = new ChappyJMSTransformFlow(
					"blabla",
					StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"),
					transaction).send();
			while(transformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
			if (transformer.getStatusCode() >= 0) {
				List<String> actual = transformer.getOutputResultAsString();
				assertEquals(actual.size(), 1);
				assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummyStepsResponse.txt"),
							actual.get(0));
			} else {
				fail("processing error on server");
			}
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
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
		try {
			IJMSTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
			JMSUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			ChappyJMSTransformFlow transformer = new ChappyJMSTransformFlow(
					StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
					StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicSplitterEnveloperStep.xml"),
					transaction).send();
			while(transformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
			if (transformer.getStatusCode() >= 0) {
				List<String> actual = transformer.getOutputResultAsString();
				assertEquals(actual.size(), 1);
				assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
							actual.get(0));
			} else {
				fail("processing error on server");
			}
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
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
		try {
			IJMSTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
			JMSUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			ChappyJMSTransformFlow transformer = new ChappyJMSTransformFlow(transaction);
			transformer.addStringConfiguration(
					StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicEnveloperStep.xml"));
			List<String> inputs = new ArrayList<>();
			inputs.add(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/firstMessage.txt"));
			inputs.add(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/secondMessage.txt"));
			transformer.addListOfInputs(inputs);
			transformer.send();
			while(transformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
			if (transformer.getStatusCode() >= 0) {
				List<String> actual = transformer.getOutputResultAsString();
				assertEquals(1, actual.size());
				assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
							actual.get(0));
			} else {
				fail("processing error on server");
			}
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
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
		try {
			IJMSTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
			JMSUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			ChappyJMSTransformFlow transformer = new ChappyJMSTransformFlow(
					StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
					StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicSplitterStep.xml"),
					transaction).send();
			while(transformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
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
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
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
		IJMSTransactionHolder transaction = null;
		try {
			transaction = JMSUtilsRequests.chappyLogin(serverPort);
			JMSUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		
		//stop and restart the server
		tearDown();
		setUp();
		CustomTransformerStorageProvider.getInstance().loadPersistenceCustomTransformers();
		TransactionProviders.getInstance().loadPersisted();
				
		//fail-over ended
		// add transformers in transaction
		ChappyJMSAddTransformer addTransformer = new ChappyJMSAddTransformer("ProcessingStep", transaction);
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
		
		//validate the new added transformer
		try {
			// list the added transformers
			ChappyJMSListTransformers listTransformers = new ChappyJMSListTransformers(transaction).send();
			while(listTransformers.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
			assertEquals("internal error for list transformers", listTransformers.getStatus(),
					IJMSStatus.OK);
			List<String> transformers = listTransformers.getListOfTransformersName();
			TestUtils.compareTwoListWithoutOrder(addTransformers, transformers);
		} catch (Exception e) {
			fail("exception occured at add transformer" + e.getLocalizedMessage());
		}
		//transform
		ChappyJMSTransformFlow transformer = new ChappyJMSTransformFlow(
				"blabla",
				StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"),
				transaction).send();
		while(transformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
		if (transformer.getStatusCode() >= 0) {
			List<String> actual = transformer.getOutputResultAsString();
			assertEquals(actual.size(), 1);
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummyStepsResponse.txt"),
						actual.get(0));
		} else {
			fail("processing error on server");
		}
		JMSUtilsRequests.chappyLogout(transaction);
	}
	
	/**
	 * test chappy exception: 
	 * 	- login
	 *  - run the flow with one input message (the transformer is missing)
	 *  - Chappy should return exception with precondition failed.
	 *  - validate the return data
	 *  - logout
	 * 
	 */
	@Test
	public void exceptionMissingTransformerInTransactionException() {
		try {
			IJMSTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
			ChappyJMSTransformFlow transformer = new ChappyJMSTransformFlow(
					StreamUtils.getStringFromResource("exceptions/missingtransformer.xml"),
					StreamUtils.getStringFromResource("exceptions/missingtransformer.xml"),
					transaction).send();
			while(transformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
			assertEquals("status should be:" + IJMSStatus.PRECONDITION_FAILED, IJMSStatus.PRECONDITION_FAILED, transformer.getStatus());
			assertEquals(StreamUtils.getStringFromResource("exceptions/missingtransformer.out"),
					transformer.getTransactionErrorMessage());
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy exception: 
	 * 	- login
	 *  - run the flow with one input message (the configuration xml is wrong for one step not supported tag in configuration)
	 *  - Chappy should return exception with forbidden.
	 *  - validate the return data
	 *  - logout
	 * 
	 */
	@Test
	public void exceptionXml2json2xmlStepsWithConfigurationWrongXMLConfiguration() {
		try {
			IJMSTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
			ChappyJMSTransformFlow transformer = new ChappyJMSTransformFlow(
					StreamUtils.getStringFromResource("xml2json2xml.xml"),
					StreamUtils.getStringFromResource("exceptions/xml2json2xmlwithconfigurations.xml"),
					transaction).send();
			while(transformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
			assertEquals("status should be:" + IJMSStatus.FORBIDDEN, IJMSStatus.FORBIDDEN, transformer.getStatus());
			assertEquals(StreamUtils.getStringFromResource("exceptions/xml2json2xmlwithconfiguration.out"),
					transformer.getTransactionErrorMessage());
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy exception: 
	 * 	- login
	 *  - run the flow with one input message (the configuration xml is wrong for one step parameters1 instead of parameters)
	 *  - Chappy should return exception with forbidden.
	 *  - validate the return data
	 *  - logout
	 * 
	 */
	@Test
	public void exceptionXml2json2xmlStepsWrongXMLConfigurationTest() {
		try {
			IJMSTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
			ChappyJMSTransformFlow transformer = new ChappyJMSTransformFlow(
					StreamUtils.getStringFromResource("exceptions/xml2json2xml.xml"),
					StreamUtils.getStringFromResource("exceptions/xml2json2xml.xml"),
					transaction).send();
			while(transformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
			assertEquals("status should be:" + IJMSStatus.FORBIDDEN, IJMSStatus.FORBIDDEN, transformer.getStatus());
			assertEquals(StreamUtils.getStringFromResource("exceptions/xml2json2xml.out"),
					transformer.getTransactionErrorMessage());
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
}
	
