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

import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.jms.protocol.IJMSProtocol;
import chappy.interfaces.jms.protocol.IJMSStatus;

/**
 * @author Gabriel Dimitriu
 *
 */
public class JMSLogoutMessage implements IJMSProtocol {

	/** cookie for the transaction */
	private IChappyCookie cookie = null;
	
	/** reply message from chappy */
	private String replyMessage = "";
	
	/** exception in case of internal server */ 
	private Exception exception = null;
	
	/** status string */
	private String status = IJMSStatus.FORBIDDEN;
	
	/**
	 * default constructor. 
	 */
	public JMSLogoutMessage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the cookie
	 */
	public IChappyCookie getCookie() {
		return cookie;
	}

	/**
	 * @param cookie the cookie to set
	 */
	public void setCookie(final IChappyCookie cookie) {
		this.cookie = cookie;
	}

	/**
	 * @return the replyMessage
	 */
	public String getReplyMessage() {
		return replyMessage;
	}

	/**
	 * @param replyMessage the replyMessage to set
	 */
	public void setReplyMessage(final String replyMessage) {
		this.replyMessage = replyMessage;
	}

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(final Exception exception) {
		this.exception = exception;
	}

	/* (non-Javadoc)
	 * @see chappy.clients.jms.protocol.IJMSProtocol#encodeInboundMessage(javax.jms.Session)
	 */
	@Override
	public Message encodeInboundMessage(final Session session) throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see chappy.clients.jms.protocol.IJMSProtocol#decodeInbound(javax.jms.Message)
	 */
	@Override
	public void decodeInbound(final Message message) throws JMSException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see chappy.clients.jms.protocol.IJMSProtocol#encodeResponseMessage(javax.jms.Session)
	 */
	@Override
	public Message encodeResponseMessage(final Session session) throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see chappy.clients.jms.protocol.IJMSProtocol#decodeReply(javax.jms.Message)
	 */
	@Override
	public void decodeReply(final Message message) throws JMSException {
		// TODO Auto-generated method stub
		
	}
}
