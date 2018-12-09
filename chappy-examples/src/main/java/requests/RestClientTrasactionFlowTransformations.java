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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import com.fasterxml.jackson.core.JsonProcessingException;

import chappy.clients.common.transaction.ChappyClientTransactionHolder;
import chappy.clients.rest.ChappyRESTLogout;
import chappy.clients.rest.ChappyRESTRunExistingFlow;
import chappy.clients.rest.ChappyRESTTransformFlow;
import utils.ValidationUtils;
import chappy.utils.streams.StreamUtils;
import utils.RESTUtilsRequests;
import static utils.ValidationUtils.fail;
import static utils.ValidationUtils.assertEquals;


/**
 * Functional tests for Chappy clients with REST protocol.
 * @author Gabriel Dimitriu
 *
 */
public class RestClientTrasactionFlowTransformations {

	private int serverRESTPort = 8100;
	private String serverRESThost = "localhost";
	
	/**
	 * @param port the rest port of chappy server.
	 */
	public RestClientTrasactionFlowTransformations(final String host, final int port) {
		serverRESTPort = port;
		serverRESThost = host;
	}

	public void runAllTests() {
		push3CustomTransformersByTransactionAndMakeTransformation();
		push3CustomTransformersByTransactionPushFlowAndMakeTransformation();
		pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs();
		pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs();
		pushCustomSpliterEnvelopperByTransactionAndMakeIntegrationWithOneInput();
		pushCustomSpliterEnvelopperByTransactionAndMakeIntegrationWithOneInput();
		pushCustomSplitterByTransactionAndMakeIntegrationWitOneInputAndMutipleOutputs();
		exceptionMissingTransformerInTransactionException();
		exceptionXml2json2xmlStepsWithConfigurationWrongXMLConfiguration();
		exceptionXml2json2xmlStepsWrongXMLConfigurationTest();
	}
	
	/*
	 *---------------------------------------------------------------------------------
	 *
	 *From here there are the functional tests which shoul be the same on all protocols. 
	 * 
	 * --------------------------------------------------------------------------------
	 */
	
	/**
	 * chappy: 
	 * 	- login in chappy using REST
	 * 	- add 3 transformer steps and validate using REST
	 *  - run a flow with those steps using REST
	 *  - validate the return data
	 *  - logout from chappy using REST
	 * @throws FileNotFoundException
	 */
	public void push3CustomTransformersByTransactionAndMakeTransformation() {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("PreProcessingStep");
		addTransformers.add("ProcessingStep");
		addTransformers.add("PostProcessingStep");
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(serverRESThost, serverRESTPort);
		
		try {
			RESTUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
		} catch (Exception e) {
			fail("Could not add custom tranformer and validate " + e.getLocalizedMessage());
		}

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
	public void pushCustomSpliterEnvelopperByTransactionAndMakeIntegrationWithOneInput() {

		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("EnveloperStep");
		addTransformers.add("SplitterStep");
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(serverRESThost, serverRESTPort);
		
		try {
			RESTUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
		} catch (Exception e) {
			fail("Could not add custom tranformer and validate " + e.getLocalizedMessage());
		}

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
	public void pushCustomEnvelopperByTransactionAndMakeIntegrationWithMultipleInputs() {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("EnveloperStep");
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(serverRESThost, serverRESTPort);
		
		try {
			RESTUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
		} catch (Exception e) {
			fail("Could not add custom tranformer and validate " + e.getLocalizedMessage());
		}

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
	public void pushCustomSplitterByTransactionAndMakeIntegrationWitOneInputAndMutipleOutputs() {
		
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("SplitterStep");
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(serverRESThost, serverRESTPort);
		
		try {
			RESTUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
		} catch (Exception e) {
			fail("Could not add custom tranformer and validate " + e.getLocalizedMessage());
		}

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
			try {
				ValidationUtils.compareTwoListOfStrings(expected, actual);
			} catch (Exception e) {
				fail("Validation of the string failed " + e.getLocalizedMessage());
			}
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
	public void exceptionMissingTransformerInTransactionException() {
		
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(serverRESThost, serverRESTPort);
		
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
	public void exceptionXml2json2xmlStepsWithConfigurationWrongXMLConfiguration() {
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(serverRESThost, serverRESTPort);
		
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
	public void exceptionXml2json2xmlStepsWrongXMLConfigurationTest() {
		
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(serverRESThost, serverRESTPort);
		
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
	public void push3CustomTransformersByTransactionPushFlowAndMakeTransformation() {
		List<String> addTransformers = new ArrayList<>();
		addTransformers.add("PreProcessingStep");
		addTransformers.add("ProcessingStep");
		addTransformers.add("PostProcessingStep");
		ChappyClientTransactionHolder transaction = RESTUtilsRequests.chappyLogin(serverRESThost, serverRESTPort);
		
		try {
			RESTUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
		} catch (Exception e) {
			fail("Could not add custom tranformer and validate " + e.getLocalizedMessage());
		}
		
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
