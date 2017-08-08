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
package chappy.persistence.transaction;

import java.io.IOException;
import java.util.List;

import chappy.interfaces.persistence.IPersistence;
import chappy.interfaces.transactions.AbstractTransaction;
import chappy.persistence.providers.CustomTransformerStorageProvider;
import chappy.persistence.providers.PersistenceProvider;

/**
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractPersistenceTransaction extends AbstractTransaction {

	
	/** persistence implementation */
	private IPersistence persistenceImpl = null;
	
	/** system log persistence implementation */
	private IPersistence systemLogPersistenceImpl = null;
	
	/** system flow persistence implementation */
	private IPersistence systemFlowPersistenceImpl = null;
	
	/** system upgrade persistence implementation */
	private IPersistence systemUpgradePersistenceImpl = null;
	
	/**
	 * 
	 */
	public AbstractPersistenceTransaction() {
		
	}
	
	/**
	 * constructor used for loading.
	 * @param id
	 * @param persistence
	 * @param transformers
	 */
	public AbstractPersistenceTransaction(final String id, final boolean persistence, final List<String> transformers) {
		super(id,persistence, transformers);
		try {
			systemUpgradePersistenceImpl = PersistenceProvider.getInstance().getSystemUpgradePersistence();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			systemFlowPersistenceImpl = PersistenceProvider.getInstance().getSystemFlowPersistence();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			systemLogPersistenceImpl = PersistenceProvider.getInstance().getSystemPersistence();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#setPersistenceImpl(chappy.interfaces.persistence.IPersistence)
	 */
	@Override
	public void setPersistenceImpl(final IPersistence persistence) {
		this.persistenceImpl = persistence;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#getPersistenceImpl()
	 */
	@Override
	public IPersistence getPersistenceImpl() {
		return this.persistenceImpl;
	}
	
	/* (non-Javadoc)
	 * @see chappy.transaction.base.ITransaction#addTransformer(java.lang.String, java.lang.String, byte[])
	 */
	@Override
	public void addTransformer(final String userName, final String fullName, final byte[] originalByteCode) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		super.addTransformer(userName, fullName, originalByteCode);
		CustomTransformerStorageProvider.getInstance().pushNewUserTransformer(userName, fullName, originalByteCode, this);
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#setSystemLogPeristence(chappy.interfaces.persistence.IPersistence)
	 */
	@Override
	public void setSystemLogPersistence(IPersistence persistenceImpl) {
		this.systemLogPersistenceImpl = persistenceImpl;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#getSystemLogPersistence()
	 */
	@Override
	public IPersistence getSystemLogPersistence() {
		return this.systemLogPersistenceImpl;
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#setSystemFlowPeristence(chappy.interfaces.persistence.IPersistence)
	 */
	@Override
	public void setSystemFlowPersistence(IPersistence persistenceImpl) {
		this.systemFlowPersistenceImpl = persistenceImpl;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#getSystemFlowPersistence()
	 */
	@Override
	public IPersistence getSystemFlowPersistence() {
		return this.systemFlowPersistenceImpl;
	}
	
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#setSystemUpgradePeristence(chappy.interfaces.persistence.IPersistence)
	 */
	@Override
	public void setSystemUpgradePersistence(IPersistence persistenceImpl) {
		this.systemUpgradePersistenceImpl = persistenceImpl;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#getSystemLogPersistence()
	 */
	@Override
	public IPersistence getSystemUpgradePersistence() {
		return this.systemUpgradePersistenceImpl;
	}
}
