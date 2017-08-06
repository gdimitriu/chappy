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
import java.util.List;

import javax.jdo.annotations.Join;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import chappy.interfaces.markers.ISystemFlowPersistence;

/**
 * @author Gabriel Dimitriu
 *
 */
@PersistenceCapable(detachable = "true")
public class DatanucleusFlowTransactionPersistence implements ISystemFlowPersistence{

	@Persistent//(defaultFetchGroup = "true", customValueStrategy = "uuuid", primaryKey = "true")
	private String transactionId;
	
	@Persistent(defaultFetchGroup = "true")
	@Order
	@Join(column = "DatanucleusFlowTransactionPersistence_listOfTransformers")
	private List<String> listOftransformers = new ArrayList<String>();
	
	@Persistent(defaultFetchGroup = "true")
	private String storageId;
	
	/**
	 * 
	 */
	public DatanucleusFlowTransactionPersistence() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}
	
	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(final String transactionId) {
		this.transactionId = transactionId;
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
	public String getStorageId() {
		return storageId;
	}
	
	/**
	 * @param storageId the storageId to set
	 */
	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}
}
