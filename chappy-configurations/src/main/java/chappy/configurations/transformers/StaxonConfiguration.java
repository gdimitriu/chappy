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
 
package chappy.configurations.transformers;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This hold the possible configurations or the staxon engine.
 * This is annotate using jaxb.
 * @author Gabriel Dimitriu
 *
 */
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class StaxonConfiguration {

	/** if the message has array on root */
	@XmlElement(name = "autoArray")
	private boolean autoArray = false;
	
	/** if it has multiple elements at root the this will be the virtual node */
	@XmlElement(name = "virtualNode")
	private String virtualNode = "";
	
	
	/** interpret the primitive based on content of the string */
	@XmlElement(name = "autoPrimitive")
	private boolean autoPrimitive = false;
	
	/** input properties for the engines */
	@XmlElement(name = "inputProperty")
	private ConfigurationProperties[] inputProperties = null;
	
	/** output properties for the engines */
	@XmlElement(name = "outputProperty")
	private ConfigurationProperties[] outputProperties = null;
	
	/**
	 * 
	 */
	public StaxonConfiguration() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * isArray return true if the message has array at root level
	 * @return true if array should be inserted.
	 */
	public boolean getAutoArray() {
		return autoArray;
	}

	/**
	 * return the virtual node name if is needed to be inserted.
	 * @return virtual node name
	 */
	public String getVirtualNode() {
		return virtualNode;
	}

	/**
	 * return true if the client request autoprimitives.
	 * @return true if auto is enabled
	 */
	public boolean getAutoPrimitive() {
		return autoPrimitive;
	}

	/**
	 * get the input configuration properties 
	 * @return
	 */
	public ConfigurationProperties[] getInputProperties() {
		return inputProperties;
	}
	
	/**
	 * get the output configuration properties.
	 * @return
	 */
	public ConfigurationProperties[] getOutputProperties() {
		return outputProperties;
	}
	
	/**
	 * @param array the array to set
	 */
	public void setAutoArray(final String array) {
		this.autoArray = "true".equals(array);
	}

	/**
	 * @param virtualNode the virtualNode to set
	 */
	public void setVirtualNode(final String virtualNode) {
		this.virtualNode = virtualNode;
	}

	/**
	 * @param autoPrimitive the autoPrimitive to set
	 */
	public void setAutoPrimitive(final String autoPrimitive) {
		this.autoPrimitive = "true".equals(autoPrimitive);
	}

	/**
	 * @param inputProperties the inputProperties to set
	 */
	public void setInputProperties(final ConfigurationProperties[] inputProperties) {
		this.inputProperties = inputProperties;
	}

	/**
	 * @param outputProperties the outputProperties to set
	 */
	public void setOutputProperties(final ConfigurationProperties[] outputProperties) {
		this.outputProperties = outputProperties;
	}

}
