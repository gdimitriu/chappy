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
package chappy.persistence.providers;

import java.util.HashMap;
import java.util.Map;

import chappy.interfaces.cookies.CookieTransaction;
import chappy.interfaces.persistence.IPersistence;
import chappy.interfaces.transactions.ITransaction;

/**
 * @author Gabriel Dimitriu
 *
 */
public class TransactionStorageProvider {

	private IPersistence systemFlowPersistence = null;
	
	/** map of transactions persisted data */
	private Map<String, ITransaction> mapOfTransactionPersistedData = null;
	
	/** map of transactions */
	private Map<String, ITransaction> mapOfTransactionTransientData = null;
	/**
	 * 
	 */
	public TransactionStorageProvider() {
		try {
			systemFlowPersistence = PersistenceProvider.getInstance().getSystemFlowPersistence();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		mapOfTransactionPersistedData = new HashMap<String, ITransaction>();
		mapOfTransactionTransientData = new HashMap<String, ITransaction>();
	}
	
	
	/**
	 * load the persisted transactions.
	 */
	public void loadPersisted() {
		
	}

	/**
	 * get the transaction from persistence.
	 * @param cookie
	 * @return transaction.
	 */
	public ITransaction getTransaction(final CookieTransaction cookie) {
		if (mapOfTransactionTransientData.containsKey(cookie.generateStorageId())) {
			return mapOfTransactionTransientData.get(cookie.generateStorageId());
		} else if (mapOfTransactionPersistedData.containsKey(cookie.generateStorageId())) {
			return mapOfTransactionPersistedData.get(cookie.generateStorageId());
		}
		return null;
	}
	
	/**
	 * remove a transaction.
	 * @param cookie
	 */
	public void removeTransaction(final CookieTransaction cookie) {
		if (mapOfTransactionTransientData.containsKey(cookie.generateStorageId())) {
			mapOfTransactionTransientData.remove(cookie.generateStorageId());
		} else if (mapOfTransactionPersistedData.containsKey(cookie.generateStorageId())) {
			mapOfTransactionPersistedData.remove(cookie.generateStorageId());
		}
	}
	
	/**
	 * put the transaction into the persistence and cache.
	 * @param cookie
	 * @param transaction
	 */
	public void putTransaction(final CookieTransaction cookie, final ITransaction transaction) {
		if (transaction.isPersistence()) {
			mapOfTransactionPersistedData.put(cookie.generateStorageId(), transaction);
		} else {
			mapOfTransactionTransientData.put(cookie.generateStorageId(), transaction);
		}
	}
}
