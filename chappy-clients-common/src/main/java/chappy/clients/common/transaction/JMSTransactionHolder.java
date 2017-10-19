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

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.jms.IJMSTransactionHolder;

/**
 * implementation for JMS transaction holder.
 * @author Gabriel Dimitriu
 *
 */
public class JMSTransactionHolder implements IJMSTransactionHolder {
	
	/** current session used by chappy */
	private Session currentSession = null;
	
	/** current connection used by chappy */
	private Connection currentConnection = null;
	
	/** current consumer for replies */
	private MessageConsumer currentMessageConsumer = null;
	
	/** current  producer for messages */
	private MessageProducer currentMessageProducer = null;
	
	/** reply to destination */
	private Destination replyTo = null;
	
	/** current cookie for chappy */
	private IChappyCookie currentCookie = null;
	
	/**
	 * @param currentSession
	 * @param currentConnection
	 * @param currentMessageConsumer
	 * @param currentMessageProducer
	 * @param currentCookie
	 * @param replyTo 
	 */
	public JMSTransactionHolder(final Connection currentConnection, final Session currentSession,
			final MessageConsumer currentMessageConsumer, final MessageProducer currentMessageProducer,
			final IChappyCookie currentCookie, final Destination replyTo) {
		super();
		this.currentSession = currentSession;
		this.currentConnection = currentConnection;
		this.currentMessageConsumer = currentMessageConsumer;
		this.currentMessageProducer = currentMessageProducer;
		this.currentCookie = currentCookie;
		this.replyTo = replyTo;
	}


	/**
	 *  default constructor.
	 */
	public JMSTransactionHolder() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @param currentSession
	 * @param currentConnection
	 * @param currentMessageConsumer
	 * @param currentMessageProducer
	 */
	public JMSTransactionHolder(Session currentSession, Connection currentConnection,
			MessageConsumer currentMessageConsumer, MessageProducer currentMessageProducer) {
		super();
		this.currentSession = currentSession;
		this.currentConnection = currentConnection;
		this.currentMessageConsumer = currentMessageConsumer;
		this.currentMessageProducer = currentMessageProducer;
	}

	/**
	 * @param current transaction connection
	 */
	public void setCurrentConnection(final Connection connection) {
		this.currentConnection = connection;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSTransactionHolder#getCurrentConnection()
	 */
	@Override
	public Connection getCurrentConnection() {
		return this.currentConnection;
	}

	/**
	 * @param current transaction session
	 */
	public void setCurrentSession(final Session session) {
		this.currentSession = session;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSTransactionHolder#getCurrentSession()
	 */
	@Override
	public Session getCurrentSession() {
		return this.currentSession;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSTransactionHolder#getCurrentMessageConsumer()
	 */
	@Override
	public MessageConsumer getCurrentMessageConsumer() {
		return this.currentMessageConsumer;
	}

	/**
	 * @param current transaction message consumer
	 */
	public void setCurrentMessagConsumer(final MessageConsumer consumer) {
		this.currentMessageConsumer = consumer;
	}

	/**
	 * @param current transaction message producer
	 */
	public void setCurrentMessageProducer(final MessageProducer producer) {
		this.currentMessageProducer = producer;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSTransactionHolder#getCurrentMessageProducer()
	 */
	@Override
	public MessageProducer getCurrentMessageProducer() {
		return this.currentMessageProducer;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.IClientTransaction#getCookie()
	 */
	@Override
	public IChappyCookie getCookie() {
		return this.currentCookie;
	}

	/**
	 * set transaction cookie.
	 * @param cookie for client transaction.
	 */
	public void setCookie(final IChappyCookie cookie) {
		this.currentCookie = cookie;
	}


	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSTransactionHolder#getCurrentReplyToDestination()
	 */
	@Override
	public Destination getCurrentReplyToDestination() {
		return replyTo;
	}

}
