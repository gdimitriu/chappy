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
import chappy.clients.jms.protocol.JMSLogoutMessage;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.jms.IJMSClient;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.transactions.IClientTransaction;

/**
 * Chappy logout request client for JMS.
 * @author Gabriel Dimitriu
 *
 */
public class ChappyJMSLogout extends AbstractChappyClient implements IJMSClient {

	/** client transaction coming from login */
	private ChappyClientTransactionHolder clientTransaction = new ChappyClientTransactionHolder();
	
	
	/**
	 * @param client transaction coming from login
	 */
	public ChappyJMSLogout(final IClientTransaction client) {
		if (client instanceof ChappyClientTransactionHolder) {
			clientTransaction = (ChappyClientTransactionHolder) client;
		} else if (client instanceof JMSTransactionHolder) {
			clientTransaction.setJmsTransaction((JMSTransactionHolder) client);
		}
		setProtocol(new JMSLogoutMessage());
		getProtocol().setCookie(client.getCookie());
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#send()
	 */
	@Override
	public ChappyJMSLogout send() throws JMSException {
		Message message = ((JMSLogoutMessage) getProtocol()).encodeInboundMessage(clientTransaction.getCurrentSession());
		message.setJMSReplyTo(clientTransaction.getCurrentReplyToDestination());
		clientTransaction.getCurrentMessageProducer().send(message);
		clientTransaction.getCurrentMessageConsumer().setMessageListener(this);		
		setProtocol(null);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(final Message message) {
		try {
			setProtocol(JMSLogoutMessage.createDecodedReplyMessage(message));
		} catch (JMSException e) {
			if (getProtocol() == null) {
				setProtocol(new JMSLogoutMessage());
			}
			getProtocol().setReplyMessage(e.getLocalizedMessage());
			getProtocol().setException(e);
		}
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getCookie()
	 */
	@Override
	public IChappyCookie getCookie() {
		if (getProtocol() == null) {
			return null;
		}
		return clientTransaction.getCookie();
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#createTransactionHolder()
	 */
	@Override
	public ChappyClientTransactionHolder createTransactionHolder() {
		try {
			clientTransaction.getJmsTransaction().closeAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clientTransaction;
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getStatus()
	 */
	@Override
	public String getStatus() {
		if (getProtocol() == null) {
			return IJMSStatus.REPLY_NOT_READY;
		}
		return ((JMSLogoutMessage) getProtocol()).getStatus();
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getStatusCode()
	 */
	@Override
	public int getStatusCode() {
		if (getProtocol() == null) {
			return -1;
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getTransactionErrorMessage()
	 */
	@Override
	public String getTransactionErrorMessage() {
		if (getProtocol() == null) {
			return IJMSStatus.REPLY_NOT_READY;
		}
		return getProtocol().getReplyMessage();
	}
}
