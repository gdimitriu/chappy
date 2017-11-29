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
package chappy.clients.common;

import chappy.clients.common.protocol.AbstractChappyAddFlowMessage;

/**
 * Chappy add flow request wrapper, abstract implementation for all services.
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractChappyAddFlow extends AbstractChappyClient {

	/**
	 * 
	 */
	public AbstractChappyAddFlow() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the protocol
	 */
	@Override
	public AbstractChappyAddFlowMessage getProtocol() {
		return (AbstractChappyAddFlowMessage) super.getProtocol();
	}
	
	/**
	 * @param flowName the name of the flow
	 * @param flowConfiguration the configuration of the flow
	 */
	public void setFlow(final String flowName, final String flowConfiguration) {
		getProtocol().setFlow(flowName, flowConfiguration);
	}

}
