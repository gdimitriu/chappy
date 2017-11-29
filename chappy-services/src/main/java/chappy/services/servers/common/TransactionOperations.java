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
package chappy.services.servers.common;

import java.io.InputStream;
import java.util.List;

import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.exception.ForbiddenException;
import chappy.interfaces.flows.IFlowRunner;
import chappy.interfaces.flows.MultiDataQueryHolder;
import chappy.interfaces.services.IChappyServiceNamesConstants;
import chappy.interfaces.transactions.ITransaction;
import chappy.persistence.providers.CustomTransformerStorageProvider;
import chappy.policy.provider.SystemPolicyProvider;
import chappy.providers.flow.runners.TransformersFlowRunnerProvider;
import chappy.providers.transaction.TransactionProviders;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * This provide utility methods for transaction operations.
 * @author Gabriel Dimitriu
 *
 */
public class TransactionOperations {

	/**
	 * utility for login operation.
	 * @param requester the class that request this.
	 * @param userName the user name
	 * @param password the password for the user
	 * @param persistence if the persistence is allowed
	 * @param transactionId the id of the transaction
	 * @return cookie.
	 * @throws ForbiddenException 
	 */
	public static IChappyCookie login(final Class<?> requester, final String userName, final String password, final boolean persistence, final String transactionId) throws ForbiddenException {
		
		if (!SystemPolicyProvider.getInstance().getAuthenticationHandler().isAuthenticate(userName, password)) {
			return null;
		}
		IChappyCookie response = null;
		
		boolean allowedPersistence = SystemPolicyProvider.getInstance().getAuthenticationHandler().isAllowedPersistence(userName);
		if (persistence != allowedPersistence) {
			if (persistence) {
				return null;
			}
		}
		
		response = TransactionProviders.getInstance().startTransaction(requester, userName, password, persistence, transactionId);
		return response;
	}
	
	/**
	 * Utility for logout request.
	 * @param received cookie
	 */
	public static ITransaction logout(final IChappyCookie received) {
		ITransaction transaction = TransactionProviders.getInstance().getTransaction(received);
		if (transaction == null) {
			return null;
		}
    	List<String> listOfTransformers = transaction.getListOfCustomTansformers();
    	CustomTransformerStorageProvider.getInstance().removeTransformers(received.getUserName(), listOfTransformers);
    	
    	TransactionProviders.getInstance().removeTransaction(received);
    	transaction.commit();
		return transaction;
	}
	
	/**
	 * Add Static flow Runner to the cache.
	 * @param received cookie
	 * @param configurationStream
	 * @param queryParams
	 * @param multiData
	 * @throws Exception
	 */
	public static void addStaticFlowRunner(final IChappyCookie received, final InputStream configurationStream,
			final String flowName, final MultiDataQueryHolder multiData) throws Exception {
		
		IFlowRunner runner = TransformersFlowRunnerProvider.getInstance()
				.createFlowRunner(IChappyServiceNamesConstants.STATIC_FLOW, configurationStream, multiData);

		runner.createSteps(received);

		ITransaction transaction = TransactionProviders.getInstance().getTransaction(received);
		transaction.putFlowRunner(flowName, runner);
	}
	
	/**
	 * run a static flow runner from the cache.
	 * @param received
	 * @param holder
	 * @param multiData
	 * @param flowName
	 * @throws Exception
	 */
	public static void runStaticFlowRunnerByName(final IChappyCookie received, final List<StreamHolder> holders, final MultiDataQueryHolder multiData,
			String flowName) throws Exception {
		
		ITransaction transaction = TransactionProviders.getInstance().getTransaction(received);
		transaction.getFlowRunner(flowName).executeSteps(holders, multiData);
	}
	
	/**
	 * run a static flow only one time.
	 * @param received
	 * @param configurationStream
	 * @param holders
	 * @param multiData
	 * @throws Exception
	 */
	public static void runStaticFlow(final IChappyCookie received, final InputStream configurationStream, final List<StreamHolder> holders,
			final MultiDataQueryHolder multiData) throws Exception {
		IFlowRunner runner = TransformersFlowRunnerProvider.getInstance()
				.createFlowRunner(IChappyServiceNamesConstants.STATIC_FLOW, configurationStream, multiData);
		runner.createSteps(received);
		runner.executeSteps(holders);
	}
	
}
