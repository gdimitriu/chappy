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

import chappy.clients.common.AbstractChappyLogout;
import chappy.clients.jms.protocol.JMSLogoutMessage;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.jms.IJMSClient;
import chappy.interfaces.jms.IJMSTransactionHolder;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.transactions.IClientTransaction;

/**
 * client for the Chappy transaction logout on JMS.
 * @author Gabriel Dimitriu
 *
 */
public class ChappyJMSLogout extends AbstractChappyLogout implements IJMSClient {

	/** client transaction coming from login */
	private IJMSTransactionHolder clientTransaction = null;
	
	
	/**
	 * @param client transaction coming from login
	 */
	public ChappyJMSLogout(final IClientTransaction client) {
		clientTransaction = (IJMSTransactionHolder) client;
		setLogoutProtocol(new JMSLogoutMessage());
		getLogoutProtocol().setCookie(client.getCookie());
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#send()
	 */
	@Override
	public void send() throws JMSException {
		Message message = ((JMSLogoutMessage) getLogoutProtocol()).encodeInboundMessage(clientTransaction.getCurrentSession());
		message.setJMSReplyTo(clientTransaction.getCurrentReplyToDestination());
		clientTransaction.getCurrentMessageProducer().send(message);
		clientTransaction.getCurrentMessageConsumer().setMessageListener(this);		
		setLogoutProtocol(null);
		
	}
	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(final Message message) {
		try {
			setLogoutProtocol(JMSLogoutMessage.createDecodedReplyMessage(message));
		} catch (JMSException e) {
			if (getLogoutProtocol() == null) {
				setLogoutProtocol(new JMSLogoutMessage());
			}
			getLogoutProtocol().setReplyMessage(e.getLocalizedMessage());
			getLogoutProtocol().setException(e);
		}
		
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getCookie()
	 */
	@Override
	public IChappyCookie getCookie() {
		if (getLogoutProtocol() == null) {
			return null;
		}
		return clientTransaction.getCookie();
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
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getStatus()
	 */
	@Override
	public String getStatus() {
		if (getLogoutProtocol() == null) {
			return IJMSStatus.REPLY_NOT_READY;
		}
		return ((JMSLogoutMessage) getLogoutProtocol()).getStatus();
	}
}
