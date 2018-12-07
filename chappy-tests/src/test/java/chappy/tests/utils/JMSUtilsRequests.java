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
package chappy.tests.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import javax.jms.JMSException;

import chappy.clients.common.transaction.ChappyClientTransactionHolder;
import chappy.clients.jms.ChappyJMSAddFlow;
import chappy.clients.jms.ChappyJMSAddTransformer;
import chappy.clients.jms.ChappyJMSListTransformers;
import chappy.clients.jms.ChappyJMSLogin;
import chappy.clients.jms.ChappyJMSLogout;
import chappy.interfaces.jms.IJMSTransactionHolder;
import chappy.interfaces.jms.protocol.IJMSStatus;

/**
 * JMS requests utils for testing purpose.
 * @author Gabriel Dimitriu
 *
 */
public final class JMSUtilsRequests {

	/**OK message */
	public static String CHAPPY_RECEIVED_OK = "Chappy request had been received OK"; 
	/**
	 * 
	 */
	public JMSUtilsRequests() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * test wrapper for chappy login
	 * @return transaction holder to be pass
	 * @throws Exception
	 */
	public static ChappyClientTransactionHolder chappyLogin(final int serverPort) throws Exception {
		ChappyJMSLogin login = new ChappyJMSLogin("system", "system", true);
		login.createConnectionToServer("localhost", serverPort);
		login.send();
		while(login.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
		
		assertEquals("request did not completed", IJMSStatus.OK, login.getStatus());			
		assertNull("there should be no exeception", login.getTransactionException());
		assertFalse("should be no exception", login.hasException());
		assertEquals("reply message is wrong", CHAPPY_RECEIVED_OK, login.getTransactionErrorMessage());
		
		if (!login.hasException()) {
			 ChappyClientTransactionHolder transaction =  login.createTransactionHolder();
			 assertEquals("user should be system", "system", transaction.getCookie().getUserName());
			 return transaction;
		}
		return null;
	}
	
	/**
	 * add custom transformers to chappy
	 * @param addTransformers the list of transformers to add
	 * @param transaction the client transaction 
	 * @return chappy transaction holder
	 */
	public static ChappyClientTransactionHolder chppyAddCustomTransformers(final List<String> addTransformers,
			final ChappyClientTransactionHolder transaction) {
		
		// add transformers in transaction
		for (String transf : addTransformers) {
			ChappyJMSAddTransformer addTransformer = new ChappyJMSAddTransformer(transf, transaction);
			try {
				addTransformer.setTransformer(transf, RestCallsUtils.CUSTOM_TRANSFORMERS_DUMMY);
				addTransformer.send();
				while(addTransformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
				assertEquals("add transformer " + transf + " exception", IJMSStatus.OK,  addTransformer.getStatus());
			} catch (IOException | JMSException | InterruptedException e) {
				fail("exception occured at add transformer" + e.getLocalizedMessage());
			}
		}
		return transaction;
	}
	
	/**
	 * add custom transformers and list and validate them.
	 * @param addTransformers list of transformers to add
	 * @param transaction the client transaction
	 * @return chappy transaction holder
	 */
	public static ChappyClientTransactionHolder chppyAddCustomTransformersAndValidate(final List<String> addTransformers,
			final ChappyClientTransactionHolder transaction) {
		
		// add transformers in transaction
		for (String transf : addTransformers) {
			ChappyJMSAddTransformer addTransformer = new ChappyJMSAddTransformer(transf, transaction);
			try {
				addTransformer.setTransformer(transf, RestCallsUtils.CUSTOM_TRANSFORMERS_DUMMY);
				addTransformer.send();
				while(addTransformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
				assertEquals("add transformer " + transf + " exception", IJMSStatus.OK,  addTransformer.getStatus());
			} catch (IOException | JMSException | InterruptedException e) {
				fail("exception occured at add transformer" + e.getLocalizedMessage());
			}
		}
		chappyValidateTransformers(addTransformers, transaction);
		return transaction;
	}

	/**
	 * validate the added transformers on chappy
	 * @param addTransformers the list of transformers to validate
	 * @param transaction the client transaction
	 */
	public static void chappyValidateTransformers(final List<String> addTransformers,
			final ChappyClientTransactionHolder transaction) {
		try {
			// list the added transformers
			ChappyJMSListTransformers listTransformers = new ChappyJMSListTransformers(transaction).send();
			while(listTransformers.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
			assertEquals("internal error for list transformers", IJMSStatus.OK, listTransformers.getStatus());
			List<String> transformers = listTransformers.getListOfTransformersName();
			TestUtils.compareTwoListWithoutOrder(addTransformers, transformers);
		} catch (Exception e) {
			fail("exception occured at add transformer" + e.getLocalizedMessage());
		}
	}
	
	/**
	 * test wrapper for chappy logout
	 * @param transaction holder from login
	 * @throws Exception
	 */
	public static void chappyLogout(final IJMSTransactionHolder transaction) throws Exception {
		ChappyJMSLogout logout = new ChappyJMSLogout(transaction);
		logout.send();
		while(logout.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);

		assertEquals("request did not completed", IJMSStatus.OK, logout.getStatus());			
		assertNull("there should be no exeception", logout.getTransactionException());
		assertFalse("should be no exception", logout.hasException());
		assertEquals("reply message is wrong", JMSUtilsRequests.CHAPPY_RECEIVED_OK, logout.getTransactionErrorMessage());
	}
	
	/**
	 * send a flow to chappy
	 * @param flowName
	 * @param configuration
	 * @param transaction
	 * @return transaction
	 * @throws Exception
	 */
	public static IJMSTransactionHolder chappyAddFlow(final String flowName, final String configuration,
			final IJMSTransactionHolder transaction) throws Exception {
		ChappyJMSAddFlow flow = new ChappyJMSAddFlow(flowName, configuration, transaction);
		flow.send();
		while(flow.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
		assertEquals("add flow " + flowName + " did not completed", IJMSStatus.OK, flow.getStatus());
		return transaction;
	}
	
}
