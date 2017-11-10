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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import chappy.clients.common.protocol.AbstractChappyTransformFlowMessage;
import chappy.interfaces.IChappyProtocol;
import chappy.interfaces.jms.protocol.IJMSProtocol;
import chappy.interfaces.jms.protocol.IJMSStatus;

/**
 * Chappy transform request protocol message implementation for JMS.
 * @author Gabriel Dimitriu
 *
 */
public class JMSTransformFlowMessage extends AbstractChappyTransformFlowMessage implements IJMSProtocol {

	/** status string */
	private String status = IJMSStatus.FORBIDDEN;
	
	/**
	 * 
	 */
	public JMSTransformFlowMessage(final String configuration) {
		// TODO Auto-generated constructor stub
	}

	public JMSTransformFlowMessage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Message encodeInboundMessage(final Session session) throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void decodeInboundMessage(final Message message) throws JMSException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Message encodeReplyMessage(final Session session) throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void decodeReplyMessage(final Message message) throws JMSException {
		// TODO Auto-generated method stub
		
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

}
