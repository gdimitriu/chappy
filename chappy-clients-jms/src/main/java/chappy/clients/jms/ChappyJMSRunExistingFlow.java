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
package chappy.clients.jms;

import javax.jms.JMSException;

import chappy.interfaces.transactions.IClientTransaction;

/**
 * Chappy run an existing flow request client for JMS.
 * @author Gabriel Dimitriu
 */
public class ChappyJMSRunExistingFlow extends ChappyJMSTransformFlow {

	/**
	 * @param input
	 * @param configuration
	 * @param client
	 */
	public ChappyJMSRunExistingFlow(final String input, final String flowName, final IClientTransaction client) {
		super(input, client, flowName);
	}

	/**
	 * @param client
	 */
	public ChappyJMSRunExistingFlow(final IClientTransaction client) {
		super(client);
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.rest.IRESTClient#send()
	 */
	@Override
	public ChappyJMSRunExistingFlow send() throws JMSException {
		return (ChappyJMSRunExistingFlow) super.send();
	}

}
