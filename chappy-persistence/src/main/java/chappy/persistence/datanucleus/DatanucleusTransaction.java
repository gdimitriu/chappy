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
package chappy.persistence.datanucleus;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import chappy.interfaces.transactions.AbstractTransaction;

/**
 * @author Gabriel Dimitriu
 *
 */
public class DatanucleusTransaction extends AbstractTransaction {

	/** persistence manager who own this log Transaction */
	private PersistenceManager persistenceLogManager = null;
	
	/** persistence log Transaction */
	private Transaction logTransaction = null;
	
	/**
	 * 
	 */
	public DatanucleusTransaction() {
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see chappy.transaction.base.ITransaction#addTransformer(java.lang.String, java.lang.String, byte[])
	 */
	@Override
	public void addTransformer(final String userName, final String fullName, final byte[] originalByteCode) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		super.addTransformer(userName, fullName, originalByteCode);
	}

	@Override
	public void start() {		
		if (getSystemLogPersistence().getFactory() == null) {
			return ;
		}
		persistenceLogManager  = ((PersistenceManagerFactory) getSystemLogPersistence().getFactory()).getPersistenceManager();
		logTransaction = persistenceLogManager.currentTransaction();
		logTransaction.begin();
	}

	@Override
	public void commit() {
		if (logTransaction != null) {
			 logTransaction.commit();
		 }
		persistenceLogManager.close();
	}

	@Override
	public void rollback() {
		if (logTransaction != null) {
			logTransaction.rollback();
		}
		persistenceLogManager.close();
	}

	@Override
	public void makePersistent(final Object obj) {
		if (logTransaction != null && persistenceLogManager != null) {
			 persistenceLogManager.makePersistent(obj);
		 }
	}
}
