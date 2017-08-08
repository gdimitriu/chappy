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
package chappy.providers.transaction;

import chappy.interfaces.cookies.CookieTransaction;
import chappy.interfaces.cookies.CookieTransactionsToken;
import chappy.interfaces.exception.ForbiddenException;
import chappy.interfaces.persistence.IPersistence;
import chappy.interfaces.transactions.ITransaction;
import chappy.persistence.providers.PersistenceProvider;
import chappy.persistence.providers.TransactionStorageProvider;

/**
 * providers for transactions.
 * @author Gabriel Dimitriu
 *
 */
public class TransactionProviders {

	/** singleton providers */
	static private TransactionProviders singleton = new TransactionProviders();
	
	private TransactionStorageProvider storageProvider = null;
	
	/**
	 * private because is singleton.
	 */
	private TransactionProviders() {
		storageProvider = new TransactionStorageProvider();
		storageProvider.loadPersisted();
	}
	
	/**
	 *  load the persistenced transactions.
	 * @return void
	 */
	public void loadPersisted() {
		storageProvider.loadPersisted();
	}
	
	/**
	 * get the instance of the singleton.
	 * @return singleton of the transactions.
	 */
	public static TransactionProviders getInstance() {
		return singleton;
	}

	/**
	 * get the transaction.
	 * @param cookie of the transaction
	 * @return base transaction.
	 */
	public ITransaction getTransaction(final CookieTransaction cookie) {
		return storageProvider.getTransaction(cookie);
	}

	/**
	 * remove transaction from storage.
	 * @param cookie of the transaction
	 */
	public void removeTransaction(final CookieTransactionsToken cookie) {
		storageProvider.removeTransaction(cookie);
	}
	

	/**
	 * start a new transaction.
	 * @param cookie that is identified
	 * @param persistence true if it has persistence.
	 * @return the transaction.
	 * @throws ForbiddenException in case of transaction problem for persistence
	 */
	public ITransaction startTransaction(final CookieTransaction cookie, final boolean persistence) throws ForbiddenException {
		ITransaction transaction;
		if (!persistence) {
			transaction = new ChappyTransaction();
		} else {
			try {
				transaction = PersistenceProvider.getInstance().getSystemPersistence().createTransaction();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throw new ForbiddenException("persistence not allowed : " + e.getLocalizedMessage());
			}
			try {
				IPersistence systemLogPersistence = PersistenceProvider.getInstance().getSystemPersistence();
				transaction.setSystemLogPersistence(systemLogPersistence);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throw new ForbiddenException("persistence for logs not allowed : " + e.getLocalizedMessage());
			}
			try {
				IPersistence systemUpgradePersistence = PersistenceProvider.getInstance().getSystemUpgradePersistence();
				transaction.setSystemUpgradePersistence(systemUpgradePersistence);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throw new ForbiddenException("persistence for upgrade not allowed : " + e.getLocalizedMessage());
			}
			try {
				IPersistence systemFlowPersistence = PersistenceProvider.getInstance().getSystemFlowPersistence();
				transaction.setSystemFlowPersistence(systemFlowPersistence);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throw new ForbiddenException("persistence for upgrade not allowed : " + e.getLocalizedMessage());
			}
		}
		transaction.setPersistence(persistence);
		storageProvider.putTransaction(cookie, transaction);
		return transaction;
	}
	
}
