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
package chappy.services.servers.jms.resources;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import chappy.interfaces.jms.resources.JMSAbstractProducerConsumer;
import chappy.interfaces.jms.protocol.IJMSCommands;
import chappy.interfaces.jms.resources.IJMSQueueNameConstants;
import chappy.providers.jms.resources.JMSRouteProvider;

/**
 * Router for input messages to the correct internal queues.
 * @author Gabriel Dimitriu
 *
 */
public class TransactionRouter extends JMSAbstractProducerConsumer {

	/**
	 * default constructor
	 */
	public TransactionRouter() {
		//nothing to do yet
	}

	/* (non-Javadoc)
	 * @see servers.jms.IResourceProducerConsumer#getQueueName()
	 */
	@Override
	public String getQueueName() {
		return IJMSQueueNameConstants.TRANSACTION;
	}

	/* (non-Javadoc)
	 * @see servers.jms.IJMSRuntimeResource#processMessage(javax.jms.Session, javax.jms.Message)
	 */
	@Override
	public void processMessage(final Session session, final Message message) {
		
		Destination replyTo = null;
		try {
			replyTo = message.getJMSReplyTo();
		} catch (JMSException e2) {
			e2.printStackTrace();
		}
		try {
			message.setJMSCorrelationID(message.getJMSMessageID());
			// read the command
			String command = message.getStringProperty(IJMSCommands.COMMAND_PROPERTY);
			if (IJMSCommands.getAllCommands().contains(command)) {
				String route = JMSRouteProvider.getInstance().getRouteQueueName(command);
				routeMessage(session, route, message);				
			}
		} catch (JMSException e) {
			e.printStackTrace();
			try {
				returnErrorMessage(session, replyTo, e, message);
			} catch (JMSException e1) {
				e1.printStackTrace();
			}
			return;
		}
	}

	/**
	 * This will return the error to the caller queue
	 * @param session in which it has received the message.
	 * @param replyTo the queue in which it has to reply.
	 * @param exception the exception which has to be send the the requestor.
	 * @param message message which had been received
	 * @throws JMSException
	 */
	private void returnErrorMessage(final Session session, final Destination replyTo,
			final JMSException exception, final Message message) 
					throws JMSException{
		
		ObjectMessage retMsg = session.createObjectMessage();
		retMsg.setObject(exception);
		retMsg.setJMSCorrelationID(message.getJMSMessageID());
		session.createProducer(replyTo).send(retMsg);
	}

	/**
	 * Route the message to the correct internal queue.
	 * @param session in which the message was received.
	 * @param route the name of the queue
	 * @param strMsg the message
	 * @throws JMSException
	 */
	private void routeMessage(final Session session, final String route, final Message strMsg) 
			throws JMSException {
		try {
			try {
				Destination destination = session.createQueue(route);
				MessageProducer producer = session.createProducer(destination);
				producer.setDeliveryMode(DeliveryMode.PERSISTENT);
				producer.send(strMsg);
				session.commit();
			} catch (JMSException e) {
				session.rollback();
			}
		} catch (JMSException e1) {
			session.commit();
		}
	}

}
