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
package chappy.clients.rest;

import javax.ws.rs.core.MediaType;

import chappy.interfaces.transactions.IClientTransaction;

/**
 * Chappy run an existing flow request client for REST.
 * @author Gabriel Dimitriu
 *
 */
public class ChappyRESTRunExistingFlow extends ChappyRESTTransformFlow {

	/**
	 * @param input string for the transformation
	 * @param flowNmae the name of the flow
	 * @param client the chappy client transaction
	 */
	public ChappyRESTRunExistingFlow(final String input, final String flowName, final IClientTransaction client) {
		super(input, null, null, flowName,  client);
	}

	/**
	 * @param input string for the transformation
	 * @param type the type of the request
	 * @param flowName the name of the flow
	 * @param client the chappy client transaction
	 */
	public ChappyRESTRunExistingFlow(final String input, final MediaType type, final String flowName, final IClientTransaction client) {
		super(input, type, null, flowName, client);
	}

	/**
	 * @param client the chappy client transaction
	 */
	public ChappyRESTRunExistingFlow(IClientTransaction client) {
		super(client);
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.rest.IRESTClient#send()
	 */
	@Override
	public ChappyRESTRunExistingFlow send() {
		return (ChappyRESTRunExistingFlow) super.send();
	}
}
