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
package chappy.interfaces.jms.resources;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.Session;

/**
 * @author Gabriel Dimitriu
 *
 */
public interface IJMSRuntimeResource {

	/**
	 * @return queue name associated with this consumer.
	 */
	public String getQueueName();	
	
	/**
	 * @return factory name as string
	 */
	public String getFactoryName();
	
	public Connection getCurrentConnection();

	public void setCurrentConnection(final Connection currentConnection);
	
	/**
	 * get the maximum number of instances allowed.
	 * @return nr of number of instances allowed (-1) for infinite
	 */
	default public int getMaxConcurentInstances() {
		return 10;
	}
	
	/**
	 * @param session the session in which the message was received
	 * @param message the received message
	 */
	public void processMessage(final Session session, final Message message);
}
