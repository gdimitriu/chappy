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
package chappy.clients.common.protocol;

/**
 * Chappy add flow request protocol message abstract implementation for all services.
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractChappyAddFlowMessage extends AbstractChappyProtocolMessage {

	
	/** flow definition XML */
	private String flowDefinition = null;
	
	/** flow name */
	private String flowName = null;
	/**
	 * 
	 */
	public AbstractChappyAddFlowMessage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param flowName
	 * @param flowDefiniton
	 */
	public AbstractChappyAddFlowMessage(final String flowName, final String flowDefiniton) {
		this.flowDefinition = flowDefiniton;
		this.flowName = flowName;
	}

	/**
	 * @return the flowDefinition
	 */
	public String getFlowDefinition() {
		return flowDefinition;
	}

	/**
	 * @param flowDefinition the flowDefinition to set
	 */
	public void setFlowDefinition(String flowDefinition) {
		this.flowDefinition = flowDefinition;
	}

	/**
	 * @return the flowName
	 */
	public String getFlowName() {
		return flowName;
	}

	/**
	 * @param flowName the flowName to set
	 */
	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}
	
	/**
	 * @param name of the flow
	 * @param configuration of the flow
	 */
	public void setFlow(final String name, final String configuration) {
		this.flowDefinition = configuration;
		this.flowName = name;
	}
}
