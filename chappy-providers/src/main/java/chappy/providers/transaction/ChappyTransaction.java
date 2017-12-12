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

import java.io.IOException;

import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.persistence.ICustomStepPersistence;
import chappy.interfaces.persistence.IPersistence;
import chappy.interfaces.transactions.AbstractTransaction;
import chappy.persistence.providers.CustomTransformerStorageProvider;

/**
 * @author Gabriel Dimitriu
 *
 */
public class ChappyTransaction extends AbstractTransaction {

	/**
	 * 
	 */
	public ChappyTransaction() {
		
	}

	

	/* (non-Javadoc)
	 * @see chappy.transaction.base.ITransaction#addTransformer(java.lang.String, java.lang.String, byte[])
	 */
	@Override
	public void addTransformer(final String userName, final String fullName, final byte[] originalByteCode) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		super.addTransformer(userName, fullName, originalByteCode);
		CustomTransformerStorageProvider.getInstance().pushNewUserTransformer(userName, fullName, originalByteCode, null);
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#start()
	 */
	@Override
	public void start() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#commit()
	 */
	@Override
	public void commit() {
		// TODO Auto-generated method stub		
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#rollback()
	 */
	@Override
	public void rollback() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#makePersistent(java.lang.Object)
	 */
	@Override
	public void makePersistent(final Object obj) {
		// TODO Auto-generated method stub		
	}



	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#setPersistenceImpl(chappy.interfaces.persistence.IPersistence)
	 */
	@Override
	public void setPersistenceImpl(IPersistence persistenceImpl) {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#getPersistenceImpl()
	 */
	@Override
	public IPersistence getPersistenceImpl() {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#setSystemLogPersistence(chappy.interfaces.persistence.IPersistence)
	 */
	@Override
	public void setSystemLogPersistence(IPersistence persistenceImpl) {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#getSystemLogPersistence()
	 */
	@Override
	public IPersistence getSystemLogPersistence() {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#setSystemFlowPersistence(chappy.interfaces.persistence.IPersistence)
	 */
	@Override
	public void setSystemFlowPersistence(IPersistence flowPersistence) {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#getSystemFlowPersistence()
	 */
	@Override
	public IPersistence getSystemFlowPersistence() {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#setSystemUpgradePersistence(chappy.interfaces.persistence.IPersistence)
	 */
	@Override
	public void setSystemUpgradePersistence(IPersistence upgradePersistence) {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#getSystemUpgradePersistence()
	 */
	@Override
	public IPersistence getSystemUpgradePersistence() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public ICustomStepPersistence persistTransformer(String generateStorageName, byte[] remappedBytecode) {
		// TODO Auto-generated method stub
		return null;
	}
	

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#generateTransactionId(chappy.interfaces.cookies.CookieTransaction)
	 */
	@Override
	public void generateTransactionId(final IChappyCookie cookie) {
		if(cookie.getTransactionId() == null  || cookie.getTransactionId().isEmpty()) {
			setTransactionId(cookie.getUserName());
			cookie.setTransactionId(getTransactionId());			
			return;
		}
		setTransactionId(cookie.getTransactionId());
	}



	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#persist()
	 */
	@Override
	public void persist(final IChappyCookie cookie) {
		// ChappyTransaction is not persistence capable.
	}
}
