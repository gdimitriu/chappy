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
import javax.jms.StreamMessage;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.jms.protocol.IJMSCommands;
import chappy.interfaces.jms.protocol.IJMSProtocol;
import chappy.interfaces.jms.protocol.IJMSProtocolKeys;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.providers.cookie.CookieFactory;

/**
 * @author Gabriel Dimitriu
 *
 */
public class JMSLoginMessage implements IJMSProtocol{

	/** user name */
	private String userName = null;
	
	/** user password */
	private String password = null;
	
	/** user persistence required */
	private boolean persistence = false;
	
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
	public JMSLoginMessage() {
		
	}
	
	/**
	 * @param userName of login user
	 * @param password of login user
	 */
	public JMSLoginMessage(final String userName, final String password) {
		this.userName = userName;
		this.password = password;
	}
	
	/**
	 * get the userName of the login user.
	 * @return userName of the login user.
	 */
	public String getUserName() {
		return this.userName;
	}
	
	/**
	 * get the password of the login user.
	 * @return password of the login user.
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * get the persistence flag;
	 * @return true if persistence is required.
	 */
	public boolean isPersistence() {
		return persistence;
	}
	
	
	/**
	 * set the required persistence.
	 * @param persistence true if persistence is required.
	 */
	public void setPersistence(final boolean persistence) {
		this.persistence = persistence;
	}
	
	/**
	 * get the reply message from the chappy.
	 * @return the replyMessage
	 */
	public String getReplyMessage() {
		return replyMessage;
	}
	
	/**
	 * set the replay message from chappy.
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

	/**
	 * set the cookie that correspond to this login
	 * @param cookie that correspond to this login.
	 */
	public void setCookie(final IChappyCookie cookie) {
		this.cookie = cookie;
	}
	
	/**
	 * get the cookie for this login.
	 * @return cookie for this login.
	 */
	public IChappyCookie getCookie() {
		return this.cookie;
	}
	
	/* (non-Javadoc)
	 * @see chappy.clients.jms.protocol.IJMSProtocol#decodeReply(javax.jms.Message)
	 */
	@Override
	public void decodeReply(final Message message) throws JMSException {
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
	
	/* (non-Javadoc)
	 * @see chappy.clients.jms.protocol.IJMSProtocol#encodeResponseMessage(javax.jms.Session)
	 */
	@Override
	public Message encodeResponseMessage(final Session session) throws JMSException {
		ObjectMessage message = session.createObjectMessage();
		message.setJMSCorrelationID(cookie.getTransactionId());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(IJMSProtocolKeys.REPLY_MESSAGE_KEY, replyMessage);
		if (cookie != null) {
			map.put(IJMSProtocolKeys.COOKIE_KEY, cookie);
		}
		if (exception != null) {
			map.put(IJMSProtocolKeys.REPLY_EXCEPTION_KEY, exception);
		}
		message.setObject(map);
		return message;
	}
	
	/* (non-Javadoc)
	 * @see chappy.clients.jms.protocol.IJMSProtocol#decodeInbound(javax.jms.Message)
	 */
	@Override
	public void decodeInbound(final Message message) throws JMSException {
		if (message instanceof StreamMessage) {
			StreamMessage strMsg = (StreamMessage) message;
			this.userName = strMsg.readString();
			this.password = strMsg.readString();
			this.persistence = strMsg.readBoolean();
			cookie = CookieFactory.getFactory().newCookie(this.getClass(), userName);
			cookie.setTransactionId(message.getJMSMessageID());
		}
	}
	
	/* (non-Javadoc)
	 * @see chappy.clients.jms.protocol.IJMSProtocol#encodeInboundMessage(javax.jms.Session)
	 */
	@Override
	public Message encodeInboundMessage(final Session session) throws JMSException {		
		StreamMessage message = session.createStreamMessage();
		message.setStringProperty(IJMSCommands.COMMAND_PROPERTY, IJMSCommands.LOGIN);
		message.writeString(this.userName);
		message.writeString(this.password);
		message.writeBoolean(this.persistence);		
		return message;
	}
	
	/**
	 * Create a decoded message from inbound received in chappy.
	 * @param message received by JMS
	 * @return decoded message
	 * @throws JMSException
	 */
	public static JMSLoginMessage decodeInboundMessage(final Message message) throws JMSException {
		JMSLoginMessage msg = new JMSLoginMessage();
		msg.decodeInbound(message);
		return msg;
	}
	
	/**
	 * Create a decoded reply message from inbound comming from chappy.
	 * @param message received by JMS
	 * @return decoded message
	 * @throws JMSException
	 */
	public static JMSLoginMessage decodeReplyMessage(final Message message) throws JMSException {
		JMSLoginMessage msg = new JMSLoginMessage();
		msg.decodeReply(message);
		return msg;
	}
}
