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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.collections4.MultiValuedMap;

import chappy.clients.common.protocol.AbstractChappyTransformFlowMessage;
import chappy.interfaces.IChappyProtocol;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.flows.MultiDataQueryHolder;
import chappy.interfaces.jms.protocol.IJMSCommands;
import chappy.interfaces.jms.protocol.IJMSProtocol;
import chappy.interfaces.jms.protocol.IJMSProtocolKeys;
import chappy.interfaces.jms.protocol.IJMSStatus;

/**
 * Chappy transform request protocol message implementation for JMS.
 * @author Gabriel Dimitriu
 *
 */
public class JMSTransformFlowMessage extends AbstractChappyTransformFlowMessage implements IJMSProtocol {

	/** status string */
	private String status = IJMSStatus.FORBIDDEN;
	
	/** hold the queries of the system */
	private MultiDataQueryHolder queries = new MultiDataQueryHolder();
	
	/**
	 * 
	 */
	public JMSTransformFlowMessage(final String configuration) {
		setConfiguration(configuration);
	}

	public JMSTransformFlowMessage() {
		// TODO Auto-generated constructor stub
	}

	public JMSTransformFlowMessage(final String input, final String configuration, final String flowName) {
		super(input, configuration, flowName);
	}

	@Override
	public Message encodeInboundMessage(final Session session) throws JMSException {
		ObjectMessage message = session.createObjectMessage();
		message.setStringProperty(IJMSCommands.COMMAND_PROPERTY, IJMSCommands.FLOW);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(IJMSProtocolKeys.COOKIE_KEY, getCookie());
		map.put(IJMSProtocolKeys.FLOW_CONFIGURATION_KEY, getConfiguration());
		map.put(IJMSProtocolKeys.FLOW_QUERIES_KEY, queries.getQueries());
		map.put(IJMSProtocolKeys.DATA_FLOW_NR_KEY, getInputs().size());
		for (int i = 0; i < getInputs().size(); i++) {
			map.put(IJMSProtocolKeys.DATA_FLOW_KEY + i, getInputs().get(i));
		}
		message.setObject(map);
		return message;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void decodeInboundMessage(final Message message) throws JMSException {
		ObjectMessage objMsg = (ObjectMessage) message;
		HashMap<String, Object> retObj = (HashMap<String, Object>) ((ObjectMessage) objMsg).getObject();
		if (retObj.containsKey(IJMSProtocolKeys.COOKIE_KEY)) {
			setCookie((IChappyCookie) retObj.get(IJMSProtocolKeys.COOKIE_KEY));
		}
		setConfiguration((String) retObj.get(IJMSProtocolKeys.FLOW_CONFIGURATION_KEY));
		queries.setQueries((MultiValuedMap<String, String>) retObj.get(IJMSProtocolKeys.FLOW_QUERIES_KEY));
		int size = (Integer) retObj.get(IJMSProtocolKeys.DATA_FLOW_NR_KEY);
		List<String> inputs = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			inputs.add((String) retObj.get(IJMSProtocolKeys.DATA_FLOW_KEY + i));
		}
		setInputs(inputs);
	}

	@Override
	public Message encodeReplyMessage(final Session session) throws JMSException {
		ObjectMessage replyMessage = session.createObjectMessage();
		replyMessage.setStringProperty(IJMSProtocolKeys.REPLY_STATUS_KEY, status);
		replyMessage.setJMSCorrelationID(getCookie().getTransactionId());
		HashMap<String, Object> retObj = new HashMap<>();
		retObj.put(IJMSProtocolKeys.COOKIE_KEY, getCookie());
		if (hasException()) {
			retObj.put(IJMSProtocolKeys.REPLY_EXCEPTION_KEY, getException());
		}
		if (getReplyMessage() != null && !getReplyMessage().isEmpty()) {
			retObj.put(IJMSProtocolKeys.REPLY_MESSAGE_KEY, getReplyMessage());
		}
		if (getOutputs() != null && !getOutputs().isEmpty()) {
			int size = getOutputs().size();
			retObj.put(IJMSProtocolKeys.DATA_FLOW_NR_KEY, size);
			for (int i = 0; i < size; i++) {
				retObj.put(IJMSProtocolKeys.DATA_FLOW_KEY + i, getOutputs().get(i));
			}
		}
		replyMessage.setObject(retObj);
		return replyMessage;
	}

	@Override
	public void decodeReplyMessage(final Message message) throws JMSException {
		status = ((ObjectMessage) message).getStringProperty(IJMSProtocolKeys.REPLY_STATUS_KEY);
		@SuppressWarnings("unchecked")
		HashMap<String, Object> retObj = (HashMap<String, Object>) ((ObjectMessage) message).getObject();
		setCookie((IChappyCookie) retObj.get(IJMSProtocolKeys.COOKIE_KEY));
		if (retObj.containsKey(IJMSProtocolKeys.REPLY_EXCEPTION_KEY)) {
			setException((Exception) retObj.get(IJMSProtocolKeys.REPLY_EXCEPTION_KEY));
		}
		if (retObj.containsKey(IJMSProtocolKeys.REPLY_MESSAGE_KEY)) {
			setReplyMessage((String) retObj.get(IJMSProtocolKeys.REPLY_MESSAGE_KEY));
		}
		if (retObj.containsKey(IJMSProtocolKeys.DATA_FLOW_NR_KEY)) {
			List<String> outputs = new ArrayList<>();
			for (int i = 0 ; i < (Integer) retObj.get(IJMSProtocolKeys.DATA_FLOW_NR_KEY); i++) {
				outputs.add((String) retObj.get(IJMSProtocolKeys.DATA_FLOW_KEY + i));
			}
			setOutputs(outputs);
		}
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
	 * @return MultiDataQueryHolder of the decoded queries.
	 */
	public MultiDataQueryHolder getMultidataQuery() {
		return queries;
	}

	/**
	 * created the decoded inbound message received by chappy.
	 * @param message from JMS
	 * @return this as a factory.
	 * @throws JMSException
	 */
	public static JMSTransformFlowMessage createDecodedInboundMessage(final Message message) throws JMSException {
		JMSTransformFlowMessage transformer = new JMSTransformFlowMessage();
		transformer.decodeInboundMessage(message);
		return transformer;
	}
	
	/**
	 * created the decoded reply message from chappy.
	 * @param message from JMS
	 * @return this as a factory.
	 * @throws JMSException
	 */
	public static IChappyProtocol createDecodedReplyMessage(final Message message) throws JMSException {
		JMSTransformFlowMessage transformer = new JMSTransformFlowMessage();
		transformer.decodeReplyMessage(message);
		return transformer;
	}
}
