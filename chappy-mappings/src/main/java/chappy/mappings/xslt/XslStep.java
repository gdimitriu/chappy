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
import java.util.Collection;
import java.util.Iterator;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import chappy.interfaces.flows.MultiDataQueryHolder;
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
	
	public void setMappingStream(final MultiDataQueryHolder dataHolder) throws Exception {
		Collection<InputStream> data = dataHolder.getBodyValueAs(mappingName, InputStream.class);
		if (data == null || data.isEmpty()) {
			throw new Exception("mapping rule " + mappingName + " is not present in formData request");
		}
		mappingStream =  data.iterator().next();
		if (mappingStream == null) {
			throw new Exception("mapping rule " + mappingName + " is not present in formData request");
		}
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transformers.AbstractStep#execute(chappy.utils.streams.wrappers.StreamHolder, chappy.interfaces.flows.MultiDataQueryHolder)
	 */
	@Override
	public void execute(StreamHolder holder,
			final MultiDataQueryHolder dataHolder)
					throws Exception {
		setMappingStream(dataHolder);
		Source messageSource = new SAXSource(new InputSource(holder.getInputStream()));
		ByteArrayOutputStreamWrapper bos = new ByteArrayOutputStreamWrapper();
		Result messageResult = new StreamResult(bos);
		Source mappingSource = new SAXSource(new InputSource(mappingStream));
		
		//initialize the transformer
		Transformer transformer = TransformerFactory.newInstance(factoryEngine,
				getClass().getClassLoader()).newTransformer(mappingSource);
	
		Iterator<String> it = dataHolder.getQueriesSet().iterator();
        
		while(it.hasNext()){
        	String theKey = (String) it.next();
		    transformer.setParameter(theKey, dataHolder.getFirstQuery(theKey));
		}

		transformer.transform(messageSource, messageResult);
		
		//write back the outputstream
		holder.setOutputStream(bos);
	}
}
