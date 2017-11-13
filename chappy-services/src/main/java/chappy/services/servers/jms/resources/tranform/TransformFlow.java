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
package chappy.services.servers.jms.resources.tranform;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import chappy.clients.jms.protocol.JMSTransformFlowMessage;
import chappy.interfaces.flows.IFlowRunner;
import chappy.interfaces.jms.protocol.IJMSCommands;
import chappy.interfaces.jms.resources.IJMSQueueNameConstants;
import chappy.interfaces.jms.resources.JMSAbstractProducerConsumer;
import chappy.interfaces.services.IChappyServiceNamesConstants;
import chappy.providers.flow.runners.TransformersFlowRunnerProvider;
import chappy.utils.streams.StreamUtils;
import chappy.utils.streams.wrappers.ByteArrayInputStreamWrapper;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * JMS tranformer flow request.
 * @author Gabriel Dimitriu
 *
 */
public class TransformFlow  extends JMSAbstractProducerConsumer {

	/**
	 * 
	 */
	public TransformFlow() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.resources.IJMSRuntimeResource#getQueueName()
	 */
	@Override
	public String getQueueName() {
		return IJMSQueueNameConstants.TRANSFORM;
	}

	@Override
	public void processMessage(Session session, Message message) {
		String command = null;
		try {
			command = message.getStringProperty(IJMSCommands.COMMAND_PROPERTY);
		} catch (JMSException e) {
			e.printStackTrace();
			sendStandardError(session, message);
			return;
		}
		try {
			if (IJMSCommands.FLOW.equals(command)) {
				transformFlow(session, message);
			} else {
				sendStandardError(session, message);
			}
		} catch (Exception e) {
			sendOnError(session, message, e, command);
		}
	}
	
	/** Send the message in case of error.
	 * @param session is the session in which the error has arrived
	 * @param message is the message where the error arrise
	 * @param e is exception received
	 */
	private void sendOnError(final Session session, final Message message, final Exception e, final String command) {
		try {
			Message reply = null;
			if (IJMSCommands.FLOW.equals(command)) {
				reply = createFlowTransformerErrorReply(session, e);
			}
			Destination replyTo = session.createQueue(IJMSQueueNameConstants.TRANSACTION_RETURN);
			MessageProducer producer = session.createProducer(replyTo);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			producer.send(reply);
			session.commit();
		} catch (Exception e1) {
			sendStandardError(session, message);
		}
	}


	/**
	 * transform the flow and send the reply.
	 * @param session in which the message was received.
	 * @param message the received message.
	 * @throws Exception
	 */
	private void transformFlow(final Session session, final Message message) throws Exception {
		JMSTransformFlowMessage transformer = JMSTransformFlowMessage.createDecodedInboundMessage(message);
		InputStream configurationStream = null;
		if (transformer.getConfiguration() == null || "".equals(transformer.getConfiguration())) {
			configurationStream = new ByteArrayInputStream(transformer.getConfiguration().getBytes());
		}
		/* create the list of input stream holders */
		List<StreamHolder> holders = new ArrayList<StreamHolder>();
		for (String input : transformer.getInputs()) {
			byte[] buffer = input.getBytes();
			holders.add(new StreamHolder(new ByteArrayInputStreamWrapper(buffer, 0, buffer.length)));
		}
		
		
		IFlowRunner runner = TransformersFlowRunnerProvider.getInstance()
				.createFlowRunner(IChappyServiceNamesConstants.STATIC_FLOW, configurationStream, transformer.getMultidataQuery());
		runner.createSteps(transformer.getCookie());
		runner.executeSteps(holders);
		/* create the output */
		List<String> retList = new ArrayList<String>();
		for (StreamHolder holder : holders) {
			retList.add(StreamUtils.toStringFromStream(holder.getInputStream()));
		}		
		transformer.setOutputs(retList);
		Message reply = transformer.encodeReplyMessage(session);
		
		Destination replyTo = null;
		if (message.getJMSReplyTo() != null) {
			replyTo = message.getJMSReplyTo();
		} else {
			replyTo = session.createQueue(IJMSQueueNameConstants.TRANSACTION_RETURN);
		}
		MessageProducer producer = session.createProducer(replyTo);
		producer.setDeliveryMode(DeliveryMode.PERSISTENT);
		producer.send(reply);
		session.commit();
	}
	
	
	private Message createFlowTransformerErrorReply(final Session session, final Exception e)  throws JMSException  {
		// TODO Auto-generated method stub
		return null;
	}
}
