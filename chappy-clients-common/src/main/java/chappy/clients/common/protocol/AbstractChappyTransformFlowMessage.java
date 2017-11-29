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

import java.util.ArrayList;
import java.util.List;

import chappy.utils.streams.StreamUtils;

/**
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractChappyTransformFlowMessage extends AbstractChappyProtocolMessage {

	/** list of inputs data*/
	private List<String> inputs = null;
	
	/** list of outputs data*/
	private List<String> outputs = null;
	
	/** configuration string */
	private String configuration = null;
	
	/** flow name */
	private String flowName = null;
	
	/**
	 * default constructor
	 */
	public AbstractChappyTransformFlowMessage() {
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();
	}

	/**
	 * initialize using one input and one configuration
	 * @param input
	 * @param config
	 */
	public AbstractChappyTransformFlowMessage(final String input, final String config, final String flowName) {
		inputs = new ArrayList<>();
		inputs.add(input);
		configuration = config;
		this.setFlowName(flowName);
	}
	
	/**
	 * @return the inputs
	 */
	public List<String> getInputs() {
		return inputs;
	}

	/**
	 * @param inputs the inputs to set
	 */
	public void setInputs(final List<String> inputs) {
		this.inputs = inputs;
	}

	/**
	 * @return the outputs
	 */
	public List<String> getOutputs() {
		return outputs;
	}

	/**
	 * @param outputs the outputs to set
	 */
	public void setOutputs(final List<String> outputs) {
		this.outputs = outputs;
	}

	/**
	 * @return the configuration
	 */
	public String getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(final String configuration) {
		this.configuration = configuration;
	}

	
	/**
	 * add an input from the system resource.
	 * @param resource the path to the input data resource
	 */
	public void addInputFromResource(final String resource) {
		inputs.add(StreamUtils.getStringFromResource(resource));
	}
	
	/**
	 * add configuration from the system resource.
	 * @param resource the path the configuration data
	 */
	public void addConfigFromResource(final String resource) {
		configuration = StreamUtils.getStringFromResource(resource);
	}
	
	/**
	 * @param message to add as input
	 */
	public void addInputString(final String message) {
		inputs.add(message);
	}
	
	/**
	 * @return input string
	 */
	public String getInputString() {
		if (inputs != null && !inputs.isEmpty()) {
			return inputs.get(0);
		}
		return null;
	}
	
	/**
	 * set the flow configuration.
	 * @param config the flow configuration
	 */
	public void setStringConfiguration(final String config) {
		configuration = config;
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
	public void setFlowName(final String flowName) {
		this.flowName = flowName;
	}
}
