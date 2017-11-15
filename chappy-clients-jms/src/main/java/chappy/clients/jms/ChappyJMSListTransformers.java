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

import chappy.clients.common.AbstractChappyListTransformers;
import chappy.clients.jms.protocol.JMSListTransformersMessage;
import chappy.interfaces.jms.IJMSClient;
import chappy.interfaces.jms.IJMSTransactionHolder;
import chappy.interfaces.jms.protocol.IJMSMessages;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.transactions.IClientTransaction;

/**
 * JMS request for list transformers
 * @author Gabriel Dimitriu
 *
 */
public class ChappyJMSListTransformers extends AbstractChappyListTransformers implements IJMSClient {

	/** client transaction coming from login */
	private IJMSTransactionHolder clientTransaction = null;
	
	public ChappyJMSListTransformers(final IClientTransaction client) {
		clientTransaction = (IJMSTransactionHolder) client;
		setProtocol(new JMSListTransformersMessage());
		getProtocol().setCookie(client.getCookie());
	}

	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(final Message message) {
		try {
			setProtocol(JMSListTransformersMessage.createDecodedReplyMessage(message));
		} catch (JMSException e) {
			if (getProtocol() == null) {
				setProtocol(new JMSListTransformersMessage());
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
		return ((JMSListTransformersMessage) getProtocol()).getStatus();
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
	public ChappyJMSListTransformers send() throws JMSException {
		Message message = ((JMSListTransformersMessage) getProtocol()).encodeInboundMessage(clientTransaction.getCurrentSession());
		message.setJMSReplyTo(clientTransaction.getCurrentReplyToDestination());
		clientTransaction.getCurrentMessageProducer().send(message);
		clientTransaction.getCurrentMessageConsumer().setMessageListener(this);		
		setProtocol(null);
		return this;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#closeAll()
	 */
	@Override
	public String closeAll() {
		boolean ok = true;
		String ret = "Chappy:=";
		try {
			clientTransaction.getCurrentSession().close();
		} catch (JMSException e) {
			ret = ret + " " + e.getLocalizedMessage();
			ok = false;
		}
		try {
			clientTransaction.getCurrentConnection().close();
		} catch (JMSException e) {
			ret = ret + " " + e.getLocalizedMessage();
			ok = false;
		}
		try {
			clientTransaction.getCurrentConnection().stop();
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
		return clientTransaction;
	}

}