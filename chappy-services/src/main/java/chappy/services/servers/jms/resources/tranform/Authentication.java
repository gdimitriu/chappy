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
import chappy.clients.jms.protocol.JMSLoginMessage;
import chappy.clients.jms.protocol.JMSLogoutMessage;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.exception.ForbiddenException;
import chappy.interfaces.jms.protocol.IJMSCommands;
import chappy.interfaces.jms.protocol.IJMSMessages;
import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.jms.resources.IJMSQueueNameConstants;
import chappy.interfaces.jms.resources.JMSAbstractProducerConsumer;
import chappy.interfaces.transactions.ITransaction;
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
		String command = null;
		try {
			command = message.getStringProperty(IJMSCommands.COMMAND_PROPERTY);
		} catch (JMSException e) {
			e.printStackTrace();
			sendStandardError(session, message);
			return;
		}
		try {
			if (IJMSCommands.LOGIN.equals(command)) {
				loginReceived(session, message);
			} else if (IJMSCommands.LOGOUT.equals(command)) {
				logoutReceived(session, message);
			} else {
				sendStandardError(session, message);
			}
		} catch (JMSException | ForbiddenException e) {
			sendOnError(session, message, e, command);
		}

	}

	/**
	 * Chappy received a logout message.
	 * @param session in which the message has been received
	 * @param message the message which has been received.
	 * @throws JMSException
	 * @throws ForbiddenException
	 */
	private void logoutReceived(final Session session, final Message message) throws JMSException, ForbiddenException {
		JMSLogoutMessage logoutMessage = JMSLogoutMessage.createDecodedInboundMessage(message);
		if (logoutMessage == null) {
			throw new ForbiddenException(IJMSMessages.FORBIDDEN);
		}
		IChappyCookie cookie = logoutMessage.getCookie();
		if (cookie == null) {
			throw new ForbiddenException(IJMSMessages.FORBIDDEN);
		}
		ITransaction transaction = TransactionOperations.logout(cookie);
		if (transaction == null) {
			throw new ForbiddenException(IJMSMessages.FORBIDDEN);
		}
		JMSLogoutMessage replyMessage = new JMSLogoutMessage();
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
	
	/**
	 * Chappy received a login message.
	 * @param session in which the message has been received
	 * @param message the message which has been received.
	 * @throws JMSException
	 * @throws ForbiddenException
	 */
	private void loginReceived(final Session session, final Message message) throws JMSException, ForbiddenException {
		JMSLoginMessage loginMessage = JMSLoginMessage.createDecodedInboundMessage(message);
		JMSLoginMessage replyMessage = new JMSLoginMessage();
		IChappyCookie cookie = TransactionOperations.login(this.getClass(), loginMessage.getUserName(), loginMessage.getPassword(),
				loginMessage.isPersistence(), message.getJMSCorrelationID());
		if (cookie == null) {
			throw new ForbiddenException(IJMSMessages.FORBIDDEN);
		}
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

	/**
	 * Send the message in case of error.
	 * @param session is the session in which the error has arrived
	 * @param message is the message where the error arrise
	 * @param e is exception received
	 */
	private void sendOnError(final Session session, final Message message, final Exception e, final String command) {
		try {
			Message reply = null;
			if (IJMSCommands.LOGIN.equals(command)) {
				reply = createLoginErrorReply(session, e);
			} else if (IJMSCommands.LOGOUT.equals(command)) {
				reply = createLogoutErrorReply(session, e);
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
	 * @param session
	 * @param e
	 * @return message to be send
	 * @throws JMSException
	 */
	private Message createLoginErrorReply(final Session session, final Exception e) throws JMSException {
		JMSLoginMessage replyMessage = new JMSLoginMessage();
		replyMessage.setCookie(null);
		replyMessage.setException(e);
		replyMessage.setStatus(IJMSStatus.COMMUNICATION_SERVER_ERROR);
		replyMessage.setReplyMessage(IJMSMessages.COMMUNICATION_ERROR);
		Message reply = replyMessage.encodeReplyMessage(session);
		return reply;
	}
	
	/**
	 * @param session
	 * @param e
	 * @return message to be send
	 * @throws JMSException
	 */
	private Message createLogoutErrorReply(final Session session, final Exception e) throws JMSException {
		JMSLogoutMessage replyMessage = new JMSLogoutMessage();
		replyMessage.setCookie(null);
		replyMessage.setException(e);
		replyMessage.setStatus(IJMSStatus.COMMUNICATION_SERVER_ERROR);
		replyMessage.setReplyMessage(IJMSMessages.COMMUNICATION_ERROR);
		Message reply = replyMessage.encodeReplyMessage(session);
		return reply;
	}

}
