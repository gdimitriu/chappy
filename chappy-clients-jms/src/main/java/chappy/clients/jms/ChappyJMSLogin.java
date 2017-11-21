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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import chappy.clients.common.AbstractChappyClient;
import chappy.clients.common.transaction.JMSTransactionHolder;
import chappy.clients.jms.protocol.JMSLoginMessage;
import chappy.interfaces.jms.IJMSClient;
import chappy.interfaces.jms.IJMSTransactionHolder;
import chappy.interfaces.jms.protocol.IJMSMessages;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.jms.resources.IJMSQueueNameConstants;

/**
 * Chappy login request client for JMS
 * @author Gabriel Dimitriu
 *
 */
public class ChappyJMSLogin extends AbstractChappyClient implements IJMSClient{
	
	private JMSTransactionHolder transaction = null;
	
	/**
	 * base constructor. 
	 * @param userName the user
	 * @param passwd the code
	 */
	public ChappyJMSLogin(final String userName, final String passwd, final boolean persistence) {
		setProtocol(new JMSLoginMessage(userName, passwd));
		((JMSLoginMessage) getProtocol()).setPersistence(persistence);
		transaction = new JMSTransactionHolder(userName, passwd, persistence);
	}
	
	/**
	 * @param serverName
	 * @param port
	 * @throws Exception 
	 */
	public void createConnectionToServer(final String serverName, final int port) throws Exception {
		transaction.createConnectionToServer(serverName, port);
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#send()
	 */
	@Override
	public ChappyJMSLogin send() throws JMSException {
		Destination destination = transaction.getCurrentSession().createQueue(IJMSQueueNameConstants.TRANSACTION);		
		transaction.setCurrentMessageProducer(transaction.getCurrentSession().createProducer(destination));
		transaction.setCurrentReplyToDestination(transaction.getCurrentSession().createQueue(IJMSQueueNameConstants.TRANSACTION_RETURN));
		transaction.getCurrentConnection().start();
		Message message = ((JMSLoginMessage) getProtocol()).encodeInboundMessage(transaction.getCurrentSession());
		message.setJMSReplyTo(transaction.getCurrentReplyToDestination());
		transaction.getCurrentMessageProducer().send(message);
		String messageID = message.getJMSMessageID();
		transaction.createMessageConsumerFilter(messageID);
		transaction.getCurrentMessageConsumer().setMessageListener(this);
		setProtocol(null);
		return this;
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
			transaction.getCurrentSession().close();
		} catch (JMSException e) {
			ret = ret + " " + e.getLocalizedMessage();
			ok = false;
		}
		try {
			transaction.getCurrentConnection().close();
		} catch (JMSException e) {
			ret = ret + " " + e.getLocalizedMessage();
			ok = false;
		}
		try {
			transaction.getCurrentConnection().stop();
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
		transaction.setCookie(getCookie());
		return transaction;
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
