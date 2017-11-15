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
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import chappy.clients.common.protocol.AbstractChappyListTransformersMessage;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.jms.protocol.IJMSCommands;
import chappy.interfaces.jms.protocol.IJMSProtocol;
import chappy.interfaces.jms.protocol.IJMSProtocolKeys;
import chappy.interfaces.jms.protocol.IJMSStatus;

/**
 * 
 * Chappy list transformers request protocol message implementation for JMS.
 * @author Gabriel Dimitriu
 *
 */
public class JMSListTransformersMessage extends AbstractChappyListTransformersMessage implements IJMSProtocol{

	/** status string */
	private String status = IJMSStatus.FORBIDDEN;
	
	/**
	 * default constructor used for reply messages.
	 */
	public JMSListTransformersMessage() {
		
	}
		
	/**
	 * constructor used by exception to add a message.
	 * @param message
	 */
	public JMSListTransformersMessage(final String message) {
		setReplyMessage(message);
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

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.protocol.IJMSProtocol#encodeInboundMessage(javax.jms.Session)
	 */
	@Override
	public Message encodeInboundMessage(final Session session) throws JMSException {
		ObjectMessage message = session.createObjectMessage();
		message.setStringProperty(IJMSCommands.COMMAND_PROPERTY, IJMSCommands.LIST_TRANSFORMERS);
		message.setObject(getCookie());
		return message;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.protocol.IJMSProtocol#decodeInboundMessage(javax.jms.Message)
	 */
	@Override
	public void decodeInboundMessage(final Message message) throws JMSException {
		ObjectMessage objMsg = (ObjectMessage) message;
		setCookie((IChappyCookie) objMsg.getObject());
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.protocol.IJMSProtocol#encodeReplyMessage(javax.jms.Session)
	 */
	@Override
	public Message encodeReplyMessage(final Session session) throws JMSException {
		ObjectMessage message = session.createObjectMessage();
		message.setStringProperty(IJMSProtocolKeys.REPLY_STATUS_KEY, status);
		message.setJMSCorrelationID(getCookie().getTransactionId());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(IJMSProtocolKeys.REPLY_MESSAGE_KEY, getReplyMessage());
		if (getCookie() != null) {
			map.put(IJMSProtocolKeys.COOKIE_KEY, getCookie());
		}
		if (getException() != null) {
			map.put(IJMSProtocolKeys.REPLY_EXCEPTION_KEY, getException());
		}
		if (getReplyMessage() != null) {
			map.put(IJMSProtocolKeys.REPLY_MESSAGE_KEY, getReplyMessage());
		}
		if (getListOfTransformersName() != null) {
			map.put(IJMSProtocolKeys.TRANSFORMERS_KEY, getListOfTransformersName());
		}
		message.setObject(map);
		return message;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.protocol.IJMSProtocol#decodeReplyMessage(javax.jms.Message)
	 */
	@SuppressWarnings("unchecked")	
	@Override
	public void decodeReplyMessage(final Message message) throws JMSException {
		if (message instanceof ObjectMessage) {
			status = message.getStringProperty(IJMSProtocolKeys.REPLY_STATUS_KEY);
			ObjectMessage msg = (ObjectMessage) message;
			HashMap<String, Object> map = (HashMap<String, Object>) msg.getObject();
			if (map != null && !map.isEmpty()) {
				if (map.containsKey(IJMSProtocolKeys.REPLY_MESSAGE_KEY)) {
					setReplyMessage((String) map.get(IJMSProtocolKeys.REPLY_MESSAGE_KEY));
				} else {
					setReplyMessage(null);
				}
				if (map.containsKey(IJMSProtocolKeys.COOKIE_KEY)) {
					setCookie((IChappyCookie) map.get(IJMSProtocolKeys.COOKIE_KEY));
				} else {
					setCookie(null);
				}
				if (map.containsKey(IJMSProtocolKeys.REPLY_EXCEPTION_KEY)) {
					setException((Exception) map.get(IJMSProtocolKeys.REPLY_EXCEPTION_KEY));
				}
				if (map.containsKey(IJMSProtocolKeys.TRANSFORMERS_KEY)) {
					setListOfTransformersName((List<String>) map.get(IJMSProtocolKeys.TRANSFORMERS_KEY));
				}
			}
		}
	}

	/**
	 * created the decoded reply message from chappy.
	 * @param message from JMS
	 * @return this as a factory.
	 * @throws JMSException
	 */
	public static JMSListTransformersMessage createDecodedReplyMessage(final Message message) throws JMSException {
		JMSListTransformersMessage transformer = new JMSListTransformersMessage();
		transformer.decodeReplyMessage(message);
		return transformer;
	}

	/**
	 * Create a decoded message from inbound received in chappy.
	 * @param message received by JMS
	 * @return decoded message
	 * @throws JMSException
	 */
	public static JMSListTransformersMessage createDecodedInboundMessage(final Message message) throws JMSException {
		JMSListTransformersMessage msg = new JMSListTransformersMessage();
		msg.decodeInboundMessage(message);
		return msg;
	}
}
