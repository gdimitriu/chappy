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
import chappy.clients.common.AbstractChappyLogin;
import chappy.clients.common.transaction.JMSTransactionHolder;
import chappy.clients.jms.protocol.JMSLoginMessage;
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
public class ChappyJMSLogin extends AbstractChappyLogin implements IJMSClient{
	
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
		setProtocol(new JMSLoginMessage(userName, passwd));
		getProtocol().setPersistence(persistence);
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
	 * @see chappy.interfaces.jms.IJMSClient#send()
	 */
	@Override
	public void send() throws JMSException {
		Destination destination = session.createQueue(IJMSQueueNameConstants.TRANSACTION);		
		producer = session.createProducer(destination);
		replyTo = session.createQueue(IJMSQueueNameConstants.TRANSACTION_RETURN);
		connection.start();
		Message message = ((JMSLoginMessage) getProtocol()).encodeInboundMessage(session);
		message.setJMSReplyTo(replyTo);
		producer.send(message);
		String messageID = message.getJMSMessageID();						
		consumer = session.createConsumer(replyTo, "JMSCorrelationID = '" + 
				messageID + "'");
		consumer.setMessageListener(this);		
		setProtocol(null);
	}
	
	@Override
	public void onMessage(Message message) {
		try {
			setProtocol(JMSLoginMessage.createDecodedReplyMessage(message));
		} catch (JMSException e) {
			if (getProtocol() == null) {
				setProtocol(new JMSLoginMessage());
			}
			getProtocol().setReplyMessage(e.getLocalizedMessage());
			getProtocol().setException(e);
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
		return new JMSTransactionHolder(connection, session, consumer, producer, getCookie(), replyTo);
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#getStatus()
	 */
	@Override
	public String getStatus() {
		if (getProtocol() == null) {
			return IJMSStatus.REPLY_NOT_READY;
		}
		return ((JMSLoginMessage )getProtocol()).getStatus();
	}

	@Override
	public int getStatusCode() {
		if (getProtocol() == null) {
			return -1;
		}
		return 0;
	}

	@Override
	public String getTransactionErrorMessage() {
		if (getProtocol() == null) {
			return IJMSMessages.REPLY_NOT_READY;
		}
		return getProtocol().getReplyMessage();
	}
	
	
}
