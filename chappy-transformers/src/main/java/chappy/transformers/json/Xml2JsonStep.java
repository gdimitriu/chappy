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
package chappy.transformers.json;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import de.odysseus.staxon.json.JsonXMLOutputFactory;

/**
* This extends StaxonStep and implements the overriden methods to have the xml2json feature.
* @author Gabriel Dimitriu
*
*/
public class Xml2JsonStep extends StaxonStep {
	
	/** json output factory because is xml2json */
	private JsonXMLOutputFactory outputFactory = null;
	
	/** xml input factory because is xml2json */
	private XMLInputFactory inputFactory = null;

	/**
	 * 
	 */
	public Xml2JsonStep() {
		
	}
	
	@Override
	public void configure() {
		super.configure();
		inputFactory = XMLInputFactory.newFactory();
		outputFactory = new JsonXMLOutputFactory(config);
	}
	
	@Override
	protected XMLEventReader createEventReader(final InputStream input) throws XMLStreamException {
		return inputFactory.createXMLEventReader(input);
	}
	
	@Override
	protected XMLEventWriter createEventWriter(final OutputStream output) throws XMLStreamException {
		return outputFactory.createXMLEventWriter(output);
	}
	
}
