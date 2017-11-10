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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import chappy.clients.jms.ChappyJMSAddTransformer;
import chappy.clients.jms.ChappyJMSLogin;
import chappy.clients.jms.ChappyJMSLogout;
import chappy.configurations.providers.SystemConfigurationProvider;
import chappy.configurations.system.SystemConfigurations;
import chappy.interfaces.jms.IJMSTransactionHolder;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.services.IServiceServer;
import chappy.persistence.providers.CustomTransformerStorageProvider;
import chappy.policy.provider.JMSRuntimeResourceProvider;
import chappy.services.servers.jms.ServerJMS;
import chappy.services.servers.jms.resources.TransactionRouter;
import chappy.services.servers.jms.resources.tranform.AddTransformer;
import chappy.services.servers.jms.resources.tranform.Authentication;
import chappy.services.servers.jms.resources.tranform.TransformFlow;
import chappy.tests.rest.transformers.test.RestCallsUtils;

/**
 * Functional tests for Chappy clients with JMS protocol.
 * @author Gabriel Dimitriu
 *
 */
public class JMSClientTransactionFlowTransformationsTest {

	private IServiceServer server = null;
	
	/** server port for connection */
	private int serverPort = 61616;
	
	/**OK message */
	private String CHAPPY_RECEIVED_OK = "Chappy request had been received OK"; 

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
	
	/**
	 * test wrapper for chappy login
	 * @return transaction holder to be pass
	 * @throws Exception
	 */
	private IJMSTransactionHolder jmsChappyLogin() throws Exception {
		ChappyJMSLogin login = new ChappyJMSLogin("system", "system", true);
		login.createConnectionToServer("localhost", serverPort);
		login.send();
		while(login.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
		
		assertEquals("request did not completed", IJMSStatus.OK, login.getStatus());			
		assertNull("there should be no exeception", login.getTransactionException());
		assertFalse("should be no exception", login.hasException());
		assertEquals("reply message is wrong", CHAPPY_RECEIVED_OK, login.getTransactionErrorMessage());
		
		if (!login.hasException()) {
			 IJMSTransactionHolder transaction =  login.createTransactionHolder();
			 assertEquals("user should be system", "system", transaction.getCookie().getUserName());
			 return transaction;
		}
		return null;
	}

	/**
	 * test wrapper for chappy logout
	 * @param transaction holder from login
	 * @throws Exception
	 */
	private void jmsChappyLogout(IJMSTransactionHolder transaction) throws Exception {
		ChappyJMSLogout logout = new ChappyJMSLogout(transaction);
		logout.send();
		while(logout.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);

		assertEquals("request did not completed", IJMSStatus.OK, logout.getStatus());			
		assertNull("there should be no exeception", logout.getTransactionException());
		assertFalse("should be no exception", logout.hasException());
		assertEquals("reply message is wrong", CHAPPY_RECEIVED_OK, logout.getTransactionErrorMessage());
	}
	
	/**
	 * login and add custom transformers and list and validate them.
	 * @return chappy transaction holder
	 */
	private IJMSTransactionHolder chappyJMSAddCustomTransformers(final List<String> addTransformers,
			final IJMSTransactionHolder transaction) {
		
		// add transformers in transaction
		for (String transf : addTransformers) {
			ChappyJMSAddTransformer addTransformer = new ChappyJMSAddTransformer(transf, transaction);
			try {
				addTransformer.setTransformer(transf, RestCallsUtils.CUSTOM_TRANSFORMERS_DUMMY);
				addTransformer.send();
				while(addTransformer.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
				assertEquals("add transformer " + transf + " exception", addTransformer.getStatus(),
						IJMSStatus.OK);
			} catch (IOException | JMSException | InterruptedException e) {
				e.printStackTrace();
				fail("exception occured at add transformer" + e.getLocalizedMessage());
			}
		}
		return transaction;
	}
	
	/**
	 * chappy test:
	 * 	- login in chappy using jms
	 * 	- logout from chappy using jms
	 */
	@Test
	public void jmsChappyLoginLogout() {
		try {
			IJMSTransactionHolder transaction = jmsChappyLogin();
			jmsChappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void jmsChappyAddTransformer() {
		try {
			IJMSTransactionHolder transaction = jmsChappyLogin();
			List<String> addTransformers = new ArrayList<>();
			addTransformers.add("PreProcessingStep");
			chappyJMSAddCustomTransformers(addTransformers, transaction);
			jmsChappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
}
