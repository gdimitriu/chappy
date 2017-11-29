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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
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
	private List<String> listOftransformers = new ArrayList<String>();
	
	@Persistent(defaultFetchGroup = "true")
	private String storageId;
	
	@Persistent(defaultFetchGroup = "true")
	private String cookieTransactionId;
	
	@Persistent(defaultFetchGroup = "true")
	@Join(column = "DatanucleusFlowTransactionPersistence_runners")
	private Map<String, IFlowRunner> flowRunners = new HashMap<>();
	
	/**
	 * 
	 */
	public DatanucleusFlowTransactionPersistence() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the transactionId
	 */
	@Override
	public String getTransactionId() {
//		if (cookieTransactionId != null) {
//			return cookieTransactionId;
//		}
		return transactionId;
	}
	
	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(final String transactionId) {
		this.transactionId = transactionId;
	}
	
	/**
	 * 
	 * @param transactionId the cookie transaction id (JMS or other transaction id) to be set
	 */
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
	public List<String> getListOftransformers() {
		return listOftransformers;
	}
	
	/**
	 * @param listOftransformers the listOftransformers to set
	 */
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
		trans.setFlowRunners(flowRunners);
		return trans;
	}
	
	public Map<String, IFlowRunner> getFlowRunners() {
		return this.flowRunners;
	}
	
	public void setFlowRunners(final Map<String, IFlowRunner> runners) {
		this.flowRunners.clear();
		this.flowRunners.putAll(runners);
	}
}
