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
package chappy.clients.common.transaction;

import java.net.URI;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.jms.IJMSTransactionHolder;
import chappy.interfaces.rest.IRESTTransactionHolder;

/**
 * @author Gabriel Dimitriu
 *
 */
public class ChappyClientTransactionHolder implements IRESTTransactionHolder, IJMSTransactionHolder{
	
	/** holder for the jms transaction */
	private JMSTransactionHolder jmsTransaction = null;
	
	/** holder for the rest transaction */
	private RESTTransactionHolder restTransaction = null;

	/**
	 * 
	 */
	public ChappyClientTransactionHolder() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * factory to create REST Transaction.
	 * @param client 
	 * @param baseUri
	 * @param target
	 * @param cookie
	 * @return
	 */
	public static ChappyClientTransactionHolder createRESTTransactionHolder(final Client client, final URI baseUri, final WebTarget target, final IChappyCookie cookie) {
		RESTTransactionHolder rest = new RESTTransactionHolder(client, baseUri, target, cookie);
		ChappyClientTransactionHolder chappy = new ChappyClientTransactionHolder();
		chappy.setRestTransaction(rest);
		return chappy;
	}

	/**
	 * factory to create JMS Transaction.
	 * @param currentConnection
	 * @param currentSession
	 * @param currentMessageConsumer
	 * @param currentMessageProducer
	 * @param currentCookie
	 * @param replyTo
	 * @return chappy transaction.
	 */
	public static ChappyClientTransactionHolder createJMSTransactionHolder(final Connection currentConnection, final Session currentSession,
			final MessageConsumer currentMessageConsumer, final MessageProducer currentMessageProducer,
			final IChappyCookie currentCookie, final Destination replyTo) {
		JMSTransactionHolder jms = new JMSTransactionHolder(currentConnection, currentSession, currentMessageConsumer,
				currentMessageProducer, currentCookie, replyTo);
		ChappyClientTransactionHolder chappy = new ChappyClientTransactionHolder();
		chappy.setJmsTransaction(jms);
		return chappy;
	}
	
	/**
	 * factory to create JMS transaction
	 * @param currentSession
	 * @param currentConnection
	 * @param currentMessageConsumer
	 * @param currentMessageProducer
	 * @return chappy transaction.
	 */
	public static ChappyClientTransactionHolder createJMSTransactionHolder(final Session currentSession, final Connection currentConnection,
			final MessageConsumer currentMessageConsumer, final MessageProducer currentMessageProducer) {
		JMSTransactionHolder jms = new JMSTransactionHolder(currentSession, currentConnection, currentMessageConsumer,
				currentMessageProducer);
		ChappyClientTransactionHolder chappy = new ChappyClientTransactionHolder();
		chappy.setJmsTransaction(jms);
		return chappy;
	}
	/**
	 * @return the jmsTransaction
	 */
	public JMSTransactionHolder getJmsTransaction() {
		return jmsTransaction;
	}

	/**
	 * @param jmsTransaction the jmsTransaction to set
	 */
	public void setJmsTransaction(JMSTransactionHolder jmsTransaction) {
		this.jmsTransaction = jmsTransaction;
	}

	/**
	 * @return the restTransaction
	 */
	public RESTTransactionHolder getRestTransaction() {
		return restTransaction;
	}

	/**
	 * @param restTransaction the restTransaction to set
	 */
	public void setRestTransaction(RESTTransactionHolder restTransaction) {
		this.restTransaction = restTransaction;
	}

	@Override
	public IChappyCookie getCookie() {
		if (restTransaction != null) {
			return restTransaction.getCookie();
		}
		if (jmsTransaction != null) {
			return jmsTransaction.getCookie();
		}
		return null;
	}

	@Override
	public Client getRestClient() {
		return restTransaction.getRestClient();
	}

	@Override
	public URI getBaseUri() {
		return restTransaction.getBaseUri();
	}

	@Override
	public WebTarget getRestTarget() {
		return restTransaction.getRestTarget();
	}

	@Override
	public Connection getCurrentConnection() {
		return jmsTransaction.getCurrentConnection();
	}

	@Override
	public Session getCurrentSession() {
		return jmsTransaction.getCurrentSession();
	}

	@Override
	public MessageConsumer getCurrentMessageConsumer() {
		return jmsTransaction.getCurrentMessageConsumer();
	}

	@Override
	public MessageProducer getCurrentMessageProducer() {
		return jmsTransaction.getCurrentMessageProducer();
	}

	@Override
	public Destination getCurrentReplyToDestination() {
		return jmsTransaction.getCurrentReplyToDestination();
	}
}
