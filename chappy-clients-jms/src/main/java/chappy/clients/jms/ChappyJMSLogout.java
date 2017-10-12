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
public class ChappyJMSLogout implements IJMSClient {

	/** client transaction coming from login */
	private IJMSTransactionHolder clientTransaction = null;
	
	/** internal handler for jms protocol */
	private JMSLogoutMessage logoutProtocol = null;
	
	/**
	 * @param client transaction coming from login
	 */
	public ChappyJMSLogout(final IClientTransaction client) {
		clientTransaction = (IJMSTransactionHolder) client;
		logoutProtocol = new JMSLogoutMessage();
		logoutProtocol.setCookie(client.getCookie());
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.IJMSClient#send()
	 */
	@Override
	public void send() throws JMSException {
		Message message = logoutProtocol.encodeInboundMessage(clientTransaction.getCurrentSession());
		message.setJMSReplyTo(clientTransaction.getCurrentReplyToDestination());
		clientTransaction.getCurrentMessageProducer().send(message);
		clientTransaction.getCurrentMessageConsumer().setMessageListener(this);		
		logoutProtocol = null;
		
	}
	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(final Message message) {
		try {
			logoutProtocol = JMSLogoutMessage.createDecodedReplyMessage(message);
		} catch (JMSException e) {
			if (logoutProtocol == null) {
				logoutProtocol = new JMSLogoutMessage();
			}
			logoutProtocol.setReplyMessage(e.getLocalizedMessage());
			logoutProtocol.setException(e);
		}
		
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getCookie()
	 */
	@Override
	public IChappyCookie getCookie() {
		if (logoutProtocol == null) {
			return null;
		}
		return clientTransaction.getCookie();
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getStatus()
	 */
	@Override
	public String getStatus() {
		if (logoutProtocol == null) {
			return IJMSStatus.REPLY_NOT_READY;
		}
		return logoutProtocol.getStatus();
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#hasException()
	 */
	@Override
	public boolean hasException() {
		if (logoutProtocol == null) {
			return false;
		}
		return logoutProtocol.hasException();
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getTransactionErrorMessage()
	 */
	@Override
	public String getTransactionErrorMessage() {
		if (logoutProtocol == null) {
			return IJMSStatus.REPLY_NOT_READY;
		}
		return logoutProtocol.getReplyMessage();
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IChappyClient#getTransactionException()
	 */
	@Override
	public Exception getTransactionException() {
		if (logoutProtocol == null) {
			return null;
		}
		return logoutProtocol.getException();
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
