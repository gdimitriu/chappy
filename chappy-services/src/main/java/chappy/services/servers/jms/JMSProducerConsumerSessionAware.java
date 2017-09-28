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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;

import chappy.interfaces.jms.resources.IJMSRuntimeResource;

/**
 * Just a session aware (it could reply) for a JMS consumer/producer.
 * @author Gabriel Dimitriu
 *
 */
public class JMSProducerConsumerSessionAware implements MessageListener, Runnable{

	/** the resource on which will consumer the message. */
	private IJMSRuntimeResource resource = null;
	
	/** the session which receive the message. */
	private Session currentSession = null;
	
	/** the queue on which we had received the message. */
	private Queue queue = null;
	
	/**
	 * constructor for consumer/producer.
	 * @param session
	 * @param res
	 * @param queue
	 */
	public JMSProducerConsumerSessionAware(final Session session, final IJMSRuntimeResource res, final Queue queue) {
		currentSession = session;
		resource = res;
		this.queue  = queue;
	}
	
	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(final Message message) {
		resource.processMessage(currentSession, message);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {		
		try {
			MessageConsumer consumer = currentSession.createConsumer(queue);
			consumer.setMessageListener(this);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}

}
