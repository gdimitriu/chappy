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
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import de.odysseus.staxon.json.JsonXMLInputFactory;

/**
 * This extends StaxonStep and implements the overriden methods to have the json2xml feature.
 * @author Gabriel Dimitriu
 *
 */
public class Json2XmlStep extends StaxonStep {
	
	/** xml output factory because is a jxon2xml */
	private XMLOutputFactory outputFactory = null;
	/**json input factory because is a json2xml */
	private JsonXMLInputFactory inputFactory = null;
	/**
	 * 
	 */
	public Json2XmlStep() {
	}
	
	@Override
	public void configure() {
		super.configure();
		inputFactory = new JsonXMLInputFactory(config);
		outputFactory = XMLOutputFactory.newInstance();
	}
	
	@Override
	protected XMLEventReader createEventReader(final InputStream input) throws XMLStreamException {
		return inputFactory.createXMLEventReader(input);
	}
	
	@Override
	protected XMLEventWriter createEventWriter(final OutputStream output) throws XMLStreamException {
		return outputFactory.createXMLEventWriter(output);
	}
	
	@Override
	protected boolean specialProcessing(final XMLEventReader eventReader,
			final XMLEventWriter eventWriter) {
		//TODO: for arrays in output ? should be something in staxon or a configuration
		return false;
	}
}
