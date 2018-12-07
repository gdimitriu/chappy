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
import javax.jms.JMSException;
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
	private IJMSTransactionHolder jmsTransaction = null;
	
	/** holder for the rest transaction */
	private IRESTTransactionHolder restTransaction = null;

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
	 * @throws Exception exception if the connection could not be created.
	 */
	public IJMSTransactionHolder getJmsTransaction() throws Exception {
		if (jmsTransaction == null && restTransaction != null) {
			jmsTransaction = new JMSTransactionHolder(restTransaction.getCookie());
		}
		return jmsTransaction;
	}

	/**
	 * @param jmsTransaction the jmsTransaction to set
	 */
	public void setJmsTransaction(final JMSTransactionHolder jmsTransaction) {
		this.jmsTransaction = jmsTransaction;
	}

	/**
	 * @return the restTransaction
	 */
	public IRESTTransactionHolder getRestTransaction() {
		if (restTransaction == null && jmsTransaction != null) {
			restTransaction = new RESTTransactionHolder(jmsTransaction.getCookie());
		}
		return restTransaction;
	}

	/**
	 * @param client the restTransaction to set
	 */
	public void setRestTransaction(final IRESTTransactionHolder client) {
		this.restTransaction = client;
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
		if (restTransaction == null && jmsTransaction != null) {
			restTransaction = new RESTTransactionHolder(jmsTransaction.getCookie());
		}
		return restTransaction.getRestClient();
	}

	@Override
	public URI getBaseUri() {
		if (restTransaction == null && jmsTransaction != null) {
			restTransaction = new RESTTransactionHolder(jmsTransaction.getCookie());
		}
		return restTransaction.getBaseUri();
	}

	@Override
	public WebTarget getRestTarget() {
		if (restTransaction == null && jmsTransaction != null) {
			restTransaction = new RESTTransactionHolder(jmsTransaction.getCookie());
		}
		return restTransaction.getRestTarget();
	}

	@Override
	public Connection getCurrentConnection() throws JMSException {
		if (jmsTransaction == null && restTransaction != null) {
			jmsTransaction = new JMSTransactionHolder(restTransaction.getCookie());
		}
		checkAndReconnectDeadSessions();
		return jmsTransaction.getCurrentConnection();
	}

	@Override
	public Session getCurrentSession() throws JMSException {
		if (jmsTransaction == null && restTransaction != null) {
			jmsTransaction = new JMSTransactionHolder(restTransaction.getCookie());
		}
		checkAndReconnectDeadSessions();
		return jmsTransaction.getCurrentSession();
	}

	@Override
	public MessageConsumer getCurrentMessageConsumer() throws JMSException {
		if (jmsTransaction == null && restTransaction != null) {
			jmsTransaction = new JMSTransactionHolder(restTransaction.getCookie());
		}
		checkAndReconnectDeadSessions();
		return jmsTransaction.getCurrentMessageConsumer();
	}

	@Override
	public MessageProducer getCurrentMessageProducer() throws JMSException {
		if (jmsTransaction == null && restTransaction != null) {
			jmsTransaction = new JMSTransactionHolder(restTransaction.getCookie());
		}
		checkAndReconnectDeadSessions();
		return jmsTransaction.getCurrentMessageProducer();
	}

	@Override
	public Destination getCurrentReplyToDestination() throws JMSException {
		if (jmsTransaction == null && restTransaction != null) {
			jmsTransaction = new JMSTransactionHolder(restTransaction.getCookie());
		}
		checkAndReconnectDeadSessions();
		return jmsTransaction.getCurrentReplyToDestination();
	}

	@Override
	public void createConnectionToServer(final String serverName, final int port) throws Exception {
		if (restTransaction != null) {
			restTransaction.createConnectionToServer(serverName, port);
		} else if (jmsTransaction != null) {
			jmsTransaction.createConnectionToServer(serverName, port);
		}
	}
	
	/**
	 * @throws JMSException
	 */
	private void checkAndReconnectDeadSessions() throws JMSException {
		if (jmsTransaction.isClosed()) {
			jmsTransaction = new JMSTransactionHolder(jmsTransaction.getCookie());
		}
	}

	@Override
	public String closeAll() {
		if (jmsTransaction != null) {
			return jmsTransaction.closeAll();
		}
		return null;
	}

	@Override
	public boolean isClosed() {
		if (jmsTransaction != null) {
			return jmsTransaction.isClosed();
		}
		return true;
	}

	@Override
	public void setCookie(final IChappyCookie cookie) {
		if (jmsTransaction != null) {
			jmsTransaction.setCookie(cookie);
		}
		if (restTransaction != null) {
			restTransaction.setCookie(cookie);
		}
	}
}
