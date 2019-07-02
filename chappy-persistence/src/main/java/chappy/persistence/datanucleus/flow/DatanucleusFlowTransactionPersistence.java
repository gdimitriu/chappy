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
package chappy.persistence.datanucleus.flow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import chappy.interfaces.flows.IFlowRunner;
import chappy.interfaces.markers.ISystemFlowPersistence;
import chappy.interfaces.transactions.ITransaction;
import chappy.persistence.datanucleus.DatanucleusTransaction;

/**
 * @author Gabriel Dimitriu
 *
 */
@PersistenceCapable(detachable = "true")
public class DatanucleusFlowTransactionPersistence implements ISystemFlowPersistence{

	@Persistent(defaultFetchGroup = "true", valueStrategy = IdGeneratorStrategy.UUIDHEX, primaryKey = "true")
	private String transactionId;
	
	@Persistent(defaultFetchGroup = "true")
	@Order
	@Join(column = "DatanucleusFlowTransactionPersistence_listOfTransformers")
	private List<String> listOftransformers = new ArrayList<>();
	
	@Persistent(defaultFetchGroup = "true")
	private String storageId;
	
	@Persistent(defaultFetchGroup = "true")
	private String cookieTransactionId;
	
	@Persistent(defaultFetchGroup = "true")
	@Order
	@Join(column = "DatanucleusFlowTransactionPersistence_flowRunnersNames")
	private List<String> flowRunnersNames = new ArrayList<>();
	
	@Persistent(defaultFetchGroup = "true", serializedElement = "true")
	@Order
	@Join(column = "DatanucleusFlowTransactionPersistence_flowRunnersInstances")
	@Extension(key = "mapping-strategy", value = "per-implementation", vendorName = "datanucleus")
	private List<IFlowRunner> flowRunnersInstances = new ArrayList<>();
	
	/** true if it has to be persist */
	@Persistent(defaultFetchGroup = "true")
	private boolean persistence = false;
	/**
	 * 
	 */
	public DatanucleusFlowTransactionPersistence() {
		// TODO Auto-generated constructor stub
	}
	
	public DatanucleusFlowTransactionPersistence(final String id, final boolean persistence, final List<String> transformers, final String cookieTransactionId) {
		this.persistence = persistence;
		this.transactionId = id;
		this.cookieTransactionId = cookieTransactionId;
		this.listOftransformers = new ArrayList<String>();
		if (transformers != null && !transformers.isEmpty()) {
			this.listOftransformers.addAll(transformers);
		}
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
	
	/**
	 * @return the transactionId
	 */
	@Override
	public String getTransactionId() {
		return transactionId;
	}
	
	/**
	 * @param transactionId the transactionId to set
	 */
	@Override
	public void setTransactionId(final String transactionId) {
		this.transactionId = transactionId;
	}
	
	/**
	 * 
	 * @param transactionId the cookie transaction id (JMS or other transaction id) to be set
	 */
	@Override
	public void setCookieTransactionId(final String transactionId) {
		this.cookieTransactionId = transactionId;
	}
	
	/**
	 * @return the cookie transaction id (JMS or other transaction id)
	 */
	@Override
	public String getCookieTransactionId() {
		return this.cookieTransactionId;
	}
	
	/**
	 * @return the listOftransformers
	 */
	@Override
	public List<String> getListOftransformers() {
		return listOftransformers;
	}
	
	/**
	 * @param listOftransformers the listOftransformers to set
	 */
	@Override
	public void setListOftransformers(final List<String> listOftransformers) {
		this.listOftransformers.clear();
		this.listOftransformers.addAll(listOftransformers);
	}
	
	/**
	 * @return the storageId
	 */
	@Override
	public String getStorageId() {
		return storageId;
	}
	
	/**
	 * @param storageId the storageId to set
	 */
	@Override
	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.markers.ISystemFlowPersistence#createRealElement()
	 */
	@Override
	public ITransaction createRealElement(final PersistenceManager pm) {
		
		DatanucleusTransaction trans = new DatanucleusTransaction(transactionId, true, listOftransformers, cookieTransactionId);
		trans.setPersistedTransaction(this);
		trans.setPersistenceFlowManager(pm);
		trans.setFlowRunners(flowRunnersNames, flowRunnersInstances);
		return trans;
	}

	@Override
	public void setFlowRunners(final List<String> names, final List<IFlowRunner> instances) {
		flowRunnersNames.clear();
		flowRunnersNames.addAll(names);
		flowRunnersInstances.clear();
		flowRunnersInstances.addAll(instances);
	}

	@Override
	public List<String> getFlowRunnersNames() {
		return flowRunnersNames;
	}

	@Override
	public List<IFlowRunner> getFlowRunnersInstances() {
		return flowRunnersInstances;
	}
	
	@Override
	public void putFlowRunner(final String nameOfFlow, final IFlowRunner flowRunner) {
		int index = flowRunnersNames.indexOf(nameOfFlow);
		if (index > -1) {
			flowRunnersInstances.remove(index);
			flowRunnersNames.remove(index);
		}
		flowRunnersNames.add(nameOfFlow);
		flowRunnersInstances.add(flowRunner);
	}
	
	@Override
	public IFlowRunner getFlowRunner(final String nameOfFlow) {
		int index = flowRunnersNames.indexOf(nameOfFlow);
		if (index > -1) {
			return flowRunnersInstances.get(index);
		}
		return null;
	}
	
	@Override
	public void removeFlowRunner(final String nameOfFlow) {
		int index = flowRunnersNames.indexOf(nameOfFlow);
		if (index > -1) {
			flowRunnersInstances.remove(index);
			flowRunnersNames.remove(index);
		}
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#addTransformer(java.lang.String, java.lang.String, byte[])
	 */
	@Override
	public void addTransformer(final String userName, final String fullName, final byte[] originalByteCode)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		this.listOftransformers.add(fullName);
	}
}
