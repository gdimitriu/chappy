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
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;

import chappy.interfaces.cookies.IChappyCookie;

/**
 * @author Gabriel Dimitriu
 *
 */
public class JMSLoginMessage {

	/** user name */
	private String userName = null;
	
	/** user password */
	private String password = null;
	
	/** cookie for the transaction */
	private IChappyCookie cookie = null;
	
	/**
	 * default constructor used for send.
	 */
	public JMSLoginMessage() {
		
	}
	/**
	 * @param userName of login user
	 * @param password of login user
	 */
	public JMSLoginMessage(final String userName, final String password) {
		this.userName = userName;
		this.password = password;
	}
	
	/**
	 * get the userName of the login user.
	 * @return userName of the login user.
	 */
	public String getUserName() {
		return this.userName;
	}
	
	/**
	 * get the password of the login user.
	 * @return password of the login user.
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * set the cookie that correspond to this login
	 * @param cookie that correspond to this login.
	 */
	public void setCookie(final IChappyCookie cookie) {
		this.cookie = cookie;
	}
	
	/**
	 * get the cookie for this login.
	 * @return cookie for this login.
	 */
	public IChappyCookie getCookie() {
		return this.cookie;
	}
	
	/**
	 * Create a decoded message from inbound.
	 * @param message received by JMS
	 * @return decoded message
	 * @throws JMSException
	 */
	public static JMSLoginMessage decodeInboundMessage(final Message message) throws JMSException {
		JMSLoginMessage msg = new JMSLoginMessage();
		msg.decodeInbound(message);
		return msg;
	}
	
	public static JMSLoginMessage decodeReplyMessage(final Message message) throws JMSException {
		JMSLoginMessage msg = new JMSLoginMessage();
		msg.decodeReply(message);
		return msg;
	}
	
	/**
	 * decode the reply message.
	 * @param message received from chappy
	 * @throws JMSException
	 */
	private void decodeReply(final Message message) throws JMSException {
		if (message instanceof ObjectMessage) {
			
		}
	}
	
	/**
	 * encode the response message to be send by chappy.
	 * @param session in which the message should be send
	 * @return message to be send.
	 * @throws JMSException 
	 */
	public Message encodeResponseMessage(final Session session) throws JMSException {
		ObjectMessage message = session.createObjectMessage();
		return message;
	}
	
	/**
	 * decode the inbound message.
	 * @param message received by chappy
	 */
	private void decodeInbound(final Message message) throws JMSException {
		if (message instanceof StreamMessage) {
			
		}
	}
	
	/**
	 * encode the inbound message to be send to chappy.
	 * @param session in which the message should be send
	 * @return message to be send.
	 * @throws JMSException 
	 */
	public Message encodeInboundMessage(final Session session) throws JMSException {
		StreamMessage message = session.createStreamMessage();
		return message;
	}
}
