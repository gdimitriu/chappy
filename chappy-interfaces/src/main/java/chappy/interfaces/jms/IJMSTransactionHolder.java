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
package chappy.interfaces.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import chappy.interfaces.transactions.IClientTransaction;

/**
 * This is the interface for the JMS transaction Holder for the clients.
 * @author Gabriel Dimitriu
 *
 */
public interface IJMSTransactionHolder extends IClientTransaction {

	/**
	 * @return connection for transaction.
	 */
	public Connection getCurrentConnection() throws JMSException ;
	
	/**
	 * @return current session of the client transaction.
	 */
	public Session getCurrentSession() throws JMSException ;
	
	/**
	 * @return current message consumer for the transaction
	 */
	public MessageConsumer getCurrentMessageConsumer() throws JMSException ;
			
	/**
	 * @return current message producer for transaction.
	 */
	public MessageProducer getCurrentMessageProducer() throws JMSException ;
	
	
	/**
	 * @return current replyTo destination.
	 */
	public Destination getCurrentReplyToDestination() throws JMSException ;
	
	/**
	 * close all connections.
	 * This should be called only once.
	 * @return message confirmation.
	 */
	public String closeAll();
	
	/**
	 * @param serverName
	 * @param port
	 * @throws Exception 
	 */
	public void createConnectionToServer(final String serverName, final int port) throws Exception;
	
	/**
	 * check is the connection is closed.
	 * @return true if the connection is closed.
	 */
	public boolean isClosed();

	/**
	 * start a JMS transaction.
	 * @throws JMSException
	 */
	public default void startTransaction() throws JMSException {
		//nothing for default
	}
	
	/**
	 * create the consumer with filter for the correlation message.
	 * @param correlationId
	 * @throws JMSException
	 */
	public default void createMessageConsumerFilter(final String correlationId) throws JMSException {
		//nothing for default.
	}
}
