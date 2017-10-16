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
package chappy.clients.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;

import chappy.clients.jms.protocol.JMSLoginMessage;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.jms.IJMSClient;
import chappy.interfaces.jms.IJMSTransactionHolder;
import chappy.interfaces.jms.protocol.IJMSMessages;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.jms.resources.IJMSQueueNameConstants;

/**
 * client for the chappy transaction login on JMS. 
 * @author Gabriel Dimitriu
 *
 */
public class ChappyJMSLogin implements IJMSClient{
	
	/** internal handler for jms protocol */
	private JMSLoginMessage loginProtocol = null;
	
	/** session used by chappy */
	private Session session = null;
	
	/** connection used by chappy */
	private Connection connection = null;
	
	/** reply to destination */
	private Destination replyTo = null;
	
	/** consumer for replies */
	private MessageConsumer consumer = null;
	
	/** producer for messages */
	private MessageProducer producer = null;
	
	/**
	 * base constructor. 
	 * @param userName the user
	 * @param passwd the code
	 */
	public ChappyJMSLogin(final String userName, final String passwd, final boolean persistence) {
		loginProtocol = new JMSLoginMessage(userName, passwd);
		loginProtocol.setPersistence(persistence);
	}
	
	/**
	 * @param serverName
	 * @param port
	 * @throws Exception 
	 */
	public void createConnectionToServer(final String serverName, final int port) throws Exception {
		ConnectionFactory connFactory = ActiveMQJMSClient.createConnectionFactory("tcp://" + serverName + ":" + port, "default");
		connection = connFactory.createConnection("system","system");
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#getCookie()
	 */
	@Override
	public IChappyCookie getCookie() {
		if (loginProtocol == null) {
			return null;
		}
		return loginProtocol.getCookie();
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#getStatus()
	 */
	@Override
	public String getStatus() {
		if (loginProtocol == null) {
			return IJMSStatus.REPLY_NOT_READY;
		}
		return loginProtocol.getStatus();
	}
	
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#hasException()
	 */
	@Override
	public boolean hasException() {
		if (loginProtocol == null) {
			return false;
		}
		return loginProtocol.hasException();
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#getTransactionErrorMessage()
	 */
	@Override
	public String getTransactionErrorMessage() {
		if (loginProtocol == null) {
			return IJMSMessages.REPLY_NOT_READY;
		}
		return loginProtocol.getReplyMessage();
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getTransactionException()
	 */
	@Override
	public Exception getTransactionException() {
		if (loginProtocol == null) {
			return null;
		}
		return loginProtocol.getException();		
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#send()
	 */
	@Override
	public void send() throws JMSException {
		Destination destination = session.createQueue(IJMSQueueNameConstants.TRANSACTION);		
		producer = session.createProducer(destination);
		replyTo = session.createQueue(IJMSQueueNameConstants.TRANSACTION_RETURN);
		connection.start();
		Message message = loginProtocol.encodeInboundMessage(session);
		message.setJMSReplyTo(replyTo);
		producer.send(message);
		String messageID = message.getJMSMessageID();						
		consumer = session.createConsumer(replyTo, "JMSCorrelationID = '" + 
				messageID + "'");
		consumer.setMessageListener(this);		
		loginProtocol = null;
	}
	
	@Override
	public void onMessage(Message message) {
		try {
			loginProtocol = JMSLoginMessage.createDecodedReplyMessage(message);
		} catch (JMSException e) {
			if (loginProtocol == null) {
				loginProtocol = new JMSLoginMessage();
			}
			loginProtocol.setReplyMessage(e.getLocalizedMessage());
			loginProtocol.setException(e);
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
			session.close();
		} catch (JMSException e) {
			ret = ret + " " + e.getLocalizedMessage();
			ok = false;
		}
		try {
			connection.close();
		} catch (JMSException e) {
			ret = ret + " " + e.getLocalizedMessage();
			ok = false;
		}
		try {
			connection.stop();
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

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#createTransactionHolder()
	 */
	@Override
	public IJMSTransactionHolder createTransactionHolder() {
		return new JMSTransactionHolder(connection, session, consumer, producer, loginProtocol.getCookie(), replyTo);
	}
}
