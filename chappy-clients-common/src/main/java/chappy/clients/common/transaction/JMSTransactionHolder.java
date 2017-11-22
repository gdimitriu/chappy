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
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.artemis.api.core.ActiveMQDisconnectedException;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;

import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.jms.IJMSTransactionHolder;
import chappy.interfaces.jms.resources.IJMSQueueNameConstants;
import chappy.providers.cookie.CookieFactory;

/**
 * implementation for JMS transaction holder.
 * @author Gabriel Dimitriu
 *
 */
public class JMSTransactionHolder implements IJMSTransactionHolder, ExceptionListener {
	
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
	
	/** exception received during transactions */
	private JMSException exceptionReceived = null;
	
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
	 * create a jms transaction from cookie.
	 * this is used when converting transaction from one protocol to other.
	 * @param cookie
	 * @throws JMSException the exception if the connection could not be created.
	 */
	public JMSTransactionHolder(final IChappyCookie cookie) throws JMSException {
		currentCookie = cookie;
		createTransactonConnections(cookie);
	}


	/**
	 * @param cookie
	 */
	private void createTransactonConnections(final IChappyCookie cookie) {
		try {
			createConnectionToServer(cookie.getJmsServerName(), cookie.getJmsServerPort());
			startTransaction();
			createMessageConsumerFilter(cookie.getCorrelationId());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}


	
	/**
	 * @param userName
	 * @param passwd
	 * @param persistence
	 */
	public JMSTransactionHolder(final String userName, final String passwd, final boolean persistence) {
		currentCookie = CookieFactory.getFactory().newCookie(userName, passwd, persistence);
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
		if (this.currentConnection == null) {
			createTransactonConnections(currentCookie);
		}
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
		if (this.currentConnection == null) {
			createTransactonConnections(currentCookie);
		}
		return this.currentSession;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSTransactionHolder#getCurrentMessageConsumer()
	 */
	@Override
	public MessageConsumer getCurrentMessageConsumer() {
		if (this.currentConnection == null) {
			createTransactonConnections(currentCookie);
		}
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
		if (this.currentConnection == null) {
			createTransactonConnections(currentCookie);
		}
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
		if (this.currentConnection == null) {
			createTransactonConnections(currentCookie);
		}
		return replyTo;
	}

	/**
	 * @param serverName
	 * @param port
	 * @throws Exception 
	 */
	public void createConnectionToServer(final String serverName, final int port) throws JMSException {
		ConnectionFactory connFactory = null;
		try {
			connFactory = ActiveMQJMSClient.createConnectionFactory("tcp://" + serverName + ":" + port, "default");
		} catch (Exception e) {
			e.printStackTrace();
			throw new JMSException(e.getMessage());
		}
		currentConnection = connFactory.createConnection("system","system");
		currentSession = currentConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		currentConnection.setExceptionListener(this);
	}

	/**
	 * set the reply to
	 * @param destination
	 */
	public void setCurrentReplyToDestination(final Queue destination) {
		this.replyTo = destination;
	}
	
	/**
	 * create the consumer with filter for the correlation message.
	 * @param correlationId
	 * @throws JMSException
	 */
	public void createMessageConsumerFilter(final String correlationId) throws JMSException {
		if (replyTo == null) {
			replyTo = currentSession.createQueue(IJMSQueueNameConstants.TRANSACTION_RETURN);
		}
		currentMessageConsumer = currentSession.createConsumer(replyTo, "JMSCorrelationID = '" + 
		correlationId + "'");
	}

	/**
	 * check is the connection is closed.
	 * @return true if the connection is closed.
	 */
	public boolean isClosed() {
		if (currentConnection == null || (exceptionReceived != null && exceptionReceived.getCause() instanceof ActiveMQDisconnectedException)) {
			return true;
		}
		return false;
	}
	
	@Override
	public void onException(JMSException exception) {
		exceptionReceived = exception;
		if (exception.getCause()  instanceof ActiveMQDisconnectedException) {
			currentConnection = null;
			currentSession = null;
			currentMessageConsumer = null;
			currentMessageProducer = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#closeAll()
	 */
	@Override
	public String closeAll() {
		boolean ok = true;
		String ret = "Chappy:=";
		try {
			currentSession.close();
			currentSession = null;
		} catch (JMSException e) {
			ret = ret + " " + e.getLocalizedMessage();
			ok = false;
		}
		try {
			currentConnection.stop();
		} catch (JMSException e) {
			ret = ret + " " + e.getLocalizedMessage();
			ok = false;
		}
		try {
			currentConnection.close();
			currentConnection = null;
		} catch (JMSException e) {
			ret = ret + " " + e.getLocalizedMessage();
			ok =false;
		}
		if (!ok) {
			return ret;
		} else {
			return ret + " has been stopped ok.";
		}
	}
	
	public void startTransaction() throws JMSException {
		Destination destination = currentSession.createQueue(IJMSQueueNameConstants.TRANSACTION);		
		currentMessageProducer = currentSession.createProducer(destination);
		replyTo = currentSession.createQueue(IJMSQueueNameConstants.TRANSACTION_RETURN);
		currentConnection.start();
	}
}