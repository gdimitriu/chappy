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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import chappy.clients.common.transaction.ChappyClientTransactionHolder;
import chappy.clients.jms.ChappyJMSAddTransformer;
import chappy.clients.jms.ChappyJMSListTransformers;
import chappy.clients.jms.ChappyJMSRunExistingFlow;
import chappy.clients.jms.ChappyJMSTransformFlow;
import chappy.configurations.providers.SystemConfigurationProvider;
import chappy.configurations.system.SystemConfigurations;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.services.IServiceJMS;
import chappy.interfaces.services.IServiceServer;
import chappy.persistence.providers.CustomTransformerStorageProvider;
import chappy.providers.transaction.TransactionProviders;
import chappy.services.servers.jms.ServerJMS;
import chappy.tests.rest.transformers.test.RestCallsUtils;
import chappy.tests.utils.TestUtils;
import chappy.utils.streams.StreamUtils;

/**
 * Functional tests for Chappy clients with JMS protocol.
 * @author Gabriel Dimitriu
 *
 */
public class JMSClientTransactionFlowTransformationsTest {

	private static IServiceServer server = null;
	
	/** server port for connection */
	private static int serverPort = 61616;
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		SystemConfigurationProvider.getInstance().readSystemConfiguration(
				JMSClientTransactionFlowTransformationsTest.class.getClassLoader().getResourceAsStream("systemTestConfiguration.xml"));
		SystemConfigurations configuration = SystemConfigurationProvider.getInstance().getSystemConfiguration();
		server = new ServerJMS();
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
		Thread.currentThread().sleep(1000);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDown() throws Exception {
		server.stopServer();
		FileUtils.deleteDirectory(new File(((IServiceJMS) server).getBindindDirectory()));
		FileUtils.deleteDirectory(new File(((IServiceJMS) server).getJournalDirectory()));
		FileUtils.deleteDirectory(new File(((IServiceJMS) server).getLargeMessageDirectory()));
	}
	
