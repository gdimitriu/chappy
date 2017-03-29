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
package chappy.mappings.xslt;

import java.io.InputStream;
import java.util.Iterator;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.xml.sax.InputSource;

import chappy.interfaces.transformers.AbstractStep;
import chappy.utils.streams.wrappers.ByteArrayInputStreamWrapper;
import chappy.utils.streams.wrappers.ByteArrayOutputStreamWrapper;
import chappy.utils.streams.wrappers.StreamHolder;


/**
 * This hold the xslt transformation step. 
 * @author Gabriel Dimitriu
 *
 */
public class XslStep extends AbstractStep {
	
	/** this hold the mapping name */
	private String mappingName = null;
	
	/** this hold the mapping rule stream */
	private InputStream mappingStream = null;
	
	/** this hold the factory for the mapping engine */
	private String factoryEngine = null;

	/**
	 * 
	 */
	public XslStep() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * set the mapping stream which hold the xsl or translet.
	 * @param mappingStream wrapper
	 */
	public void setMappingStream(final ByteArrayInputStreamWrapper mapping) {
		mappingStream = mapping;
	}
	
	/**
	 * set the factory name for the transformation engine
	 * @param factory engine name
	 */
	public void setFactoryEngine(final String engine) {
		factoryEngine = engine;
	}
	
	/**
	 * set the mapping name.
	 * @param name of the mapping
	 */
	public void setMappingName(final String name) {
		mappingName = name;
	}
	
	public void setMappingStream(final FormDataMultiPart multipart) throws Exception {
		FormDataBodyPart part = multipart.getField(mappingName);
		if (part == null) {
			throw new Exception("mapping rule " + mappingName + " is not present in formData request");
		}
		mappingStream = part.getValueAs(InputStream.class);
		if (mappingStream == null) {
			throw new Exception("mapping rule " + mappingName + " is not present in formData request");
		}
	}

	/* (non-Javadoc)
	 * @see transformationsEngine.digester.steps.AbstractStep#execute(transformationsEngine.wrappers.StreamHolder)
	 */
	@Override
	public void execute(StreamHolder holder,
			final FormDataMultiPart multipart,
			final MultivaluedMap<String, String> queryParams)
					throws Exception {
		setMappingStream(multipart);
		Source messageSource = new SAXSource(new InputSource(holder.getInputStream()));
		ByteArrayOutputStreamWrapper bos = new ByteArrayOutputStreamWrapper();
		Result messageResult = new StreamResult(bos);
		Source mappingSource = new SAXSource(new InputSource(mappingStream));
		
		//initialize the transformer
		Transformer transformer = TransformerFactory.newInstance(factoryEngine,
				getClass().getClassLoader()).newTransformer(mappingSource);
	
		Iterator<String> it = queryParams.keySet().iterator();
        
		while(it.hasNext()){
        	String theKey = (String) it.next();
		    transformer.setParameter(theKey, queryParams.getFirst(theKey));
		}

		transformer.transform(messageSource, messageResult);
		
		//write back the outputstream
		holder.setOutputStream(bos);
	}
}
