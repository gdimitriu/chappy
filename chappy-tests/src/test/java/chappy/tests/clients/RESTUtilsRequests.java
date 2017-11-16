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

import chappy.clients.rest.ChappyRESTAddTransformer;
import chappy.clients.rest.ChappyRESTListTransformers;
import chappy.clients.rest.ChappyRESTLogin;
import chappy.clients.rest.ChappyRESTLogout;
import chappy.interfaces.rest.IRESTTransactionHolder;
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
	public static IRESTTransactionHolder chappyLogin(final int port) {
		ChappyRESTLogin login = new ChappyRESTLogin("gdimitriu", "password", true);
		try {
			login.createConnectionToServer("localhost", port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		login.send();
		assertEquals("wrong authentication", login.getStatusCode(), Status.OK.getStatusCode());
		assertEquals("wrong user", "gdimitriu", login.getCookie().getUserName());
		return login.createTransactionHolder();		
	}

	/**
	 * login and add custom transformers and list and validate them on REST.
	 * @param addTransformers (list of custom transformers)
	 * @param port in which the transformers should be added.
	 * @return chappy transaction holder
	 */
	public static IRESTTransactionHolder chappyLoginAddCustomTransformers(final List<String> addTransformers, final int port) {
		IRESTTransactionHolder transaction = chappyLogin(port);
		return chappyAddCustomTransformers(addTransformers, transaction);
	}
	
	/**
	 * add customs transformers to chappy on REST.
	 * @param addTransformers (list of custom transformers)
	 * @param transaction in which the transformers should be added.
	 * @return transaction.
	 */
	public static IRESTTransactionHolder chappyAddCustomTransformers(final List<String> addTransformers, final IRESTTransactionHolder transaction) {
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
	
	/**
	 * logout from the chappy using REST.
	 * @param transaction to logout
	 */
	public static void chappyLogout(final IRESTTransactionHolder transaction) {
		ChappyRESTLogout logout = new ChappyRESTLogout(transaction).send();
		assertEquals("could not logout", logout.getStatusCode(), Status.OK.getStatusCode());
	}
}
