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

import org.apache.commons.collections4.MultiValuedMap;

import chappy.clients.common.protocol.AbstractChappyAddFlowMessage;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.flows.MultiDataQueryHolder;
import chappy.interfaces.jms.protocol.IJMSCommands;
import chappy.interfaces.jms.protocol.IJMSProtocol;
import chappy.interfaces.jms.protocol.IJMSProtocolKeys;
import chappy.interfaces.jms.protocol.IJMSStatus;

/**
 * 
 * Chappy add flow request protocol message implementation for JMS.
 * @author Gabriel Dimitriu
 *
 */
public class JMSAddFlowMessage extends AbstractChappyAddFlowMessage implements IJMSProtocol{

	/** status string */
	private String status = IJMSStatus.FORBIDDEN;
	
	/** hold the queries of the system */
	private MultiDataQueryHolder queries = new MultiDataQueryHolder();
	
	/**
	 * 
	 */
	public JMSAddFlowMessage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param flowName
	 * @param flowDefiniton
	 */
	public JMSAddFlowMessage(final String flowName, final String flowDefiniton) {
		super(flowName, flowDefiniton);
	}
	
	/**
	 * @param flowName
	 */
	public JMSAddFlowMessage(final String flowName) {
		super(flowName);
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

	@Override
	public Message encodeInboundMessage(Session session) throws JMSException {
		ObjectMessage message = session.createObjectMessage();
		message.setStringProperty(IJMSCommands.COMMAND_PROPERTY, IJMSCommands.ADD_FLOW);
		if (getFlowName() != null) {
			message.setStringProperty(IJMSProtocolKeys.FLOW_NAME, getFlowName());
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(IJMSProtocolKeys.COOKIE_KEY, getCookie());
		if (getConfiguration() != null && !"".equals(getConfiguration())) {
			map.put(IJMSProtocolKeys.FLOW_CONFIGURATION_KEY, getConfiguration());
		}
		map.put(IJMSProtocolKeys.FLOW_QUERIES_KEY, queries.getQueries());
		message.setObject(map);
		return message;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void decodeInboundMessage(Message message) throws JMSException {
		ObjectMessage objMsg = (ObjectMessage) message;
		HashMap<String, Object> retObj = (HashMap<String, Object>) ((ObjectMessage) objMsg).getObject();
		if (retObj.containsKey(IJMSProtocolKeys.COOKIE_KEY)) {
			setCookie((IChappyCookie) retObj.get(IJMSProtocolKeys.COOKIE_KEY));
		}
		if (message.propertyExists(IJMSProtocolKeys.FLOW_NAME)) {
			setFlowName(message.getStringProperty(IJMSProtocolKeys.FLOW_NAME));
		}
		if (retObj.containsKey(IJMSProtocolKeys.FLOW_CONFIGURATION_KEY)) {
			setConfiguration((String) retObj.get(IJMSProtocolKeys.FLOW_CONFIGURATION_KEY));
		}
		queries.setQueries((MultiValuedMap<String, String>) retObj.get(IJMSProtocolKeys.FLOW_QUERIES_KEY));
	}

	@Override
	public Message encodeReplyMessage(Session session) throws JMSException {
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
		message.setObject(map);
		return message;
	}

	@Override
	public void decodeReplyMessage(Message message) throws JMSException {
		if (message instanceof ObjectMessage) {
			status = message.getStringProperty(IJMSProtocolKeys.REPLY_STATUS_KEY);
			ObjectMessage msg = (ObjectMessage) message;
			@SuppressWarnings("unchecked")
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
			}
		}
	}
	
	/**
	 * @return MultiDataQueryHolder of the decoded queries.
	 */
	public MultiDataQueryHolder getMultidataQuery() {
		return queries;
	}
	
	/**
	 * created the decoded reply message from chappy.
	 * @param message from JMS
	 * @return this as a factory.
	 * @throws JMSException
	 */
	public static JMSAddFlowMessage createDecodedReplyMessage(final Message message) throws JMSException {
		JMSAddFlowMessage transformer = new JMSAddFlowMessage();
		transformer.decodeReplyMessage(message);
		return transformer;
	}

	/**
	 * Create a decoded message from inbound received in chappy.
	 * @param message received by JMS
	 * @return decoded message
	 * @throws JMSException
	 */
	public static JMSAddFlowMessage createDecodedInboundMessage(final Message message) throws JMSException {
		JMSAddFlowMessage msg = new JMSAddFlowMessage();
		msg.decodeInboundMessage(message);
		return msg;
	}

}
