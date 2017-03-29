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
package chappy.flows.transformers.dynamicflows.test;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.junit.Test;

import chappy.configurations.transformers.ConfigurationProperties;
import chappy.configurations.transformers.StaxonConfiguration;

/**
 * This hold the tests for JAXB for staxon configuration classes.
 * @author Gabriel Dimitriu
 *
 */
public class StaxonConfigurationTest {

	@Test
	public void StaxonConfigurationUnmarshallerTest() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(StaxonConfiguration.class);
		StaxonConfiguration configuration = (StaxonConfiguration) context.createUnmarshaller()
				.unmarshal(getClass().getClassLoader().getResourceAsStream("StaxonConfiguration.xml"));
		assertEquals("autoArray", configuration.getAutoArray(), true);
		assertEquals("autoPrimitive", configuration.getAutoPrimitive(), true);
		assertEquals("VirtualNode", configuration.getVirtualNode(), "VirtualNode");
	}

	@Test
	public void StaxonConfigurationWithPropertiesUnmarshallerTest() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(StaxonConfiguration.class);
		StaxonConfiguration configuration = (StaxonConfiguration) context.createUnmarshaller()
				.unmarshal(getClass().getClassLoader().getResourceAsStream("StaxonConfigurationWithProperties.xml"));
		assertEquals("autoArray", configuration.getAutoArray(), true);
		assertEquals("autoPrimitive", configuration.getAutoPrimitive(), true);
		assertEquals("VirtualNode", configuration.getVirtualNode(), "VirtualNode");
		ConfigurationProperties[] inputProperties = configuration.getInputProperties();
		assertEquals("size of input properties should be 2", 2, inputProperties.length);
		assertEquals("firstInputProperty", "firstIP", inputProperties[0].getProperty());
		assertEquals("firstInputValue", "firstIV", inputProperties[0].getValue());
		assertEquals("secondInputProperty", "secondIP", inputProperties[1].getProperty());
		assertEquals("secondInputValue", "secondIV", inputProperties[1].getValue());
		ConfigurationProperties[] outputProperties = configuration.getOutputProperties();
		assertEquals("size of output properties should be 2", 2, outputProperties.length);
		assertEquals("firstOutputProperty", "firstOP", outputProperties[0].getProperty());
		assertEquals("firstOutputValue", "firstOV", outputProperties[0].getValue());
		assertEquals("secondOutputProperty", "secondOP", outputProperties[1].getProperty());
		assertEquals("secondOutputValue", "secondOV", outputProperties[1].getValue());
	}
}
