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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import chappy.clients.common.transaction.ChappyClientTransactionHolder;
import chappy.clients.jms.ChappyJMSRunExistingFlow;
import chappy.clients.jms.ChappyJMSTransformFlow;
import chappy.clients.rest.ChappyRESTRunExistingFlow;
import chappy.clients.rest.ChappyRESTTransformFlow;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.services.IServiceJMS;
import chappy.persistence.providers.CustomTransformerStorageProvider;
import chappy.providers.transaction.TransactionProviders;
import chappy.utils.streams.StreamUtils;

/**
 * Functional tests for Chappy clients with JMS protocol.
 * @author Gabriel Dimitriu
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MixedJMSClientRESTClientTransactionTest {
	
	/** server port for connection */
	private int serverJMSPort = 61616;
		
	/** chappy server */
	private static MixedChappyServer server = new MixedChappyServer();
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		server.startAll();
	}
	
	@After
	public void cleanUp() {
		CustomTransformerStorageProvider.getInstance().cleanRepository();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDown() throws Exception {
		IServiceJMS jmsServer = server.getServerJMS();
		server.stopAll();
		FileUtils.deleteDirectory(new File(jmsServer.getBindindDirectory()));
		FileUtils.deleteDirectory(new File(jmsServer.getJournalDirectory()));
		FileUtils.deleteDirectory(new File(jmsServer.getLargeMessageDirectory()));
	}
	
	/*
	 *---------------------------------------------------------------------------------------
	 *
	 *Small tests for special request on JMS (first testing) 
	 * 
	 * --------------------------------------------------------------------------------------*/
	
	/**
	 * test chappy
	 * 	- login in chappy using JMS
	 * 	- logout from chappy using REST
	 */
	@Test
	public void chappyJLoginRLogout() {
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSPort);
			
			RESTUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy:
	 * 	- login in chappy using REST
	 * 	- logout from chappy using JMS
	 */
	@Test 
	public void chappyRLoginJLogout() {
		try {
			ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(server.getRestPort());
			
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
	}
	
	/*------------------------------------------------------------------------------------------------------
	 * 
	 * Mixed tests from the JMS and REST clients. 
	 * 
	 * ---------------------------------------------------------------------------------------------------*/
	/**
	 * test chappy:
	 *  - login in chappy using JMS
	 *  - add transformer and validate using REST
	 *  - logout from chappy using REST
	 */
	@Test
	public void chappyJLoginRAddTransformerRListRLogout() {
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSPort);
			
			List<String> addTransformers = new ArrayList<>();
			addTransformers.add("PreProcessingStep");
			
			RESTUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			
			RESTUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy:
	 *  - login in chappy using REST
	 *  - add transformer and validate using JMS
	 *  - logout from chappy using JMS
	 */
	@Test
	public void chappyRLoginJAddTransformerJListJLogout() {
		try {
			ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(server.getRestPort());
			
			List<String> addTransformers = new ArrayList<>();
			addTransformers.add("PreProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy:
	 *  - login in chappy using JMS
	 *  - add transformer using REST
	 *  - validate transformer using JMS
	 *  - logout from chappy using REST
	 */
	@Test 
	public void chappyJLoginRAddTransformerJListRLogout() {
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSPort);
			
			List<String> addTransformers = new ArrayList<>();
			addTransformers.add("PreProcessingStep");
			RESTUtilsRequests.chppyAddCustomTransformers(addTransformers, transaction);
			
			JMSUtilsRequests.chappyValidateTransformers(addTransformers, transaction);
			
			RESTUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy:
	 *  - login in chappy using REST
	 *  - add transformers using JMS
	 *  - validate transformer using REST
	 *  - logout from chappy using JMS
	 */
	@Test
	public void chappyRLoginJAddTransformerRListJLogout() {
		try {
			ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(server.getRestPort());
			
			List<String> addTransformers = new ArrayList<>();
			addTransformers.add("PreProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformers(addTransformers, transaction);
			
			RESTUtilsRequests.chappyValidateTransformers(addTransformers, transaction);
			
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy:
	 *  - login in chappy using REST
	 *  - add 1 transformer using JMS
	 *  - add 2 transformers using REST
	 *  - validate transformers using JMS
	 *  - run the flow using REST
	 *  - logout from chappy using JMS
	 */
	@Test
	public void chappyRLoginJAdd1TransformerRAdd2TransformerJListRFlowJLogout() {
		try {
			ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(server.getRestPort());
			
			List<String> addJms = new ArrayList<>();
			addJms.add("PreProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformers(addJms, transaction);
			
			List<String> addRest = new ArrayList<>();
			addRest.add("PostProcessingStep");
			addRest.add("ProcessingStep");
			RESTUtilsRequests.chppyAddCustomTransformers(addRest, transaction);
			
			addJms.addAll(addRest);
			JMSUtilsRequests.chappyValidateTransformers(addJms, transaction);
			
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
			
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy:
	 *  - login in chappy using JMS
	 *  - add 1 transformer using REST
	 *  - add 2 transformers using JMS
	 *  - validate transformers using REST
	 *  - run the flow using JMS
	 *  - validate the result of flow
	 *  - logout from chappy using REST
	 */
	@Test 
	public void chappyJLoginRAdd1TransformerJAdd2TransformerRListJFlowRLogout() {
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSPort);
			
			List<String> addRest = new ArrayList<>();
			addRest.add("PreProcessingStep");
			RESTUtilsRequests.chppyAddCustomTransformers(addRest, transaction);
			
			List<String> addJms = new ArrayList<>();
			addJms.add("PostProcessingStep");
			addJms.add("ProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformers(addJms, transaction);
			
			addRest.addAll(addJms);
			RESTUtilsRequests.chappyValidateTransformers(addRest, transaction);
			
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
			
			RESTUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy: 
	 * 	- login using JMS
	 * 	- add 2 custom transformers using JMS
	 *  - validate that they are on the server using REST
	 *  - stop the JMS server
	 *  - add 1 custom transformer using REST
	 *  - start the JMS server
	 *  - validate that all 3 custom transformers are on the server using REST
	 *  - run the flow with one input message using REST
	 *  - validate the result of flow
	 *  - logout using REST
	 * @throws FileNotFoundException
	 */
	@Test
	public void failOverChappyRLoginJAdd1TransformerJStopRAdd2TransformerJStartJListRFlowJLogout() {
		try {
			ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(server.getRestPort());
			
			List<String> addJms = new ArrayList<>();
			addJms.add("PreProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformers(addJms, transaction);
			
			//stop the CHAPPY JMS
			server.stopJMSServer();
			
			List<String> addRest = new ArrayList<>();
			addRest.add("PostProcessingStep");
			addRest.add("ProcessingStep");
			RESTUtilsRequests.chppyAddCustomTransformers(addRest, transaction);
			
			
			//start the CHAPPY JMS
			server.startJMSServer(false);
			
			addJms.addAll(addRest);
			JMSUtilsRequests.chappyValidateTransformers(addJms, transaction);
			
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
			
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy:
	 *  - login in chappy using JMS
	 *  - add 1 transformer using REST
	 *  - stop the REST server
	 *  - add 2 transformers using JMS
	 *  - start the REST server
	 *  - validate transformers using REST
	 *  - run the flow using JMS
	 *  - validate the result of flow
	 *  - logout from chappy using REST
	 */
	@Test
	public void failOVerChappyJLoginRAdd1TransformerRStopJAdd2TransformerRStartRListJFlowRLogout() {
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSPort);
			
			List<String> addRest = new ArrayList<>();
			addRest.add("PreProcessingStep");
			RESTUtilsRequests.chppyAddCustomTransformers(addRest, transaction);
			
			//stop the CHAPPY REST
			server.stopRESTServer();
			
			List<String> addJms = new ArrayList<>();
			addJms.add("PostProcessingStep");
			addJms.add("ProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformers(addJms, transaction);
			
			//start the CHAPPY REST
			server.startRESTServer(false);
			
			addRest.addAll(addJms);
			RESTUtilsRequests.chappyValidateTransformers(addRest, transaction);
			
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
			
			RESTUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy:
	 *  - login in chappy using JMS
	 *  - add 1 transformer using REST
	 *  - stop the REST server
	 *  - add 2 transformers using JMS
	 *  - start the REST server
	 *  - validate transformers using REST
	 *  - add the flow using REST
	 *  - run the flow using JMS
	 *  - validate the result of flow
	 *  - logout from chappy using REST
	 */
	@Test
	public void failOVerChappyJLoginRAdd1TransformerRStopJAdd2TransformerRStartRListRAddFlowJRFlowRLogout() {
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSPort);
			
			List<String> addRest = new ArrayList<>();
			addRest.add("PreProcessingStep");
			RESTUtilsRequests.chppyAddCustomTransformers(addRest, transaction);
			
			//stop the CHAPPY REST
			server.stopRESTServer();
			
			List<String> addJms = new ArrayList<>();
			addJms.add("PostProcessingStep");
			addJms.add("ProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformers(addJms, transaction);
			
			//start the CHAPPY REST
			server.startRESTServer(false);
			
			addRest.addAll(addJms);
			RESTUtilsRequests.chappyValidateTransformers(addRest, transaction);
			
			RESTUtilsRequests.chappyAddFlow("first_Flow",
					StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"), transaction);
			
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
			
			RESTUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy: 
	 * 	- login using JMS
	 * 	- add 2 custom transformers using JMS
	 *  - validate that they are on the server using REST
	 *  - stop the JMS server
	 *  - add 1 custom transformer using REST
	 *  - start the JMS server
	 *  - validate that all 3 custom transformers are on the server using REST
	 *  - add a flow using JMS
	 *  - run the flow with one input message using REST
	 *  - validate the result of flow
	 *  - logout using JMS
	 * @throws FileNotFoundException
	 */
	@Test
	public void failOverChappyRLoginJAdd1TransformerJStopRAdd2TransformerJStartJListJAddFlowRFlowJLogout() {
		try {
			ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(server.getRestPort());
			
			List<String> addJms = new ArrayList<>();
			addJms.add("PreProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformers(addJms, transaction);
			
			//stop the CHAPPY JMS
			server.stopJMSServer();
			
			List<String> addRest = new ArrayList<>();
			addRest.add("PostProcessingStep");
			addRest.add("ProcessingStep");
			RESTUtilsRequests.chppyAddCustomTransformers(addRest, transaction);
			
			
			//start the CHAPPY JMS
			server.startJMSServer(false);
			
			addJms.addAll(addRest);
			JMSUtilsRequests.chappyValidateTransformers(addJms, transaction);
			
			JMSUtilsRequests.chappyAddFlow("first_Flow",
					StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"),
					transaction);
			
			ChappyRESTTransformFlow transformer = new ChappyRESTRunExistingFlow(
					"blabla", "first_Flow", transaction).send();
			
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
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * test chappy: 
	 * 	- login using JMS
	 * 	- add 3 custom transformers using JMS
	 *  - validate that they are on the server using REST
	 *  - stop the JMS server
	 *  - add 1 custom transformer using REST
	 *  - start the JMS server
	 *  - validate that all 4 custom transformers are on the server using REST
	 *  - add a first flow using JMS
	 *  - add a second flow using JMS 
	 *  - restart the JMS server.
	 *  - run the first flow with one input message using REST
	 *  - validate the result of flow
	 *  - run the second flow with one input message using JMS
	 *  - validate the result of flow
	 *  - logout using JMS
	 * @throws FileNotFoundException
	 */
	@Test
	public void failOverChappyRLoginJAdd1TransformerJStopRAdd4TransformerJStartJListJAddFlowJStopJStartRFlowJFlowJLogout() {
		try {
			ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(server.getRestPort());
			
			List<String> addJms = new ArrayList<>();
			addJms.add("PreProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformers(addJms, transaction);
			
			//stop the CHAPPY JMS
			server.stopJMSServer();
			
			List<String> addRest = new ArrayList<>();
			addRest.add("PostProcessingStep");
			addRest.add("ProcessingStep");
			addRest.add("EnveloperStep");
			addRest.add("SplitterStep");
			RESTUtilsRequests.chppyAddCustomTransformers(addRest, transaction);
			
			//start the CHAPPY JMS
			server.startJMSServer(false);
			
			addJms.addAll(addRest);
			JMSUtilsRequests.chappyValidateTransformers(addJms, transaction);
			
			JMSUtilsRequests.chappyAddFlow("first_Flow",
					StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"),
					transaction);
			
			JMSUtilsRequests.chappyAddFlow("splitter",
					StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicSplitterEnveloperStep.xml"),
							transaction);
			
			//stop the CHAPPY
			server.stopJMSServer();
			server.stopRESTServer();
			
			//clean the repository
			CustomTransformerStorageProvider.getInstance().cleanRepository();
			//start the CHAPPY
			server.startJMSServer(false);
			server.startRESTServer(false);
			//load the repos
			CustomTransformerStorageProvider.getInstance().loadPersistenceCustomTransformers();
			TransactionProviders.getInstance().loadPersisted();
			
			ChappyRESTTransformFlow transformer = new ChappyRESTRunExistingFlow(
					"blabla", "first_Flow", transaction).send();

			if (transformer.getStatusCode() >= 0) {
				List<String> actual = transformer.getOutputResultAsString();
				assertEquals(1, actual.size());
				assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummyStepsResponse.txt"),
							actual.get(0));
			} else {
				fail("processing error on server");
			}
			
			ChappyJMSTransformFlow transformerJMS = (ChappyJMSTransformFlow) new ChappyJMSRunExistingFlow(
					StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
					"splitter", transaction).send().receivePoll(1000);

			if (transformerJMS.getStatusCode() >= 0) {
				List<String> actual = transformerJMS.getOutputResultAsString();
				assertEquals(1, actual.size());
				assertEquals(StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/enveloperStepResponse.txt"),
							actual.get(0));
			} else {
				fail("processing error on server");
			}
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
	}
}
	
