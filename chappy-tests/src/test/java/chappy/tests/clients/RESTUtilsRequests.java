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

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import chappy.clients.common.transaction.ChappyClientTransactionHolder;
import chappy.clients.rest.ChappyRESTAddFlow;
import chappy.clients.rest.ChappyRESTAddTransformer;
import chappy.clients.rest.ChappyRESTListTransformers;
import chappy.clients.rest.ChappyRESTLogin;
import chappy.clients.rest.ChappyRESTLogout;
import chappy.tests.rest.transformers.test.RestCallsUtils;
import chappy.tests.utils.TestUtils;

/**
 * REST requests utils for testing purpose.
 * @author Gabriel Dimitriu
 *
 */
public final class RESTUtilsRequests {

	/**
	 * 
	 */
	public RESTUtilsRequests() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * login into the chappy using REST.
	 * @param port 
	 * @return transaction
	 */
	public static ChappyClientTransactionHolder chappyLogin(final int port) {
		ChappyRESTLogin login = new ChappyRESTLogin("gdimitriu", "password", true);
		try {
			login.createConnectionToServer("localhost", port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		login.send();
		assertEquals("wrong authentication", Status.OK.getStatusCode(), login.getStatusCode());
		assertEquals("wrong user", "gdimitriu", login.getCookie().getUserName());
		return login.createTransactionHolder();		
	}

	/**
	 * login and add custom transformers and list and validate them
	 * @param addTransformers (list of custom transformers)
	 * @param port in which the transformers should be added.
	 * @return chappy transaction holder
	 */
	public static ChappyClientTransactionHolder chappyLoginAddCustomTransformers(final List<String> addTransformers, final int port) {
		ChappyClientTransactionHolder transaction = chappyLogin(port);
		return chppyAddCustomTransformersAndValidate(addTransformers, transaction);
	}
	
	/**
	 * add custom transformers to chappy
	 * @param addTransformers the list of transformers to add
	 * @param transaction the client transaction 
	 * @return chappy transaction holder
	 */
	public static ChappyClientTransactionHolder chppyAddCustomTransformers(final List<String> addTransformers, final ChappyClientTransactionHolder transaction) {
		// add transformers in transaction
		for (String transf : addTransformers) {
			ChappyRESTAddTransformer addTransformer = new ChappyRESTAddTransformer(transf, transaction);
			try {
				addTransformer.setTransformer(transf, RestCallsUtils.CUSTOM_TRANSFORMERS_DUMMY);
				addTransformer.send();
				assertEquals("add transformer " + transf + " exception", Status.OK.getStatusCode(), addTransformer.getStatusCode());
			} catch (IOException e) {
				e.printStackTrace();
				fail("exception occured at add transformer" + e.getLocalizedMessage());
			}
		}
		return transaction;
	}
	
	/**
	 * validate the added transformers on chappy
	 * @param addTransformers the list of transformers to validate
	 * @param transaction the client transaction
	 */
	public static void chappyValidateTransformers(final List<String> addTransformers,
			final ChappyClientTransactionHolder transaction) {
		// list the added transformers
		ChappyRESTListTransformers listTransformers = new ChappyRESTListTransformers(transaction).send();
		assertEquals("internal error for list transformers", Status.OK.getStatusCode(), listTransformers.getStatusCode());
		List<String> transformers = listTransformers.getListOfTransformersName();
		TestUtils.compareTwoListWithoutOrder(addTransformers, transformers);
	}
	
	/**
	 * add custom transformers and list and validate them.
	 * @param addTransformers list of transformers to add
	 * @param transaction the client transaction
	 * @return chappy transaction holder
	 */
	public static ChappyClientTransactionHolder chppyAddCustomTransformersAndValidate(final List<String> addTransformers, final ChappyClientTransactionHolder transaction) {
		// add transformers in transaction
		for (String transf : addTransformers) {
			ChappyRESTAddTransformer addTransformer = new ChappyRESTAddTransformer(transf, transaction);
			try {
				addTransformer.setTransformer(transf, RestCallsUtils.CUSTOM_TRANSFORMERS_DUMMY);
				addTransformer.send();
				assertEquals("add transformer " + transf + " exception", Status.OK.getStatusCode(), addTransformer.getStatusCode());
			} catch (IOException e) {
				e.printStackTrace();
				fail("exception occured at add transformer" + e.getLocalizedMessage());
			}
		}

		chappyValidateTransformers(addTransformers, transaction);
		return transaction;
	}
	
	/**
	 * @param flowName
	 * @param flowConfiguration
	 * @param transaction
	 * @return
	 */
	public static ChappyClientTransactionHolder chappyAddFlow(final String flowName, final String flowConfiguration, final ChappyClientTransactionHolder transaction) {

		ChappyRESTAddFlow addFlow = new ChappyRESTAddFlow(flowName, flowConfiguration, transaction);
		addFlow.send();
		assertEquals("add flow " + flowName + " exception", Status.OK.getStatusCode(), addFlow.getStatusCode());
		return transaction;
	}
	/**
	 * logout from the chappy
	 * @param transaction to logout
	 */
	public static void chappyLogout(final ChappyClientTransactionHolder transaction) {
		ChappyRESTLogout logout = new ChappyRESTLogout(transaction.getRestTransaction()).send();
		assertEquals("could not logout", Status.OK.getStatusCode(), logout.getStatusCode());
	}
}