	/*
	 *---------------------------------------------------------------------------------
	 *
	 *Small tests for special request on JMS (first testing) 
	 * 
	 * --------------------------------------------------------------------------------
	 */
	/**
	 * test chappy:
	 * 	- login in chappy using JMS
	 * 	- logout from chappy using JMS
	 */
	@Test
	public void jmsChappyLoginLogout() {
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy:
	 *  - login in chappy using JMS
	 *  - add 1 transformer and validate using JMS
	 *  - logiout from chappy usin JMS
	 */
	@Test
	public void jmsChappyAddTransformer() {
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
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
	 * 	- login in chappy using JMS
	 * 	- add 3 transformer steps and validate using JMS
	 *  - run a flow with those steps using JMS
	 *  - validate the result of flow
	 *  - logout from chappy using JMS
	 * @throws FileNotFoundException
	 */
	@Test
	public void push3CustomTransformersByTransactionAndMakeTransformation() throws FileNotFoundException {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("PreProcessingStep");
		addTransformers.add("ProcessingStep");
		addTransformers.add("PostProcessingStep");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
			JMSUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			ChappyJMSTransformFlow transformer = new ChappyJMSTransformFlow(
					"blabla",
					StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"),
					transaction).send();
			while(transformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
			if (transformer.getStatusCode() >= 0) {
				List<String> actual = transformer.getOutputResultAsString();
				assertEquals(1, actual.size());
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
	 * 	- login in chappy using JMS
	 * 	- add 3 transformer steps and validate using JMS
	 *  - add a flow using JMS
	 *  - run a flow with those steps using JMS
	 *  - validate the result of flow
	 *  - logout from chappy using JMS
	 * @throws FileNotFoundException
	 */
	@Test
	public void push3CustomTransformersByTransactionAndFlowMakeTransformation() throws FileNotFoundException {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("PreProcessingStep");
		addTransformers.add("ProcessingStep");
		addTransformers.add("PostProcessingStep");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
			JMSUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			
			JMSUtilsRequests.chappyAddFlow("first_Flow",
					StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"),
					transaction);
			
			ChappyJMSTransformFlow transformer = new ChappyJMSRunExistingFlow(
					"blabla", "first_Flow", transaction).send();
			while(transformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
			if (transformer.getStatusCode() >= 0) {
				List<String> actual = transformer.getOutputResultAsString();
				assertEquals(1, actual.size());
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
	 * 	- login in chappy using JMS
	 * 	- add enveloper and splitter and validate using JMS
	 *  - run a flow with those steps with one input as split-envelope using JMS
	 *  - validate the return data
	 *  - logout from chappy using JMS
	 * @throws FileNotFoundException
	 */
	@Test
	public void pushCustomSpliterEnvelopperByTransactionAndMakeIntegrationWithOneInput() throws FileNotFoundException {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("EnveloperStep");
		addTransformers.add("SplitterStep");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
			JMSUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			ChappyJMSTransformFlow transformer = new ChappyJMSTransformFlow(
					StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
					StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicSplitterEnveloperStep.xml"),
					transaction).send();
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
	 * 	- login in chappy using JMS
	 * 	- add enveloper step and validate using JMS
	 *  - run a flow with enveloper with two input messages using JMS
	 *  - validate the return data
	 *  - logout from chappy using JMS
	 * @throws FileNotFoundException
	 */
	@Test
	public void pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs() throws FileNotFoundException {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("EnveloperStep");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
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
	 * 	- login in chappy using JMS
	 * 	- add splitter step and validate using JMS
	 *  - run a flow with enveloper with one input messages using JMS
	 *  - validate the return data
	 *  - logout from chappy using JMS
	 * @throws FileNotFoundException
	 */
	@Test
	public void pushCustomSplitterByTransactionAndMakeIntegrationWitOneInputAndMutipleOutputs()
			throws FileNotFoundException {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("SplitterStep");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
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
	 * 	- login in chappy using JMS
	 * 	- add 2 custom transformers and validate using JMS
	 *  - fail-over JMS server
	 *  - add 1 custom transformer using JMS
	 *  - validate that all 3 custom transformers are on the server using JMS
	 *  - run the flow with one input message using JMS
	 *  - validate the return data
	 *  - logout from chappy using JMS
	 * @throws FileNotFoundException
	 */
	@Test
	public void failOverAfterPut2CustomTransformersAddANewOneAndMakeTransformation() throws Exception {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("PreProcessingStep");
		addTransformers.add("PostProcessingStep");
		ChappyClientTransactionHolder transaction = null;
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
			while(addTransformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
			assertEquals("add transformer " +  "ProcessingStep" + " exception", IJMSStatus.OK, addTransformer.getStatus());
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
			assertEquals("internal error for list transformers",IJMSStatus.OK, listTransformers.getStatus());
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
			assertEquals(1, actual.size());
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummyStepsResponse.txt"),
						actual.get(0));
		} else {
			fail("processing error on server");
		}
		JMSUtilsRequests.chappyLogout(transaction);
	}
	
	/**
	 * test chappy: 
	 * 	- login in chappy using JMS
	 * 	- add 2 custom transformers and validate using JMS
	 *  - fail-over JMS server
	 *  - add 1 custom transformer using JMS
	 *  - validate that all 3 custom transformers are on the server using JMS
	 *  - add a flow using JMS
	 *  - run the flow with one input message using JMS
	 *  - validate the return data
	 *  - logout from chappy using JMS
	 * @throws FileNotFoundException
	 */
	@Test
	public void failOverAfterPut2CustomTransformersAddANewOneAndFlowMakeTransformation() throws Exception {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("PreProcessingStep");
		addTransformers.add("PostProcessingStep");
		ChappyClientTransactionHolder transaction = null;
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
			while(addTransformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
			assertEquals("add transformer " +  "ProcessingStep" + " exception", IJMSStatus.OK, addTransformer.getStatus());
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
			assertEquals("internal error for list transformers",IJMSStatus.OK, listTransformers.getStatus());
			List<String> transformers = listTransformers.getListOfTransformersName();
			TestUtils.compareTwoListWithoutOrder(addTransformers, transformers);
		} catch (Exception e) {
			fail("exception occured at add transformer" + e.getLocalizedMessage());
		}
		
		JMSUtilsRequests.chappyAddFlow("first_Flow",
				StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"),
				transaction);
		
		//transform
		ChappyJMSTransformFlow transformer = new ChappyJMSRunExistingFlow(
				"blabla", "first_Flow",	transaction).send();
		while(transformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
		if (transformer.getStatusCode() >= 0) {
			List<String> actual = transformer.getOutputResultAsString();
			assertEquals(1, actual.size());
			assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummyStepsResponse.txt"),
						actual.get(0));
		} else {
			fail("processing error on server");
		}
		JMSUtilsRequests.chappyLogout(transaction);
	}
	
	/**
	 * test chappy exception: 
	 * 	- login in chappy using JMS
	 *  - run the flow with one input message (the transformer is missing) using JMS
	 *		(Chappy should return exception with precondition failed.)
	 *  - validate the return data
	 *  - logout from chappy using JMS
	 * 
	 */
	@Test
	public void exceptionMissingTransformerInTransactionException() {
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
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
	 * 	- login in chappy using JMS
	 *  - run the flow with one input message (the configuration xml is wrong for one step not supported tag in configuration) using JMS
	 *		(Chappy should return exception with forbidden.)
	 *  - validate the return data
	 *  - logout from chappy using JMS
	 * 
	 */
	@Test
	public void exceptionXml2json2xmlStepsWithConfigurationWrongXMLConfiguration() {
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
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
	 * 	- login in chappy using JMS
	 *  - run the flow with one input message (the configuration xml is wrong for one step parameters1 instead of parameters) using JMS
	 *		(Chappy should return exception with forbidden.)
	 *  - validate the return data
	 *  - logout from chapyy using  JMS
	 * 
	 */
	@Test
	public void exceptionXml2json2xmlStepsWrongXMLConfigurationTest() {
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverPort);
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
	
