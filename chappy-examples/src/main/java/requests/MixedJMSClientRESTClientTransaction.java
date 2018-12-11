/**
    Copyright (c) 2018 Gabriel Dimitriu All rights reserved.
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
package requests;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import chappy.clients.common.transaction.ChappyClientTransactionHolder;
import chappy.clients.jms.ChappyJMSRunExistingFlow;
import chappy.clients.jms.ChappyJMSTransformFlow;
import chappy.clients.rest.ChappyRESTRunExistingFlow;
import chappy.clients.rest.ChappyRESTTransformFlow;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.utils.streams.StreamUtils;
import utils.JMSUtilsRequests;
import utils.RESTUtilsRequests;
import static utils.ValidationUtils.fail;
import static utils.ValidationUtils.assertEquals;

/**
 * Functional tests for Chappy clients with JMS protocol.
 * @author Gabriel Dimitriu
 *
 */
public class MixedJMSClientRESTClientTransaction {
	
	/** JMS server port for connection */
	private int serverJMSPort = 61616;
	
	/** REST server port */
	private int serverRESTPort = 8100;
	
	/** server host */
	private String serverHost = "localhost";
	
	/**
	 * @param restPort the rest port for the chappy server
	 * @param jmsPort thee jms port for the chappy server
	 */
	public MixedJMSClientRESTClientTransaction (final String host, final int restPort, final int jmsPort) {
		serverJMSPort = jmsPort;
		serverRESTPort = restPort;
		serverHost = host;
	}
	
	public void runAllTests() {
		chappyJLoginRLogout();
		chappyRLoginJLogout();
		chappyJLoginRAddTransformerJListRLogout();
		chappyJLoginRAddTransformerRListRLogout();
		chappyRLoginJAddTransformerJListJLogout();
		chappyRLoginJAddTransformerRListJLogout();
		chappyRLoginJAddTransformerJListJLogout();
		chappyJLoginRAdd1TransformerJAdd2TransformerRListJFlowRLogout();
		chappyRLoginJAdd1TransformerRAdd2TransformerJListRFlowJLogout();
		chappyRLoginJAdd1TransformerRAdd4TransformerJListJAddFlowRFlowJFlowJLogout();
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
	public void chappyJLoginRLogout() {
		System.out.println("chappyJLoginRLogout is started");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverHost, serverJMSPort);
			
			RESTUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
		System.out.println("chappyJLoginRLogout is finished");
	}
	
