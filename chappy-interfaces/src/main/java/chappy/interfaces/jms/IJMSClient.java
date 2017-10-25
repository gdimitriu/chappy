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

/**
 * @author Gabriel Dimitriu
 *
 */
public interface IJMSClient extends MessageListener {

	/**
	 * send the data to Chappy.
	 * @throws JMSException
	 */
	public void send() throws JMSException;
	
	/**
	 * close all connections.
	 * This should be called only once.
	 * @return message confirmation.
	 */
	public String closeAll();
	
	/**
	 * This contains everithing neeed to continue communication to chappy.
	 * @return Transaction Holder for JMS
	 */
	public IJMSTransactionHolder createTransactionHolder();
}
