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

import chappy.clients.common.AbstractChappyTransformFlow;
import chappy.clients.common.transaction.ChappyClientTransactionHolder;
import chappy.clients.common.transaction.JMSTransactionHolder;
import chappy.clients.jms.protocol.JMSTransformFlowMessage;
import chappy.interfaces.jms.IJMSClient;
import chappy.interfaces.jms.protocol.IJMSMessages;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.transactions.IClientTransaction;

/**
 * JMS request for run a static flow.
 * @author Gabriel Dimitriu
 *
 */
public class ChappyJMSTransformFlow extends AbstractChappyTransformFlow implements IJMSClient {

	/** client transaction coming from login */
	private ChappyClientTransactionHolder clientTransaction = new ChappyClientTransactionHolder();
	
	/**
	 * 
	 */
	public ChappyJMSTransformFlow(final String input, final String configuration, final IClientTransaction client) {
		if (client instanceof ChappyClientTransactionHolder) {
			clientTransaction = (ChappyClientTransactionHolder) client;
		} else if (client instanceof JMSTransactionHolder) {
			clientTransaction.setJmsTransaction((JMSTransactionHolder) client);
		}
		setProtocol(new JMSTransformFlowMessage(input, configuration, null));
		getProtocol().setCookie(client.getCookie());
	}
	
	/**
	 * 
	 */
	public ChappyJMSTransformFlow(final String input, final IClientTransaction client, final String flowName) {
		if (client instanceof ChappyClientTransactionHolder) {
			clientTransaction = (ChappyClientTransactionHolder) client;
		} else if (client instanceof JMSTransactionHolder) {
			clientTransaction.setJmsTransaction((JMSTransactionHolder) client);
		}
		setProtocol(new JMSTransformFlowMessage(input, null, flowName));
		getProtocol().setCookie(client.getCookie());
	}

	public ChappyJMSTransformFlow(final IClientTransaction client) {
		if (client instanceof ChappyClientTransactionHolder) {
			clientTransaction = (ChappyClientTransactionHolder) client;
		} else if (client instanceof JMSTransactionHolder) {
			clientTransaction.setJmsTransaction((JMSTransactionHolder) client);
		}
		setProtocol(new JMSTransformFlowMessage());
		getProtocol().setCookie(client.getCookie());
	}

	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(final Message message) {
		try {
			setProtocol(JMSTransformFlowMessage.createDecodedReplyMessage(message));
		} catch (JMSException e) {
			if (getProtocol() == null) {
				setProtocol(new JMSTransformFlowMessage());
			}
			getProtocol().setReplyMessage(e.getLocalizedMessage());
			getProtocol().setException(e);
		}

	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getStatus()
	 */
	@Override
	public String getStatus() {
		if (getProtocol() == null) {
			return IJMSStatus.REPLY_NOT_READY;
		}
		return ((JMSTransformFlowMessage) getProtocol()).getStatus();
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
			return IJMSMessages.REPLY_NOT_READY;
		}
		return getProtocol().getReplyMessage();
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#send()
	 */
	@Override
	public ChappyJMSTransformFlow send() throws JMSException {
		Message message = ((JMSTransformFlowMessage) getProtocol()).encodeInboundMessage(clientTransaction.getCurrentSession());
		message.setJMSReplyTo(clientTransaction.getCurrentReplyToDestination());
		clientTransaction.getCurrentMessageProducer().send(message);
		clientTransaction.getCurrentMessageConsumer().setMessageListener(this);		
		setProtocol(null);
		return this;
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

}