	/**
	 * test chappy:
	 * 	- login in chappy using REST
	 * 	- logout from chappy using JMS
	 */
	public void chappyRLoginJLogout() {
		System.out.println("chappyRLoginJLogout is started");
		try {
			ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(serverHost, serverRESTPort);
			
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
		System.out.println("chappyRLoginJLogout is finished");
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
	public void chappyJLoginRAddTransformerRListRLogout() {
		System.out.println("chappyJLoginRAddTransformerRListRLogout is started");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverHost, serverJMSPort);
			
			List<String> addTransformers = new ArrayList<>();
			addTransformers.add("PreProcessingStep");
			
			RESTUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			
			RESTUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
		System.out.println("chappyJLoginRAddTransformerRListRLogout is finished");
	}
	
	/**
	 * test chappy:
	 *  - login in chappy using REST
	 *  - add transformer and validate using JMS
	 *  - logout from chappy using JMS
	 */
	public void chappyRLoginJAddTransformerJListJLogout() {
		System.out.println("chappyRLoginJAddTransformerJListJLogout is started");
		try {
			ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(serverHost, serverRESTPort);
			
			List<String> addTransformers = new ArrayList<>();
			addTransformers.add("PreProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
		System.out.println("chappyRLoginJAddTransformerJListJLogout is finished");
	}
	
	/**
	 * test chappy:
	 *  - login in chappy using JMS
	 *  - add transformer using REST
	 *  - validate transformer using JMS
	 *  - logout from chappy using REST
	 */
	public void chappyJLoginRAddTransformerJListRLogout() {
		System.out.println("chappyJLoginRAddTransformerJListRLogout is started");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverHost, serverJMSPort);
			
			List<String> addTransformers = new ArrayList<>();
			addTransformers.add("PreProcessingStep");
			RESTUtilsRequests.chppyAddCustomTransformers(addTransformers, transaction);
			
			JMSUtilsRequests.chappyValidateTransformers(addTransformers, transaction);
			
			RESTUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
		System.out.println("chappyJLoginRAddTransformerJListRLogout is finished");
	}
	
	/**
	 * test chappy:
	 *  - login in chappy using REST
	 *  - add transformers using JMS
	 *  - validate transformer using REST
	 *  - logout from chappy using JMS
	 */
	public void chappyRLoginJAddTransformerRListJLogout() {
		System.out.println("chappyRLoginJAddTransformerRListJLogout is started");
		try {
			ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(serverHost, serverRESTPort);
			
			List<String> addTransformers = new ArrayList<>();
			addTransformers.add("PreProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformers(addTransformers, transaction);
			
			RESTUtilsRequests.chappyValidateTransformers(addTransformers, transaction);
			
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			fail(e.getLocalizedMessage());
		}
		System.out.println("chappyRLoginJAddTransformerRListJLogout is finished");
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
	public void chappyRLoginJAdd1TransformerRAdd2TransformerJListRFlowJLogout() {
		System.out.println("chappyRLoginJAdd1TransformerRAdd2TransformerJListRFlowJLogout is started");
		try {
			ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(serverHost, serverRESTPort);
			
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
		System.out.println("chappyRLoginJAdd1TransformerRAdd2TransformerJListRFlowJLogout is finished");
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
	public void chappyJLoginRAdd1TransformerJAdd2TransformerRListJFlowRLogout() {
		System.out.println("chappyJLoginRAdd1TransformerJAdd2TransformerRListJFlowRLogout is started");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverHost, serverJMSPort);
			
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
		System.out.println("chappyJLoginRAdd1TransformerJAdd2TransformerRListJFlowRLogout is finished");
	}
	
	/**
	 * test chappy: 
	 * 	- login using JMS
	 * 	- add 3 custom transformers using JMS
	 *  - validate that they are on the server using REST
	 *  - add 1 custom transformer using REST
	 *  - validate that all 4 custom transformers are on the server using REST
	 *  - add a first flow using JMS
	 *  - add a second flow using JMS 
	 *  - run the first flow with one input message using REST
	 *  - validate the result of flow
	 *  - run the second flow with one input message using JMS
	 *  - validate the result of flow
	 *  - logout using JMS
	 * @throws FileNotFoundException
	 */
	public void chappyRLoginJAdd1TransformerRAdd4TransformerJListJAddFlowRFlowJFlowJLogout() {
		System.out.println("chappyRLoginJAdd1TransformerRAdd4TransformerJListJAddFlowRFlowJFlowJLogout is started");
		try {
			ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(serverHost, serverRESTPort);
			
			List<String> addJms = new ArrayList<>();
			addJms.add("PreProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformers(addJms, transaction);
			
			List<String> addRest = new ArrayList<>();
			addRest.add("PostProcessingStep");
			addRest.add("ProcessingStep");
			addRest.add("EnveloperStep");
			addRest.add("SplitterStep");
			RESTUtilsRequests.chppyAddCustomTransformers(addRest, transaction);
			
			addJms.addAll(addRest);
			JMSUtilsRequests.chappyValidateTransformers(addJms, transaction);
			
			JMSUtilsRequests.chappyAddFlow("first_Flow",
					StreamUtils.getStringFromResource("transaction/dynamic/dummytransformers/dummySteps.xml"),
					transaction);
			
			JMSUtilsRequests.chappyAddFlow("splitter",
					StreamUtils.getStringFromResource("transaction/dynamic/multipleinputoutput/basicSplitterEnveloperStep.xml"),
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
		System.out.println("chappyRLoginJAdd1TransformerRAdd4TransformerJListJAddFlowRFlowJFlowJLogout is finished");
	}
}
	
