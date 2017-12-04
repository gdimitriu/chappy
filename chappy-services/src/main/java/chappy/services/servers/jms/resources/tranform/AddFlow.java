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
import java.io.IOException;
import java.io.InputStream;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import chappy.clients.jms.protocol.JMSAddFlowMessage;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.exception.ForbiddenException;
import chappy.interfaces.jms.protocol.IJMSCommands;
import chappy.interfaces.jms.protocol.IJMSMessages;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.jms.resources.IJMSQueueNameConstants;
import chappy.interfaces.jms.resources.JMSAbstractProducerConsumer;
import chappy.interfaces.transactions.ITransaction;
import chappy.providers.transaction.TransactionProviders;
import chappy.services.servers.common.TransactionOperations;

/**
 * Add flow resources for JMS.
 * @author Gabriel Dimitriu
 *
 */
public class AddFlow extends JMSAbstractProducerConsumer {

	/**
	 * 
	 */
	public AddFlow() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.resources.IJMSRuntimeResource#getQueueName()
	 */
	@Override
	public String getQueueName() {
		return IJMSQueueNameConstants.ADD_FLOW;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.resources.IJMSRuntimeResource#processMessage(javax.jms.Session, javax.jms.Message)
	 */
	@Override
	public void processMessage(final Session session, final Message message) {
		String command = null;
		try {
			command = message.getStringProperty(IJMSCommands.COMMAND_PROPERTY);
		} catch (JMSException e) {
			e.printStackTrace();
			sendStandardError(session, message);
			return;
		}
		try {
			if (IJMSCommands.ADD_FLOW.equals(command)) {
				addFlow(session, message);
			} else {
				sendStandardError(session, message);
			}
		} catch (Exception e) {
			sendOnError(session, message, e, command);
		}
	}

	
	/** 
	 * add flow
	 * @param session the session in which the message was received
	 * @param message the request message
	 * @throws JMSException
	 * @throws ForbiddenException
	 * @throws Exception
	 * @throws IOException
	 */
	private void addFlow(final Session session, final Message message) throws JMSException, ForbiddenException, Exception, IOException {
		JMSAddFlowMessage transformer = JMSAddFlowMessage.createDecodedInboundMessage(message);
		if (transformer == null) {
			throw new ForbiddenException(IJMSMessages.FORBIDDEN);
		}
		IChappyCookie cookie = transformer.getCookie();
		if (cookie == null) {
			throw new ForbiddenException("wrong cookie ", IJMSMessages.FORBIDDEN);
		}
		ITransaction transaction = TransactionProviders.getInstance().getTransaction(cookie);
		if (transaction == null) {
			throw new ForbiddenException(IJMSMessages.FORBIDDEN);
		}
		
		InputStream configurationStream = null;
		if (transformer.getConfiguration() != null && !"".equals(transformer.getConfiguration())) {
			configurationStream = new ByteArrayInputStream(transformer.getConfiguration().getBytes());
		}
		
		TransactionOperations.addStaticFlowRunner(cookie, configurationStream, transformer.getFlowName(), transformer.getMultidataQuery());
		
		JMSAddFlowMessage replyMessage = new JMSAddFlowMessage();
		replyMessage.setCookie(cookie);
		replyMessage.setStatus(IJMSStatus.OK);
		replyMessage.setReplyMessage(IJMSMessages.OK);
		Message reply = replyMessage.encodeReplyMessage(session);
		
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
	
	/** Send the message in case of error.
	 * @param session is the session in which the error has arrived
	 * @param message is the message where the error arrise
	 * @param e is exception received
	 */
	private void sendOnError(final Session session, final Message message, final Exception e, final String command) {
		try {
			Message reply = null;
			if (IJMSCommands.ADD_FLOW.equals(command)) {
				reply = createAddFlowErrorReply(session, e);
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
	 * create the error from a the added transformer 
	 * @param session in which the transformer supposed to be created.
	 * @param e the exception
	 * @return the error message to be send.
	 * @throws JMSException
	 */
	private Message createAddFlowErrorReply(final Session session, final Exception e)  throws JMSException  {
		JMSAddFlowMessage transformerMessage = null;
		if (e instanceof ForbiddenException && ((ForbiddenException) e).getLocalizedMessage() != null) {
			transformerMessage = new JMSAddFlowMessage(((ForbiddenException) e).getLocalizedMessage());
		} else {
			transformerMessage = new JMSAddFlowMessage();
		}
		transformerMessage.setException(e);
		transformerMessage.setReplyMessage(e.getLocalizedMessage());
		return transformerMessage.encodeReplyMessage(session);
	}
}
