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
package chappy.clients.jms.protocol;

import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.jms.protocol.IJMSCommands;
import chappy.interfaces.jms.protocol.IJMSProtocol;
import chappy.interfaces.jms.protocol.IJMSProtocolKeys;
import chappy.interfaces.jms.protocol.IJMSStatus;

/**
 * @author Gabriel Dimitriu
 *
 */
public class JMSLogoutMessage implements IJMSProtocol {

	/** cookie for the transaction */
	private IChappyCookie cookie = null;
	
	/** reply message from chappy */
	private String replyMessage = "";
	
	/** exception in case of internal server */ 
	private Exception exception = null;
	
	/** status string */
	private String status = IJMSStatus.FORBIDDEN;
	
	/**
	 * default constructor. 
	 */
	public JMSLogoutMessage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the cookie
	 */
	public IChappyCookie getCookie() {
		return cookie;
	}

	/**
	 * @param cookie the cookie to set
	 */
	public void setCookie(final IChappyCookie cookie) {
		this.cookie = cookie;
	}

	/**
	 * @return the replyMessage
	 */
	public String getReplyMessage() {
		return replyMessage;
	}

	/**
	 * @param replyMessage the replyMessage to set
	 */
	public void setReplyMessage(final String replyMessage) {
		this.replyMessage = replyMessage;
	}
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(final String status) {
		this.status = status;
	}

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(final Exception exception) {
		this.exception = exception;
	}

	/* (non-Javadoc)
	 * @see chappy.clients.jms.protocol.IJMSProtocol#encodeInboundMessage(javax.jms.Session)
	 */
	@Override
	public Message encodeInboundMessage(final Session session) throws JMSException {
		ObjectMessage message = session.createObjectMessage();
		message.setStringProperty(IJMSCommands.COMMAND_PROPERTY, IJMSCommands.LOGOUT);
		message.setObject(cookie);
		return message;
	}

	/* (non-Javadoc)
	 * @see chappy.clients.jms.protocol.IJMSProtocol#decodeInbound(javax.jms.Message)
	 */
	@Override
	public void decodeInboundMessage(final Message message) throws JMSException {
		if (message instanceof ObjectMessage) {
			cookie = (IChappyCookie) ((ObjectMessage) message).getObject();
		}
	}

	/* (non-Javadoc)
	 * @see chappy.clients.jms.protocol.IJMSProtocol#encodeResponseMessage(javax.jms.Session)
	 */
	@Override
	public Message encodeReplyMessage(final Session session) throws JMSException {
		ObjectMessage message = session.createObjectMessage();
		message.setStringProperty(IJMSProtocolKeys.REPLY_STATUS_PROPERTY, status);
		message.setJMSCorrelationID(cookie.getTransactionId());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(IJMSProtocolKeys.REPLY_MESSAGE_KEY, replyMessage);
		if (cookie != null) {
			map.put(IJMSProtocolKeys.COOKIE_KEY, cookie);
		}
		if (exception != null) {
			map.put(IJMSProtocolKeys.REPLY_EXCEPTION_KEY, exception);
		}
		if (replyMessage != null) {
			map.put(IJMSProtocolKeys.REPLY_MESSAGE_KEY, replyMessage);
		}
		message.setObject(map);
		return message;
	}

	/* (non-Javadoc)
	 * @see chappy.clients.jms.protocol.IJMSProtocol#decodeReplyMessage(javax.jms.Message)
	 */
	@Override
	public void decodeReplyMessage(final Message message) throws JMSException {
		if (message instanceof ObjectMessage) {
			status = message.getStringProperty(IJMSProtocolKeys.REPLY_STATUS_PROPERTY);
			ObjectMessage msg = (ObjectMessage) message;
			@SuppressWarnings("unchecked")
			HashMap<String, Object> map = (HashMap<String, Object>) msg.getObject();
			if (map != null && !map.isEmpty()) {
				if (map.containsKey(IJMSProtocolKeys.REPLY_MESSAGE_KEY)) {
					this.replyMessage = (String) map.get(IJMSProtocolKeys.REPLY_MESSAGE_KEY);
				} else {
					this.replyMessage = null;
				}
				if (map.containsKey(IJMSProtocolKeys.COOKIE_KEY)) {
					this.cookie = (IChappyCookie) map.get(IJMSProtocolKeys.COOKIE_KEY);
				} else {
					this.cookie = null;
				}
				if (map.containsKey(IJMSProtocolKeys.REPLY_EXCEPTION_KEY)) {
					this.exception = (Exception) map.get(IJMSProtocolKeys.REPLY_EXCEPTION_KEY);
				}
			}
		}
	}
	
	/**
	 * Create a decoded message from inbound received in chappy.
	 * @param message received by JMS
	 * @return decoded message
	 * @throws JMSException
	 */
	public static JMSLogoutMessage createDecodedInboundMessage(final Message message) throws JMSException {
		JMSLogoutMessage msg = new JMSLogoutMessage();
		msg.decodeInboundMessage(message);
		return msg;
	}
	
	/**
	 * Create a decoded reply message from inbound comming from chappy.
	 * @param message received by JMS
	 * @return decoded message
	 * @throws JMSException
	 */
	public static JMSLogoutMessage createDecodedReplyMessage(final Message message) throws JMSException {
		JMSLogoutMessage msg = new JMSLogoutMessage();
		msg.decodeReplyMessage(message);
		return msg;
	}
	
	/**
	 * query if it has exception coming from chappy.
	 * @return true if has exception from chappy.
	 */
	public boolean hasException() {
		if (exception != null) {
			return true;			
		} else {
			return false;
		}
	}
}
