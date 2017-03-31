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
package chappy.interfaces.flows;

import java.io.InputStream;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBException;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.xml.sax.SAXException;

import chappy.utils.streams.wrappers.StreamHolder;

/**
 * @author Gabriel Dimitriu
 *
 */
public interface IFlowRunner {
	/**
	 * This will create the steps from the configuration.
	 * @throws Exception 
	 */
	public void createSteps() throws  Exception;
	
	/**
	 * This will create the steps from the configuration for a specific user.
	 * @throws Exception 
	 */
	public void createSteps(final String userName) throws  Exception;

	/**
	 * execute the flow constructed using the configuration.
	 * @param holder input/output holder
	 * @return 
	 * @throws Exception
	 */
	public StreamHolder executeSteps(final StreamHolder holder) throws Exception;
	
	/**
	 * configure the flow.
	 * This should be call the simple flow.
	 * @param mode 
	 * @param configuration
	 */
	public void configure(final String mode, final String configuration);

	/**
	 * setConfigurations, this will create the flow configuration.
	 * The flow configuration will be created using JAXB.
	 * @param configurationStream
	 * @param multipart
	 * @param queryParams 
	 * @throws JAXBException 
	 * @throws SAXException 
	 */
	public void setConfigurations(final InputStream configurationStream,
			final FormDataMultiPart multipart,
			final MultivaluedMap<String, String> queryParams) throws JAXBException, SAXException;
}
