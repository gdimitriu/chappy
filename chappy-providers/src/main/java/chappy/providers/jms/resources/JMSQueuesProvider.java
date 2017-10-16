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
package chappy.providers.jms.resources;

import java.util.ArrayList;
import java.util.List;

import chappy.interfaces.jms.resources.IJMSQueueNameConstants;

/**
 * Singleton queue provider.
 * @author Gabriel Dimitriu
 *
 */
public class JMSQueuesProvider {

	/** singleton provider */
	private static JMSQueuesProvider singleton = new JMSQueuesProvider();
	/** system queues */
	private List<String> systemQueues = new ArrayList<String>();
	/**
	 * add the system queues.
	 */
	private JMSQueuesProvider() {		
		systemQueues.add(IJMSQueueNameConstants.TRANSACTION);
		systemQueues.add(IJMSQueueNameConstants.ADD);
		systemQueues.add(IJMSQueueNameConstants.TRANSFORM);
		systemQueues.add(IJMSQueueNameConstants.AUTHENTICATION);
		systemQueues.add(IJMSQueueNameConstants.TRANSACTION_RETURN);
	}

	
	/**
	 * get all the queue from the system.
	 * @return all queues.
	 */
	public List<String> getAllQueues() {
		return systemQueues;
	}
	
	
	/**
	 * get the singleton instance.
	 * @return singleton instance.
	 */
	public static JMSQueuesProvider getInstance() {
		return singleton;
	}
}
