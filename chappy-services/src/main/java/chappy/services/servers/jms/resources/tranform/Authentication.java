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

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import chappy.clients.jms.protocol.JMSLoginMessage;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.exception.ForbiddenException;
import chappy.interfaces.jms.protocol.IJMSMessages;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.jms.resources.IJMSQueueNameConstants;
import chappy.interfaces.jms.resources.JMSAbstractProducerConsumer;
import chappy.services.servers.common.TransactionOperations;

/**
 * @author Gabriel Dimitriu
 *
 */
public class Authentication extends JMSAbstractProducerConsumer {

	/**
	 * 
	 */
	public Authentication() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.resources.IJMSRuntimeResource#getQueueName()
	 */
	@Override
	public String getQueueName() {
		return IJMSQueueNameConstants.AUTHENTICATION;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.resources.IJMSRuntimeResource#processMessage(javax.jms.Session, javax.jms.Message)
	 */
	@Override
	public void processMessage(final Session session, final Message message) {
		try {
			JMSLoginMessage loginMessage = JMSLoginMessage.decodeInboundMessage(message);
			JMSLoginMessage replyMessage = new JMSLoginMessage();
			IChappyCookie cookie = TransactionOperations.login(this.getClass(), loginMessage.getUserName(), loginMessage.getPassword(),
					loginMessage.isPersistence());
			if (cookie == null) {
				throw new ForbiddenException(IJMSMessages.FORBIDDEN);
			}
			cookie.setTransactionId(message.getJMSCorrelationID());
			replyMessage.setCookie(cookie);
			replyMessage.setStatus(IJMSStatus.OK);
			replyMessage.setReplyMessage(IJMSMessages.OK);
			Message reply = replyMessage.encodeResponseMessage(session);
			
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
		} catch (JMSException | ForbiddenException e) {
			JMSLoginMessage replyMessage = new JMSLoginMessage();
			replyMessage.setCookie(null);
			replyMessage.setException(e);
			replyMessage.setStatus(IJMSStatus.COMMUNICATION_SERVER_ERROR);
			replyMessage.setReplyMessage(IJMSMessages.COMMUNICATION_ERROR);
			try {
				Message reply = replyMessage.encodeResponseMessage(session);
				Destination replyTo = session.createQueue(IJMSQueueNameConstants.TRANSACTION_RETURN);
				MessageProducer producer = session.createProducer(replyTo);
				producer.setDeliveryMode(DeliveryMode.PERSISTENT);
				producer.send(reply);
				session.commit();
			} catch (Exception e1) {
				try {
					TextMessage msg = session.createTextMessage();
					Destination replyTo = session.createQueue(IJMSQueueNameConstants.TRANSACTION_RETURN);
					msg.setJMSCorrelationID(message.getJMSCorrelationID());
					msg.setText(IJMSMessages.INTERNAL_SERVER_ERROR);
					MessageProducer producer = session.createProducer(replyTo);
					producer.setDeliveryMode(DeliveryMode.PERSISTENT);
					producer.send(msg);
					session.commit();
				} catch (JMSException e2) {
					e2.printStackTrace();
				}
			}
		}

	}

}
