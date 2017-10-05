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
package chappy.interfaces.jms.protocol;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Interface for the JMS chappy protocol.
 * @author Gabriel Dimitriu
 *
 */
public interface IJMSProtocol {

	/**
	 * encode the inbound message to be send to chappy.
	 * @param session in which the message should be send
	 * @return message to be send.
	 * @throws JMSException 
	 */
	public Message encodeInboundMessage(final Session session) throws JMSException;
	
	/**
	 * decode the inbound message.
	 * @param message received by chappy
	 */
	public void decodeInbound(final Message message) throws JMSException;
	
	/**
	 * encode the response message to be send by chappy.
	 * @param session in which the message should be send
	 * @return message to be send.
	 * @throws JMSException 
	 */
	public Message encodeResponseMessage(final Session session) throws JMSException;
	
	/**
	 * decode the reply message.
	 * @param message received from chappy
	 * @throws JMSException
	 */
	public void decodeReply(final Message message) throws JMSException;
}
