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

import java.util.HashMap;
import java.util.Map;

import chappy.interfaces.cookies.CookieTransaction;
import chappy.interfaces.cookies.CookieTransactionsToken;
import chappy.transaction.base.Transaction;

/**
 * providers for transactions.
 * @author Gabriel Dimitriu
 *
 */
public class TransactionProviders {

	/** singleton providers */
	static private TransactionProviders singleton = new TransactionProviders();
	
	/** map of transactions */
	private Map<String, Transaction> mapOfTransactionData = null;
	
	/**
	 * private because is singleton.
	 */
	private TransactionProviders() {
		mapOfTransactionData = new HashMap<String, Transaction>();
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
	public Transaction getTransaction(final CookieTransaction cookie) {
		if (mapOfTransactionData.containsKey(cookie.generateStorageId())) {
			return mapOfTransactionData.get(cookie.generateStorageId());
		}
		return null;
	}

	/**
	 * remove transaction from storage.
	 * @param cookie of the transaction
	 */
	public void removeTransaction(final CookieTransactionsToken cookie) {
		if (mapOfTransactionData.containsKey(cookie.generateStorageId())) {
			mapOfTransactionData.remove(cookie.generateStorageId());
		}
	}
	
	public void putTransaction(final CookieTransaction cookie, final Transaction transaction) {
		mapOfTransactionData.put(cookie.generateStorageId(), transaction);
	}
}
