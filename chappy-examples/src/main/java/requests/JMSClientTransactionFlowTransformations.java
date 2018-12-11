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
import chappy.interfaces.jms.protocol.IJMSStatus;
import utils.ValidationUtils;
import chappy.utils.streams.StreamUtils;
import utils.JMSUtilsRequests;
import static utils.ValidationUtils.fail;
import static utils.ValidationUtils.assertEquals;

/**
 * Functional tests for Chappy clients with JMS protocol.
 * @author Gabriel Dimitriu
 *
 */
public class JMSClientTransactionFlowTransformations {

	/** server port for connection */
	private int serverJMSPort = 61616;
	
	/** server host for connection */
	private String serverJMSHost = "localhost";
	

	/**
	 * @param port the jms port of chappy server
	 */
	public JMSClientTransactionFlowTransformations(final String host, final int port) {
		serverJMSPort = port;
		serverJMSHost = host;
	}
	
	public void runAllTests() {
		jmsChappyLoginLogout();
		jmsChappyAddTransformer();
		push3CustomTransformersByTransactionAndFlowMakeTransformation();
		push3CustomTransformersByTransactionAndMakeTransformation();
		pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs();
		pushCustomSpliterEnvelopperByTransactionAndMakeIntegrationWithOneInput();
		pushCustomSplitterByTransactionAndMakeIntegrationWitOneInputAndMutipleOutputs();
		exceptionMissingTransformerInTransactionException();
		exceptionXml2json2xmlStepsWithConfigurationWrongXMLConfiguration();
		exceptionXml2json2xmlStepsWrongXMLConfiguration();
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
	public void jmsChappyLoginLogout() {
		System.out.println("jmsChappyLoginLogout is started");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSHost, serverJMSPort);
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		System.out.println("jmsChappyLoginLogout is finished");
	}
	
	/**
	 * test chappy:
	 *  - login in chappy using JMS
	 *  - add 1 transformer and validate using JMS
	 *  - logiout from chappy usin JMS
	 */
	public void jmsChappyAddTransformer() {
		System.out.println("jmsChappyAddTransformer is started");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSHost, serverJMSPort);
			List<String> addTransformers = new ArrayList<>();
			addTransformers.add("PreProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		System.out.println("jmsChappyAddTransformer is finished");
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
	public void push3CustomTransformersByTransactionAndMakeTransformation() {
		System.out.println("push3CustomTransformersByTransactionAndMakeTransformation is started");
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("PreProcessingStep");
		addTransformers.add("ProcessingStep");
		addTransformers.add("PostProcessingStep");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSHost, serverJMSPort);
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
		System.out.println("push3CustomTransformersByTransactionAndMakeTransformation is finished");
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
	public void push3CustomTransformersByTransactionAndFlowMakeTransformation() {
		System.out.println("push3CustomTransformersByTransactionAndFlowMakeTransformation is started");
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("PreProcessingStep");
		addTransformers.add("ProcessingStep");
		addTransformers.add("PostProcessingStep");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSHost, serverJMSPort);
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
		System.out.println("push3CustomTransformersByTransactionAndFlowMakeTransformation is finished");
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
	public void pushCustomSpliterEnvelopperByTransactionAndMakeIntegrationWithOneInput() {
		System.out.println("pushCustomSpliterEnvelopperByTransactionAndMakeIntegrationWithOneInput is started");
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("EnveloperStep");
		addTransformers.add("SplitterStep");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSHost, serverJMSPort);
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
		System.out.println("pushCustomSpliterEnvelopperByTransactionAndMakeIntegrationWithOneInput is finished");
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
	public void pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs() {
		System.out.println("pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs is started");
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("EnveloperStep");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSHost, serverJMSPort);
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
		System.out.println("pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs is finished");
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
	public void pushCustomSplitterByTransactionAndMakeIntegrationWitOneInputAndMutipleOutputs() {
		System.out.println("pushCustomSplitterByTransactionAndMakeIntegrationWitOneInputAndMutipleOutputs is started");
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("SplitterStep");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSHost, serverJMSPort);
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
				ValidationUtils.compareTwoListOfStrings(expected, actual);
			} else {
				fail("processing error on server");
			}
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		System.out.println("pushCustomSplitterByTransactionAndMakeIntegrationWitOneInputAndMutipleOutputs is finished");
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
	public void exceptionMissingTransformerInTransactionException() {
		System.out.println("exceptionMissingTransformerInTransactionException is started");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSHost, serverJMSPort);
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
		System.out.println("exceptionMissingTransformerInTransactionException is finished");
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
	public void exceptionXml2json2xmlStepsWithConfigurationWrongXMLConfiguration() {
		System.out.println("exceptionXml2json2xmlStepsWithConfigurationWrongXMLConfiguration is started");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSHost, serverJMSPort);
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
		System.out.println("exceptionXml2json2xmlStepsWithConfigurationWrongXMLConfiguration is finished");
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
	public void exceptionXml2json2xmlStepsWrongXMLConfiguration() {
		System.out.println("exceptionXml2json2xmlStepsWrongXMLConfiguration is started");
		try {
			ChappyClientTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSHost, serverJMSPort);
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
		System.out.println("exceptionXml2json2xmlStepsWrongXMLConfiguration is finished");
	}
}
	
