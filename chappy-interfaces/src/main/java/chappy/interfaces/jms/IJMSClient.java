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
package chappy.interfaces.jms;

import javax.jms.JMSException;
import javax.jms.MessageListener;

import chappy.interfaces.jms.protocol.IJMSStatus;
import chappy.interfaces.services.IChappyClient;

/**
 * @author Gabriel Dimitriu
 *
 */
public interface IJMSClient extends MessageListener, IChappyClient {

	/**
	 * send the data to Chappy.
	 * @throws JMSException
	 * return the client.
	 */
	public Object send() throws Exception;
	
	
	/**
	 * This contains everything needed to continue communication to chappy.
	 * @return Transaction Holder for JMS
	 */
	public IJMSTransactionHolder createTransactionHolder() throws Exception;
	
	
	/**
	 * Blocking receive with pool in thread.
	 * @param pollingTime the time in seconds between two poll.
	 * @return ChappyJMSInstance.
	 * @throws Exception
	 */
	public default Object receivePoll(final long pollTime) throws Exception {
		while(getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(pollTime);
		return this;
	}
}
