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
		return null;
	}
	
	/**
	 * remove a transaction.
	 * @param cookie
	 */
	public void removeTransaction(final CookieTransaction cookie) {
		
	}
}
