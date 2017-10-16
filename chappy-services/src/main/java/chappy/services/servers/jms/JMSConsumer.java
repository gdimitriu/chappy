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
package chappy.services.servers.jms;

import javax.jms.Message;
import javax.jms.MessageListener;

import chappy.interfaces.jms.resources.IJMSRuntimeResource;

/**
 * just a wrapper for a simple JMS Consumer. 
 * @author Gabriel Dimitriu
 *
 */
public class JMSConsumer implements MessageListener {

	/** the resource on which will consumer the message */ 
	private IJMSRuntimeResource resource = null;
	/**
	 * @param resource 
	 * 
	 */
	public JMSConsumer(final IJMSRuntimeResource resource) {
		this.resource = resource;
	}

	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message message) {
		resource.processMessage(null, message);
	}

}
