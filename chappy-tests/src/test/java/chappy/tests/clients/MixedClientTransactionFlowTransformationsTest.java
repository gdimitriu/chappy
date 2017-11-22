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

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import chappy.interfaces.jms.IJMSTransactionHolder;

/**
 * Functional tests for Chappy clients with JMS protocol.
 * @author Gabriel Dimitriu
 *
 */
public class MixedClientTransactionFlowTransformationsTest {
	
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
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDown() throws Exception {
		server.stopAll();
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
	 * 	- logout from chappy using REST
	 */
	@Test
	public void mixedChappyLoginLogout() {
		try {
			IJMSTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSPort);
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	
	/**
	 * chappy test:
	 * - login in chappy using jms
	 * - add transformers and valide using REST
	 * - logout from chappy using REST
	 */
	@Test
	public void mixedChappyAddTransformer() {
		try {
			IJMSTransactionHolder transaction = JMSUtilsRequests.chappyLogin(serverJMSPort);
			List<String> addTransformers = new ArrayList<>();
			addTransformers.add("PreProcessingStep");
			JMSUtilsRequests.chppyAddCustomTransformersAndValidate(addTransformers, transaction);
			JMSUtilsRequests.chappyLogout(transaction);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
}
	
