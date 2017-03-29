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
package chappy.interfaces.providers;

import java.io.InputStream;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBException;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.xml.sax.SAXException;

import chappy.interfaces.flows.IFlowRunner;

/**
 * 
 * @author Gabriel Dimitriu
 *
 */
public interface IFlowRunnerProvider {

	/**
	 * This will create the flow configuration.
	 * The flow configuration will be created using JAXB.
	 * @param configurationStream
	 * @param multipart
	 * @param queryParams 
	 * @throws JAXBException 
	 * @throws SAXException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	IFlowRunner createFlowRunner(String name, InputStream configurationStream, FormDataMultiPart multipart,
			MultivaluedMap<String, String> queryParams) throws JAXBException, SAXException, InstantiationException, IllegalAccessException, ClassNotFoundException;
	
	/**
	 * This will create the flow configuration.
	 * @param name flow name.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	IFlowRunner createFlowRunner(final String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException;

}