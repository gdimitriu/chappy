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
package chappy.interfaces.services;

import java.util.List;

import chappy.interfaces.jms.resources.IJMSRuntimeResource;

/**
 * @author Gabriel Dimitriu
 *
 */
public interface IServiceJMS extends IServiceServer{
	/**
	 * @return the queueName
	 */
	public String[] getQueueNames();

	/**
	 * @param queueName the queueName to set
	 */
	public void addQueueName(final String queueName);
	
	
	/**
	 * set the list of queues
	 * @param queues list of queues
	 */
	public void setQueuesNames(final List<String> queues);
	
	/**
	 * @return the default connectionFactoryName
	 */
	public String getDefaultConnectionFactoryName();


	/**
	 * @return the journalDirectory
	 */
	public String getJournalDirectory();
	
	/**
	 * @return the binding directory
	 */
	public String getBingindDirectory();

	/**
	 * @return the large message directory
	 */
	public String getLargeMessageDirectory();

	/**
	 * @return the queueDurable
	 */
	public boolean isQueueDurable();
	
	
	/**
	 * @return the serverSecurityEnabled
	 */
	public boolean isServerSecurityEnabled();
		
	/**
	 * @return the serverPersistenceEnabled
	 */
	public boolean isServerPersistenceEnabled();
	
	/**
	 * @param consumer that will consume the resource defined by it's queue.
	 * @return registered key
	 */
	public String registerResourceConsumer(final IJMSRuntimeResource consumer);
	
	
	/**
	 * get the resource consumer that was registered
	 * @param registered key
	 * @return resource consumer or null;
	 */
	public IJMSRuntimeResource getRegisterResourceConsumer(final String registeredkey);
	
	/**
	 * @param queueName for unregister consumer.
	 */
	public void unregisterResourceConsumer(final String registeredkey);
}
