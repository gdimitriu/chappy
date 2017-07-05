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
package chappy.transaction.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import chappy.interfaces.persistence.IPersistence;
import chappy.interfaces.transactions.ITransaction;
import chappy.providers.transformers.custom.CustomTransformerStorageProvider;

/**
 * @author Gabriel Dimitriu
 *
 */
public class Transaction implements ITransaction {
	
	/** list of transformers used by this transaction */
	private List<String> listOfTansformers = null;
	
	/** true if it has to be persist */
	private boolean persistence = false;
	
	/** transactionID */
	private String transactionId;
	
	/** persistence implementation */
	private IPersistence persistenceImpl = null;

	/**
	 * 
	 */
	public Transaction() {
		listOfTansformers = new ArrayList<String>();
	}

	/* (non-Javadoc)
	 * @see chappy.transaction.base.ITransaction#getListOfCustomTansformers()
	 */
	@Override
	public List<String> getListOfCustomTansformers() {
		return listOfTansformers;
	}

	/* (non-Javadoc)
	 * @see chappy.transaction.base.ITransaction#setListOfTansformers(java.util.List)
	 */
	@Override
	public void setListOfTansformers(final List<String> listOfTansformers) {
		this.listOfTansformers = listOfTansformers;
	}

	/* (non-Javadoc)
	 * @see chappy.transaction.base.ITransaction#addTransformer(java.lang.String, java.lang.String, byte[])
	 */
	@Override
	public void addTransformer(final String userName, final String fullName, final byte[] originalByteCode) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		this.listOfTansformers.add(fullName);
		CustomTransformerStorageProvider.getInstance().pushNewUserTransformer(userName, fullName, originalByteCode, persistence);
	}

	/* (non-Javadoc)
	 * @see chappy.transaction.base.ITransaction#isPersistence()
	 */
	@Override
	public boolean isPersistence() {
		return persistence;
	}

	/* (non-Javadoc)
	 * @see chappy.transaction.base.ITransaction#setPersistence(boolean)
	 */
	@Override
	public void setPersistence(final boolean persistence) {
		this.persistence = persistence;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#setTransactionId(java.lang.String)
	 */
	@Override
	public void setTransactionId(final String id) {
		this.transactionId = id;
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#getTransactionId()
	 */
	@Override
	public String getTransactionId() {
		return this.transactionId;
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
}
