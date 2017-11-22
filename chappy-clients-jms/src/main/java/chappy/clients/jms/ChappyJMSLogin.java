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

import javax.jms.JMSException;
import javax.jms.Message;
import chappy.clients.common.AbstractChappyClient;
import chappy.clients.common.transaction.ChappyClientTransactionHolder;
import chappy.clients.common.transaction.JMSTransactionHolder;
import chappy.clients.jms.protocol.JMSLoginMessage;
import chappy.interfaces.jms.IJMSClient;
import chappy.interfaces.jms.protocol.IJMSMessages;
import chappy.interfaces.jms.protocol.IJMSStatus;

/**
 * Chappy login request client for JMS
 * @author Gabriel Dimitriu
 *
 */
public class ChappyJMSLogin extends AbstractChappyClient implements IJMSClient{
	
	private ChappyClientTransactionHolder transaction = new ChappyClientTransactionHolder();
	
	/**
	 * base constructor. 
	 * @param userName the user
	 * @param passwd the code
	 */
	public ChappyJMSLogin(final String userName, final String passwd, final boolean persistence) {
		setProtocol(new JMSLoginMessage(userName, passwd));
		((JMSLoginMessage) getProtocol()).setPersistence(persistence);
		transaction.setJmsTransaction(new JMSTransactionHolder(userName, passwd, persistence));
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
	public ChappyJMSLogin send() throws Exception {
		transaction.getJmsTransaction().startTransaction();
		Message message = ((JMSLoginMessage) getProtocol()).encodeInboundMessage(transaction.getCurrentSession());
		message.setJMSReplyTo(transaction.getCurrentReplyToDestination());
		transaction.getCurrentMessageProducer().send(message);
		String messageID = message.getJMSMessageID();
		transaction.getJmsTransaction().createMessageConsumerFilter(messageID);
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
	 * @see chappy.interfaces.jms.IJMSClient#createTransactionHolder()
	 */
	@Override
	public ChappyClientTransactionHolder createTransactionHolder() throws Exception {
		transaction.getJmsTransaction().setCookie(getCookie());
		transaction.getJmsTransaction().closeAll();
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
