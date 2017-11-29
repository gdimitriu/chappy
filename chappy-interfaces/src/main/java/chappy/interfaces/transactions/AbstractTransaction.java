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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chappy.interfaces.flows.IFlowRunner;

/**
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractTransaction implements ITransaction {
	
	/** list of transformers used by this transaction */
	private List<String> listOfTansformers = null;
	
	/** true if it has to be persist */
	private boolean persistence = false;
	
	/** transactionID */
	private String transactionId;
	
	/** cookie transactionID */
	private String cookieTransactionId;
	
	/** cache of flow runners */
	private Map<String, IFlowRunner> flowRunners = new HashMap<>();	
	
	/**
	 * 
	 */
	public AbstractTransaction() {
		listOfTansformers = new ArrayList<String>();
	}
	
	/**
	 * constructor used to reload data.
	 * @param id of transaction
	 * @param persistence true if persist
	 * @param transformers list of transformers
	 */
	public AbstractTransaction(final String id, final boolean persistence, final List<String> transformers, final String cookieTransactionId) {
		this.persistence = persistence;
		this.transactionId = id;
		this.cookieTransactionId = cookieTransactionId;
		this.listOfTansformers = new ArrayList<String>();
		if (transformers != null && !transformers.isEmpty()) {
			this.listOfTansformers.addAll(transformers);
		}
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
	 * @see chappy.interfaces.transactions.ITransaction#addTransformer(java.lang.String, java.lang.String, byte[])
	 */
	@Override
	public void addTransformer(String userName, String fullName, byte[] originalByteCode)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		this.listOfTansformers.add(fullName);
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

	/**
	 * @return the cookieTransactionId
	 */
	@Override
	public String getCookieTransactionId() {
		return cookieTransactionId;
	}

	/**
	 * @param cookieTransactionId the cookieTransactionId to set
	 */
	public void setCookieTransactionId(String cookieTransactionID) {
		this.cookieTransactionId = cookieTransactionID;
	}
	
	@Override
	public void putFlowRunner(final String nameOfFlow, final IFlowRunner flowRunner) {
		flowRunners.put(nameOfFlow, flowRunner);		
	}
	
	@Override
	public IFlowRunner getFlowRunner(final String nameOfFlow) {
		if (flowRunners.containsKey(nameOfFlow)) {
			return flowRunners.get(nameOfFlow);
		}
		return null;
	}
	
	@Override
	public void removeFlowRunner(final String nameOfFlow) {
		if (flowRunners.containsKey(nameOfFlow)) {
			flowRunners.remove(nameOfFlow);
		}
	}
	
	@Override
	public void setFlowRunners(final Map<String, IFlowRunner> runners) {
		flowRunners = runners;
	}
	
	@Override
	public Map<String, IFlowRunner> getFlowRunners() {
		return flowRunners;
	}
}
