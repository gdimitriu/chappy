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

import java.util.List;

import chappy.clients.common.protocol.AbstractChappyTransformFlowMessage;

/**
 * Chappy transform message using flow request wrapper, abstract implementation for all services.
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractChappyTransformFlow extends AbstractChappyClient {

	/**
	 * 
	 */
	public AbstractChappyTransformFlow() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.clients.common.AbstractChappyClient#getProtocol()
	 */
	@Override
	public AbstractChappyTransformFlowMessage getProtocol() {
		return (AbstractChappyTransformFlowMessage) super.getProtocol();
	}
	
	/**
	 * @param message to be added as input
	 */
	public void addStringInputMessage(final String message) {
		getProtocol().addInputString(message);
	}
	
	/**
	 * @param config the flow configuration string
	 */
	public void addStringConfiguration(final String config) {
		getProtocol().setStringConfiguration(config);
	}
	
	/**
	 * get the result of the transformation as list of strings.
	 * @return the list of output string.
	 */
	public List<String> getOutputResultAsString() {
		return getProtocol().getOutputs();
	}
	
	/**
	 * add a list of inputs.
	 * @param inputs list of inputs
	 */
	public void addListOfInputs(final List<String> inputs) {
		getProtocol().setInputs(inputs);
	}
}
