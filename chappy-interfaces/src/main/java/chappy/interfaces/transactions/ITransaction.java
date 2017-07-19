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
package chappy.interfaces.transactions;

import java.io.IOException;
import java.util.List;

import chappy.interfaces.persistence.IPersistence;

/**
 * This is the transaction interface the base interface for transaction support.
 * @author Gabriel Dimitriu
 *
 */
public interface ITransaction {

	/**
	 * @return the listOfTansformers
	 */
	List<String> getListOfCustomTansformers();

	/**
	 * @param listOfTansformers the listOfTansformers to set
	 */
	void setListOfTansformers(final List<String> listOfTansformers);

	/**
	 * add a transformer.
	 * @param transformerName name of the transformer.
	 */
	void addTransformer(final String userName, final String fullName, final byte[] originalByteCode) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException;

	/**
	 * @return the persistence
	 */
	boolean isPersistence();

	/**
	 * @param persistance the persistence to set
	 */
	void setPersistence(final boolean persistence);
	
	/**
	 * set the transaction id.
	 * @param id of the transaction.
	 */
	void setTransactionId(final String id);
	
	/**
	 * get the transaction id.
	 * @return transaction id
	 */
	String getTransactionId();
	
	
	/**
	 * set the persistence implementation for this transaction.
	 * @param persistenceImpl
	 */
	void setPersistenceImpl(final IPersistence persistenceImpl);
	
	/**
	 * get the persistence implementation for this transaction.
	 * @return persistenceImpl
	 */
	IPersistence getPersistenceImpl();
	
	/**
	 * set the system log persistence
	 * @param persistenceImpl
	 */
	void setSystemLogPersistence(final IPersistence persistenceImpl);
	
	/**
	 * get the system log persistence.
	 * @return system persistence
	 */
	IPersistence getSystemLogPersistence();
	
	/**
	 * set the system flow persistence.
	 * @param flowPersistence
	 */
	void setSystemFlowPersistence(final IPersistence flowPersistence);
	
	/**
	 * get the system flow persistence.
	 * @return flow persistence
	 */
	IPersistence getSystemFlowPersistence();
	
	/**
	 * set the system upgrade persistence.
	 * @param upgradePersistence
	 */
	void setSystemUpgradePersistence(final IPersistence upgradePersistence);
	
	/**
	 * get the system upgrade persistence.
	 * @return flow persistence
	 */
	IPersistence getSystemUpgradePersistence();
	
	/**
	 * start a transaction.
	 */
	void start();
	
	/**
	 *  commit the transaction.
	 */
	void commit();
	
	/**
	 *  rollback the transaction.
	 */
	void rollback();
	
	/**
	 * persist the object.
	 * @param object to be persisted
	 */
	void makePersistent(final Object obj);

}